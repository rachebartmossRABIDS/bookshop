package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.mapper.OrderMapper;
import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    @Override
    public List<Order> selectByBid(Integer bid) {
        return orderMapper.selectByBid(bid);
    }

    @Override
    public Boolean save(Order order) {
        order.setTime(new Date());
        if (order.getNum() == null) {
            order.setNum(1);
        }
        if (order.getState() == null || order.getState().isEmpty()) {
            order.setState("1");
        }
        return orderMapper.insertSelective(order)==1?true:false;
    }

    @Override
    public PageInfo<Order> selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        //调用PageHelper执行分页
        PageHelper.startPage(pageNo,pageSize);//pageNo页码 pageSize每页显示的记录数
        List<Order> orders = orderMapper.selectByUid(uid);
        //查询结果封装成PageInfo对象
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        return pageInfo;
    }

    @Override
    public int remove(Integer oid) {
        return orderMapper.deleteByPrimaryKey(Long.parseLong(oid+""));
    }
}
