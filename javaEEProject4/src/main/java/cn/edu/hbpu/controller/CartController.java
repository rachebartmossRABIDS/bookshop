package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public Result add(Integer bid, Integer num) {
        cartService.add(bid, num);
        return Result.success("已加入购物车", null);
    }

    @PostMapping("/updateNum")
    public Result updateNum(Integer cid, Integer num) {
        int result = cartService.updateNum(cid, num);
        return result > 0 ? Result.success() : Result.fail("修改数量失败");
    }

    @PostMapping("/remove")
    public Result remove(Integer cid) {
        int result = cartService.remove(cid);
        return result > 0 ? Result.success("已移除", null) : Result.fail("移除失败");
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(cartService.list());
    }

    @PostMapping("/checkout")
    public Result checkout(String name, String tel, String addr, String requestId) {
        return Result.success("下单成功，请完成付款",
                cartService.checkout(name, tel, addr, requestId));
    }
}
