package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.mapper.OrderMapper;
import cn.edu.hbpu.mapper.OrderRequestMapper;
import cn.edu.hbpu.mapper.UserMapper;
import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.pojo.Payment;
import cn.edu.hbpu.service.OrderService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.service.PaymentService;
import cn.edu.hbpu.service.NotificationService;
import cn.edu.hbpu.service.CacheService;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final OperationLogService operationLogService;
    private final OrderRequestMapper orderRequestMapper;
    private final CacheService cacheService;

    /** 单笔订单数量上限 */
    private static final int MAX_NUM_PER_ORDER = 999;

    @Override
    @Transactional
    public Boolean save(Order order) {
        // 下单人取当前登录用户，不信任客户端传入的 uid / state
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        if (order.getBid() == null) {
            throw new BusinessException("请选择图书");
        }
        if (order.getNum() == null || order.getNum() <= 0) {
            order.setNum(1);
        }
        if (order.getNum() > MAX_NUM_PER_ORDER) {
            throw new BusinessException("单笔订单数量不能超过 " + MAX_NUM_PER_ORDER);
        }
        // 收件信息服务端校验（与前端规则一致，不信任客户端）
        if (ValidateUtil.isBlank(order.getName()) || order.getName().trim().length() > 100) {
            throw new BusinessException("请填写收件人（不超过 100 字）");
        }
        if (!ValidateUtil.isTel(order.getTel() == null ? null : order.getTel().trim())) {
            throw new BusinessException("请填写正确的 11 位手机号");
        }
        if (ValidateUtil.isBlank(order.getAddr()) || order.getAddr().trim().length() > 500) {
            throw new BusinessException("请填写收件地址（不超过 500 字）");
        }
        Book book = bookMapper.getByBidPrimary(order.getBid());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getPrice() == null) {
            throw new BusinessException("图书价格异常，无法下单");
        }
        order.setUid(uid);
        order.setName(order.getName().trim());
        order.setTel(order.getTel().trim());
        order.setAddr(order.getAddr().trim());
        order.setTime(new Date());
        // 新订单统一为待付款，金额按下单时单价快照
        order.setState(Order.STATE_UNPAID);
        order.setAmount(book.getPrice().multiply(BigDecimal.valueOf(order.getNum())));
        // 原子扣减库存（下单即占用），库存不足直接失败（事务回滚）
        if (bookMapper.deductInventory(order.getBid(), order.getNum()) == 0) {
            throw new BusinessException("图书库存不足");
        }
        if (orderMapper.insertSelective(order) != 1) {
            throw new BusinessException("创建订单失败");
        }
        cacheService.evict("book:detail:" + order.getBid());
        return true;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> saveIdempotent(Order order, String requestId) {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        if (requestId == null || requestId.trim().isEmpty()) {
            save(order);
            Map<String, Object> legacy = new LinkedHashMap<>();
            legacy.put("oid", order.getOid());
            return legacy;
        }
        String normalized = requestId.trim();
        if (normalized.length() > 64) {
            throw new BusinessException("requestId 长度不能超过 64 字符");
        }
        if (orderRequestMapper.tryInsert(normalized, uid) == 0) {
            String json = waitForResult(normalized, uid);
            if (json == null || json.trim().isEmpty()) {
                throw new BusinessException("相同请求正在处理中，请稍后重试");
            }
            try {
                return JSON.readValue(json, new TypeReference<Map<String, Object>>() { });
            } catch (Exception e) {
                throw new BusinessException("读取下单结果失败");
            }
        }
        save(order);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("oid", order.getOid());
        try {
            orderRequestMapper.saveResult(normalized, uid, JSON.writeValueAsString(result));
        } catch (Exception e) {
            throw new BusinessException("保存下单结果失败");
        }
        return result;
    }

    private String waitForResult(String requestId, Integer uid) {
        String json = null;
        for (int i = 0; i < 20; i++) {
            json = orderRequestMapper.selectResult(requestId, uid);
            if (json != null) {
                return json;
            }
            try {
                Thread.sleep(25L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return json;
    }

    @Override
    public PageInfo<Order> selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        checkSelfOrAdmin(uid, "无权查看他人的订单");
        //调用PageHelper执行分页
        PageHelper.startPage(pageNo, pageSize);//pageNo页码 pageSize每页显示的记录数
        List<Order> orders = orderMapper.selectByUid(uid);
        //查询结果封装成PageInfo对象
        return new PageInfo<>(orders);
    }

    @Override
    @Transactional
    public Payment pay(Integer oid, String channel) {
        Order order = requireOrder(oid);
        checkOwner(order, "只能支付本人的订单");
        // 原子流转：待付款 -> 已付款
        if (orderMapper.updateState(oid, Order.STATE_UNPAID, Order.STATE_PAID) != 1) {
            throw new BusinessException("订单不是待付款状态，请刷新后重试");
        }
        // 付款成功计入销量
        bookMapper.addVolume(order.getBid(), order.getNum() == null ? 1 : order.getNum());
        Payment payment = paymentService.pay(order, channel);
        cacheService.evict("book:detail:" + order.getBid());
        notificationService.send(order.getUid(), "付款成功",
                "订单 #" + order.getOid() + " 已付款，金额 ￥" + order.getAmount());
        return payment;
    }

    @Override
    @Transactional
    public void refund(Integer oid) {
        Order order = requireOrder(oid);
        checkSelfOrAdmin(order.getUid(), "无权申请他人订单的退款");
        if (orderMapper.updateState(oid, Order.STATE_PAID, Order.STATE_REFUNDED) != 1) {
            throw new BusinessException("仅已付款且未发货的订单可退款");
        }
        int num = order.getNum() == null ? 1 : order.getNum();
        bookMapper.restoreInventory(order.getBid(), num);
        bookMapper.subtractVolume(order.getBid(), num);
        paymentService.markRefunded(oid);
        cacheService.evict("book:detail:" + order.getBid());
        notificationService.send(order.getUid(), "退款成功",
                "订单 #" + order.getOid() + " 已退款，库存与销量已恢复");
    }

    @Override
    @Transactional
    public void cancel(Integer oid) {
        Order order = requireOrder(oid);
        checkSelfOrAdmin(order.getUid(), "无权取消他人的订单");
        String state = order.getState();
        if (!Order.STATE_UNPAID.equals(state) && !Order.STATE_PAID.equals(state)) {
            throw new BusinessException("订单已发货或已结束，不能取消");
        }
        // 原子流转，防止并发下重复取消/重复回补
        if (orderMapper.updateState(oid, state, Order.STATE_CANCELLED) != 1) {
            throw new BusinessException("订单状态已变化，请刷新后重试");
        }
        // 取消回补下单时占用的库存
        bookMapper.restoreInventory(order.getBid(), order.getNum() == null ? 1 : order.getNum());
        if (Order.STATE_PAID.equals(state)) {
            // 已付款订单取消（模拟退款），回滚销量
            bookMapper.subtractVolume(order.getBid(), order.getNum() == null ? 1 : order.getNum());
            paymentService.markRefunded(oid);
        }
        notificationService.send(order.getUid(), "订单已取消",
                "订单 #" + order.getOid() + " 已取消，占用库存已恢复");
        cacheService.evict("book:detail:" + order.getBid());
    }

    @Override
    public void ship(Integer oid, String logisticsCompany, String logisticsNo) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("仅管理员可发货");
        }
        Order order = requireOrder(oid);
        String company = logisticsCompany == null ? "" : logisticsCompany.trim();
        String no = logisticsNo == null ? "" : logisticsNo.trim();
        if (company.length() > 100 || no.length() > 100) {
            throw new BusinessException("物流公司与单号均不能超过 100 字");
        }
        if (orderMapper.ship(oid, company, no) != 1) {
            throw new BusinessException("仅已付款订单可发货，请刷新后重试");
        }
        operationLogService.log("订单发货", "订单 #" + oid + "，物流：" + company + " " + no);
        notificationService.send(order.getUid(), "订单已发货",
                "订单 #" + order.getOid() + " 已发货"
                        + (company.isEmpty() ? "" : "，物流公司：" + company)
                        + (no.isEmpty() ? "" : "，单号：" + no));
    }

    @Override
    public void confirm(Integer oid) {
        Order order = requireOrder(oid);
        checkOwner(order, "只能确认收货本人的订单");
        if (orderMapper.updateState(oid, Order.STATE_SHIPPED, Order.STATE_RECEIVED) != 1) {
            throw new BusinessException("订单不是已发货状态，请刷新后重试");
        }
    }

    @Override
    public int remove(Integer oid) {
        Order order = requireOrder(oid);
        checkSelfOrAdmin(order.getUid(), "无权删除他人的订单");
        String state = order.getState();
        // 仅终态订单可删除；进行中订单应先取消，避免错误回补库存
        if (!Order.STATE_RECEIVED.equals(state)
                && !Order.STATE_COMPLETED.equals(state)
                && !Order.STATE_CANCELLED.equals(state)
                && !Order.STATE_REFUNDED.equals(state)) {
            throw new BusinessException("进行中的订单不能删除，请先取消或完成订单");
        }
        return orderMapper.deleteByPrimaryKey(oid.longValue());
    }

    @Override
    public PageInfo<Order> listAll(Integer pageNo, Integer pageSize) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
        PageHelper.startPage(pageNo, pageSize);
        List<Order> orders = orderMapper.selectAll();
        return new PageInfo<>(orders);
    }

    @Override
    public Order getDetail(Integer oid) {
        Order order = requireOrder(oid);
        checkSelfOrAdmin(order.getUid(), "无权查看他人的订单详情");
        order.setBook(bookMapper.getByBidPrimary(order.getBid()));
        order.setUser(userMapper.selectPublicByPrimaryKey(order.getUid().longValue()));
        return order;
    }

    private Order requireOrder(Integer oid) {
        if (oid == null) {
            throw new BusinessException("订单编号不能为空");
        }
        Order order = orderMapper.selectByPrimaryKey(oid.longValue());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private void checkOwner(Order order, String message) {
        Integer currentUid = UserContext.getUid();
        if (currentUid == null || !currentUid.equals(order.getUid())) {
            throw new BusinessException(message);
        }
    }

    private void checkSelfOrAdmin(Integer targetUid, String message) {
        Integer currentUid = UserContext.getUid();
        if (UserContext.isAdmin()) {
            return;
        }
        if (currentUid == null || !currentUid.equals(targetUid)) {
            throw new BusinessException(message);
        }
    }
}
