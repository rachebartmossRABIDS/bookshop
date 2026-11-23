package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Kind;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.KindService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kind")
@RequiredArgsConstructor
public class KindController {
    private final KindService kindService;

    @RequestMapping("/list")
    public Result list(){
        List<Kind> kinds = kindService.listAll();
        return Result.success(kinds);
    }

    @PostMapping("/save")
    public Result save(Kind kind) {
        Boolean result = kindService.save(kind);
        return result ? Result.success("保存成功", null) : Result.fail("保存分类失败");
    }

    @PostMapping("/remove")
    public Result remove(Integer kid) {
        int result = kindService.remove(kid);
        return result > 0 ? Result.success("删除成功", null) : Result.fail("删除分类失败");
    }
}