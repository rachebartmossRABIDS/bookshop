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
}
