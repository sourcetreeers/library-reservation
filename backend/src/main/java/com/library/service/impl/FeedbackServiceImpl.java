package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Feedback;
import com.library.mapper.FeedbackMapper;
import com.library.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 反馈Service实现
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    
    @Override
    public Feedback submitFeedback(Feedback feedback) {
        feedback.setStatus("待处理");
        feedback.setCreateTime(LocalDateTime.now());
        this.save(feedback);
        return feedback;
    }
    
    @Override
    public void replyFeedback(Long id, String reply, String handleResult, Long handlerId, String handlerName) {
        Feedback feedback = this.getById(id);
        if (feedback == null) {
            throw new RuntimeException("反馈不存在");
        }
        
        feedback.setReply(reply);
        feedback.setHandleResult(handleResult);
        feedback.setHandlerId(handlerId);
        feedback.setHandlerName(handlerName);
        feedback.setStatus("已回复");
        feedback.setHandleTime(LocalDateTime.now());
        
        this.updateById(feedback);
    }
}
