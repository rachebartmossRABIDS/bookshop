package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.mapper.CartMapper;
import cn.edu.hbpu.mapper.OrderRequestMapper;
import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.pojo.Cart;
import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.service.CartService;
import cn.edu.hbpu.service.OrderService;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;
    private final OrderService orderService;
    private final OrderRequestMapper orderRequestMapper;

    /** 单种图书在购物车中的数量上限（与单笔订单上限一致） */
    private static final int MAX_NUM = 999;

    @Override
    @Transactional
    public Boolean add(Integer bid, Integer num) {
        Integer uid = requireLogin();
        if (bid == null) {
            throw new BusinessException("请选择图书");
        }
        int addNum = (num == null || num <= 0) ? 1 : num;
        Book book = requireOnSaleBook(bid);
        Cart existing = cartMapper.selectByUidBid(uid, bid);
        int total = (existing == null ? 0 : existing.getNum()) + addNum;
        if (total > MAX_NUM) {
            throw new BusinessException("单种图书数量不能超过 " + MAX_NUM);
        }
        if (book.getInventory() == null || total > book.getInventory()) {
            throw new BusinessException("超出图书剩余库存");
        }
        if (existing != null) {
            return cartMapper.updateNum(existing.getCid(), total) == 1;
        }
        Cart cart = new Cart();
        cart.setUid(uid);
        cart.setBid(bid);
        cart.setNum(addNum);
        cart.setTime(new Date());
        return cartMapper.insert(cart) == 1;
    }

    @Override
    @Transactional
    public int updateNum(Integer cid, Integer num) {
        Cart cart = requireOwnedCart(cid);
        if (num == null || num <= 0) {
            throw new BusinessException("数量至少为 1");
        }
        if (num > MAX_NUM) {
            throw new BusinessException("单种图书数量不能超过 " + MAX_NUM);
        }
        Book book = bookMapper.getWithStock(cart.getBid());
        if (book != null && book.getInventory() != null && num > book.getInventory()) {
            throw new BusinessException("超出图书剩余库存");
        }
        return cartMapper.updateNum(cid, num);
    }

    @Override
    @Transactional
    public int remove(Integer cid) {
        requireOwnedCart(cid);
        return cartMapper.deleteByCid(cid);
    }

    @Override
    public List<Cart> list() {
        return cartMapper.selectByUid(requireLogin());
    }

    @Override
    @Transactional
    public Boolean checkout(String name, String tel, String addr) {
        checkout(name, tel, addr, null);
        return true;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> checkout(String name, String tel, String addr, String requestId) {
        Integer uid = requireLogin();
        // 收件信息校验与下单一致
        if (ValidateUtil.isBlank(name) || name.trim().length() > 100) {
            throw new BusinessException("请填写收件人（不超过 100 字）");
        }
        if (!ValidateUtil.isTel(tel == null ? null : tel.trim())) {
            throw new BusinessException("请填写正确的 11 位手机号");
        }
        if (ValidateUtil.isBlank(addr) || addr.trim().length() > 500) {
            throw new BusinessException("请填写收件地址（不超过 500 字）");
        }
        String normalized = requestId == null ? "" : requestId.trim();
        if (normalized.length() > 64) {
            throw new BusinessException("requestId 长度不能超过 64 字符");
        }
        if (!normalized.isEmpty() && orderRequestMapper.tryInsert(normalized, uid) == 0) {
            String json = waitForResult(normalized, uid);
            if (json == null || json.trim().isEmpty()) {
                throw new BusinessException("相同请求正在处理中，请稍后重试");
            }
            try {
                return JSON.readValue(json, new TypeReference<Map<String, Object>>() { });
            } catch (Exception e) {
                throw new BusinessException("读取结算结果失败");
            }
        }
        List<Cart> items = cartMapper.selectByUid(uid);
        if (items.isEmpty()) {
            throw new BusinessException("购物车为空");
        }
        // 逐条复用下单逻辑（校验上架状态、原子扣减库存、金额快照）；
        // 任一条失败则整个事务回滚，购物车保持不变
        List<Integer> orderIds = new ArrayList<>();
        for (Cart item : items) {
            Book book = bookMapper.getWithStock(item.getBid());
            if (book == null) {
                throw new BusinessException("购物车中有图书已不存在，请移除后再结算");
            }
            if (book.getState() != null && book.getState() != 0) {
                throw new BusinessException("《" + book.getName() + "》已下架，请移除后再结算");
            }
            Order order = new Order();
            order.setBid(item.getBid());
            order.setNum(item.getNum());
            order.setName(name.trim());
            order.setTel(tel.trim());
            order.setAddr(addr.trim());
            orderService.save(order);
            orderIds.add(order.getOid());
        }
        cartMapper.deleteByUid(uid);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderIds", orderIds);
        if (!normalized.isEmpty()) {
            try {
                orderRequestMapper.saveResult(normalized, uid, JSON.writeValueAsString(result));
            } catch (Exception e) {
                throw new BusinessException("保存结算结果失败");
            }
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

    private Book requireOnSaleBook(Integer bid) {
        Book book = bookMapper.getWithStock(bid);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getState() != null && book.getState() != 0) {
            throw new BusinessException("图书已下架，无法加入购物车");
        }
        return book;
    }

    private Cart requireOwnedCart(Integer cid) {
        Integer uid = requireLogin();
        if (cid == null) {
            throw new BusinessException("购物车编号不能为空");
        }
        Cart cart = cartMapper.selectByCid(cid);
        if (cart == null) {
            throw new BusinessException("购物车记录不存在");
        }
        if (!uid.equals(cart.getUid())) {
            throw new BusinessException("无权操作他人的购物车");
        }
        return cart;
    }

    private Integer requireLogin() {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        return uid;
    }
}
