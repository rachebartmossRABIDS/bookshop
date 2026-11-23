package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Address;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/list")
    public Result list() {
        return Result.success(addressService.list());
    }

    @PostMapping("/save")
    public Result save(Address address) {
        return Result.success("保存成功", addressService.save(address));
    }

    @PostMapping("/remove")
    public Result remove(Integer aid) {
        int result = addressService.remove(aid);
        return result > 0 ? Result.success("删除成功", null) : Result.fail("删除地址失败");
    }

    @PostMapping("/setDefault")
    public Result setDefault(Integer aid) {
        addressService.setDefault(aid);
        return Result.success("已设为默认地址", null);
    }
}
