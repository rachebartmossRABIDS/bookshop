package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.AdminStatsService;
import cn.edu.hbpu.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminStatsService adminStatsService;
    private final OperationLogService operationLogService;

    @GetMapping("/stats")
    public Result stats() {
        return Result.success(adminStatsService.stats());
    }

    @GetMapping("/logs")
    public Result logs(Integer pageNo, Integer pageSize) {
        return Result.success(operationLogService.list(pageNo, pageSize));
    }
}
