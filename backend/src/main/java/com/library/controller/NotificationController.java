package com.library.controller;

import com.library.entity.Notification;
import com.library.entity.User;
import com.library.service.NotificationService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 通知消息控制器
 */
@RestController
@RequestMapping("/notification")
@CrossOrigin
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 分页查询用户通知
     */
    @GetMapping("/list")
    public Result<PageResult<Notification>> getNotificationList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        PageResult<Notification> result = notificationService.getUserNotifications(user.getId(), current, size);
        return Result.success(result);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        long count = notificationService.getUnreadCount(user.getId());
        return Result.success(count);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<Object> markAsRead(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        notificationService.markAsRead(id, user.getId());
        return Result.success("操作成功");
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Object> markAllAsRead(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        notificationService.markAllAsRead(user.getId());
        return Result.success("操作成功");
    }
}
