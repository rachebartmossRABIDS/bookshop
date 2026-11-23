package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Notification;
import com.github.pagehelper.PageInfo;

public interface NotificationService {
    void send(Integer uid, String title, String content);
    PageInfo<Notification> list(Integer pageNo, Integer pageSize);
    int markRead(Integer nid);
    int unreadCount();
}
