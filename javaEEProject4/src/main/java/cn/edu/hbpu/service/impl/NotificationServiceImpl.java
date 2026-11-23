package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.NotificationMapper;
import cn.edu.hbpu.pojo.Notification;
import cn.edu.hbpu.service.NotificationService;
import cn.edu.hbpu.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;

    @Override
    public void send(Integer uid, String title, String content) {
        if (uid == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setTime(new Date());
        notificationMapper.insert(notification);
    }

    @Override
    public PageInfo<Notification> list(Integer pageNo, Integer pageSize) {
        Integer uid = requireLogin();
        PageHelper.startPage(pageNo, pageSize);
        List<Notification> list = notificationMapper.selectByUid(uid);
        return new PageInfo<>(list);
    }

    @Override
    public int markRead(Integer nid) {
        if (nid == null) {
            throw new BusinessException("通知编号不能为空");
        }
        int result = notificationMapper.markRead(nid, requireLogin());
        if (result != 1) {
            throw new BusinessException("通知不存在或无权操作");
        }
        return result;
    }

    @Override
    public int unreadCount() {
        return notificationMapper.countUnread(requireLogin());
    }

    private Integer requireLogin() {
        Integer uid = UserContext.getUid();
        if (uid == null) {
            throw new BusinessException("请先登录");
        }
        return uid;
    }
}
