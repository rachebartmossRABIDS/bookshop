package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Order;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/listByBid")
    public Result listByBid(Integer bid){
        List<Order> orders=orderService.selectByBid(bid);
        return Result.success(orders);
    }
    @PostMapping("/save")
    public Result save(Order order){
        Boolean result=orderService.save(order);
        return result ? Result.success() : Result.fail("发表订单失败");
    }
    @GetMapping("/listByUid")
    public Result selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        PageInfo<Order> pageInfo = orderService.selectByUid(uid, pageNo, pageSize);
        return Result.success(pageInfo);
    }
    @PostMapping("/remove")
    public Result remove(Integer oid){
        int result=orderService.remove(oid);
        return result>0?Result.success():Result.fail("删除订单失败");
    }
}
