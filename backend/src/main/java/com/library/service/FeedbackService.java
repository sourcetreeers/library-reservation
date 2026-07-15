package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Feedback;

/**
 * 反馈Service
 */
public interface FeedbackService extends IService<Feedback> {
    
    /**
     * 学生提交反馈
     */
    Feedback submitFeedback(Feedback feedback);
    
    /**
     * 管理员回复反馈
     */
    void replyFeedback(Long id, String reply, String handleResult, Long handlerId, String handlerName);
}
