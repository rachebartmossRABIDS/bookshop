package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result list(Integer pageNo, Integer pageSize) {
        return Result.success(notificationService.list(pageNo, pageSize));
    }

    @PostMapping("/read")
    public Result read(Integer nid) {
        notificationService.markRead(nid);
        return Result.success();
    }

    @GetMapping("/unreadCount")
    public Result unreadCount() {
        return Result.success(notificationService.unreadCount());
    }
}
