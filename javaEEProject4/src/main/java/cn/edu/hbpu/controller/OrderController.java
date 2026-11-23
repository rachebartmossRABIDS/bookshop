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

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/save")
    public Result save(Order order, String requestId){
        return Result.success("下单成功，请完成付款",
                orderService.saveIdempotent(order, requestId));
    }
    @GetMapping("/listByUid")
    public Result selectByUid(Integer uid, Integer pageNo, Integer pageSize) {
        PageInfo<Order> pageInfo = orderService.selectByUid(uid, pageNo, pageSize);
        return Result.success(pageInfo);
    }
    @PostMapping("/pay")
    public Result pay(Integer oid, String channel){
        return Result.success("付款成功", orderService.pay(oid, channel));
    }
    @PostMapping("/refund")
    public Result refund(Integer oid){
        orderService.refund(oid);
        return Result.success("退款成功", null);
    }
    @PostMapping("/cancel")
    public Result cancel(Integer oid){
        orderService.cancel(oid);
        return Result.success("订单已取消", null);
    }
    @PostMapping("/ship")
    public Result ship(Integer oid, String logisticsCompany, String logisticsNo){
        orderService.ship(oid, logisticsCompany, logisticsNo);
        return Result.success("发货成功", null);
    }
    @PostMapping("/confirm")
    public Result confirm(Integer oid){
        orderService.confirm(oid);
        return Result.success("已确认收货", null);
    }
    @PostMapping("/remove")
    public Result remove(Integer oid){
        int result=orderService.remove(oid);
        return result>0?Result.success():Result.fail("删除订单失败");
    }
    @GetMapping("/listAll")
    public Result listAll(Integer pageNo, Integer pageSize) {
        PageInfo<Order> pageInfo = orderService.listAll(pageNo, pageSize);
        return Result.success(pageInfo);
    }
    @GetMapping("/detail")
    public Result detail(Integer oid) {
        return Result.success(orderService.getDetail(oid));
    }
}
