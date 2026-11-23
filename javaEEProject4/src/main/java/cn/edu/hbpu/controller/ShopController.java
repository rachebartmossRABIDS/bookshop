package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.pojo.Shop;
import cn.edu.hbpu.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    @GetMapping("/list/{kid}")
    public Result list(@PathVariable Integer kid){
        List<Shop> shops = shopService.listByKid(kid);
        return Result.success(shops);
    }
    @GetMapping("/sdetail")
    public Result sdetail(Integer sid){
        Shop shop=shopService.getBySid(sid);
        return Result.success(shop);
    }

    @GetMapping("/listAll")
    public Result listAll(){
        return Result.success(shopService.listAll());
    }

    @PostMapping("/save")
    public Result save(Shop shop) {
        Boolean result = shopService.save(shop);
        return result ? Result.success("保存成功", null) : Result.fail("保存店铺失败");
    }

    @PostMapping("/remove")
    public Result remove(Integer sid) {
        int result = shopService.remove(sid);
        return result > 0 ? Result.success("删除成功", null) : Result.fail("删除店铺失败");
    }
}
