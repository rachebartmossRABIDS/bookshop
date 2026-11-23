package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Cart;

import java.util.List;
import java.util.Map;

public interface CartService {
    /** 加入购物车（当前登录用户），同一图书累加数量 */
    Boolean add(Integer bid, Integer num);

    /** 修改购物车条目数量（仅本人） */
    int updateNum(Integer cid, Integer num);

    /** 移除购物车条目（仅本人） */
    int remove(Integer cid);

    /** 当前登录用户的购物车列表（嵌套图书含库存/状态） */
    List<Cart> list();

    /** 结算：事务内为全部条目批量下单（复用订单校验与库存扣减），成功后清空购物车 */
    Boolean checkout(String name, String tel, String addr);
    Map<String, Object> checkout(String name, String tel, String addr, String requestId);
}
