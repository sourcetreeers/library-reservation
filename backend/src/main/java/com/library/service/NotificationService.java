package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Notification;
import com.library.vo.PageResult;

/**
 * 通知消息服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 创建通知
     */
    void createNotification(Long userId, String title, String content, String type, String linkUrl);

    /**
     * 分页查询用户通知
     */
    PageResult<Notification> getUserNotifications(Long userId, int current, int size);

    /**
     * 获取未读通知数量
     */
    long getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);
}
