package cn.edu.hbpu.service;


import cn.edu.hbpu.pojo.Order;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface OrderService {
    List<Order> selectByBid(Integer bid);
    Boolean save(Order order);
    PageInfo<Order> selectByUid(Integer uid, Integer pageNo, Integer pageSize);
    int remove (Integer oid);
}
