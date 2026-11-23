package cn.edu.hbpu.service;


import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.pojo.Payment;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface OrderService {
    Boolean save(Order order);
    /** 支持 requestId 的下单；同一用户重复 requestId 返回首次结果 */
    Map<String, Object> saveIdempotent(Order order, String requestId);
    PageInfo<Order> selectByUid(Integer uid, Integer pageNo, Integer pageSize);
    /** 沙箱付款：待付款 -> 已付款，写入支付记录并增加图书销量 */
    Payment pay(Integer oid, String channel);
    /** 已付款且未发货订单退款：1 -> 6，回补库存并回滚销量 */
    void refund(Integer oid);
    /** 取消订单：待付款/已付款 -> 已取消，回补库存（已付款的同时回滚销量） */
    void cancel(Integer oid);
    /** 管理员发货：已付款 -> 已发货，可记录物流信息 */
    void ship(Integer oid, String logisticsCompany, String logisticsNo);
    /** 本人或管理员查看订单详情（含图书与买家公开信息） */
    Order getDetail(Integer oid);
    /** 买家确认收货：已发货 -> 已收货（终态） */
    void confirm(Integer oid);
    /** 删除订单：仅限终态（已收货/交易成功/已取消），不再回补库存 */
    int remove(Integer oid);
    /** 管理员分页查询全部订单（嵌套图书与买家公开信息） */
    PageInfo<Order> listAll(Integer pageNo, Integer pageSize);
}
