package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Feedback;
import com.library.entity.User;
import com.library.entity.Notification;
import com.library.service.FeedbackService;
import com.library.service.NotificationService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 反馈管理控制器
 */
@RestController
@RequestMapping({"/admin/feedback", "/mobile/feedback"})
@CrossOrigin
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private NotificationService notificationService;
    
    // ==================== 管理员接口 ====================
    
    /**
     * 管理员 - 分页查询反馈列表
     */
    @GetMapping("/page")
    public Result<Page<Feedback>> getAdminPage(
            HttpSession session,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        Page<Feedback> page = new Page<>(current, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Feedback::getType, type);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Feedback::getStatus, status);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(Feedback::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(Feedback::getCreateTime, endTime + " 23:59:59");
        }
        
        wrapper.orderByDesc(Feedback::getCreateTime);
        
        Page<Feedback> result = feedbackService.page(page, wrapper);
        return Result.success(result);
    }
    
    /**
     * 管理员 - 回复反馈
     */
    @PutMapping("/{id}/reply")
    public Result<Object> replyFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        String reply = params.get("reply") != null ? (String) params.get("reply") : "";
        String handleResult = params.get("handleResult") != null ? (String) params.get("handleResult") : "已采纳";
        
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        
        feedbackService.replyFeedback(id, reply, handleResult, user.getId(), user.getRealName());
        
        // 发送通知给提交人
        Notification notification = new Notification();
        notification.setUserId(feedback.getUserId());
        notification.setTitle("您的反馈已回复");
        notification.setContent("您提交的《" + feedback.getTitle() + "》已收到回复，请查看。");
        notification.setType("反馈通知");
        notification.setLinkUrl("/mobile/my-feedbacks");
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notificationService.save(notification);
        
        return Result.success("回复成功");
    }
    
    // ==================== 学生接口 ====================
    
    /**
     * 学生 - 提交反馈
     */
    @PostMapping("/submit")
    public Result<Object> submitFeedback(@RequestBody Feedback feedback, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"学生".equals(user.getUserType())) return Result.error(403, "仅学生可提交反馈");
        
        feedback.setUserId(user.getId());
        feedback.setUserName(user.getRealName());
        
        Feedback saved = feedbackService.submitFeedback(feedback);
        return Result.success(saved);
    }
    
    /**
     * 学生 - 查看我的反馈列表
     */
    @GetMapping("/my")
    public Result<Page<Feedback>> getMyFeedbacks(
            HttpSession session,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        
        Page<Feedback> page = new Page<>(current, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, user.getId())
               .orderByDesc(Feedback::getCreateTime);
        
        Page<Feedback> result = feedbackService.page(page, wrapper);
        return Result.success(result);
    }
}
