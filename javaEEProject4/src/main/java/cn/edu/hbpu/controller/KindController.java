package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Kind;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.KindService;
import lombok.RequiredArgsConstructor;
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
}

