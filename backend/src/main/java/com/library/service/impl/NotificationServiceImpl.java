package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Notification;
import com.library.mapper.NotificationMapper;
import com.library.service.NotificationService;
import com.library.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知消息服务实现类
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void createNotification(Long userId, String title, String content, String type, String linkUrl) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setLinkUrl(linkUrl);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public PageResult<Notification> getUserNotifications(Long userId, int current, int size) {
        int offset = (current - 1) * size;

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.orderByDesc(Notification::getCreateTime);

        List<Notification> records = notificationMapper.selectList(
                wrapper.last("LIMIT " + offset + ", " + size)
        );

        long total = notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
        );

        return new PageResult<>(records, total, current, size);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }
}
