package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.ViolationRecord;
import com.library.entity.User;
import com.library.mapper.ViolationRecordMapper;
import com.library.service.UserService;
import com.library.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 违规记录Service实现
 */
@Service
public class ViolationRecordServiceImpl extends ServiceImpl<ViolationRecordMapper, ViolationRecord> implements ViolationRecordService {
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional
    public ViolationRecord createViolation(ViolationRecord record) {
        this.save(record);
        return record;
    }
    
    @Override
    @Transactional
    public void handleViolation(Long id, Integer pointsDeduct, Integer banDays, String reply, Long handlerId, String handlerName) {
        ViolationRecord record = this.getById(id);
        if (record == null || !"待处理".equals(record.getStatus())) {
            throw new RuntimeException("记录不存在或已处理");
        }
        
        record.setPointsDeducted(pointsDeduct);
        record.setBanDays(banDays);
        record.setHandleReply(reply);
        record.setHandledBy(handlerId);
        record.setHandledByName(handlerName);
        record.setStatus("已处理");
        record.setHandleTime(LocalDateTime.now());
        
        this.updateById(record);
        
        // 执行扣分和封禁
        User user = userService.getById(record.getUserId());
        if (user != null && user.getStatus() != null) {
            // 封禁用户：设置封禁截止时间
            if (banDays != null && banDays > 0) {
                user.setStatus("封禁中_" + LocalDateTime.now().plusDays(banDays).toString().replace("T", " ").substring(0, 19));
                userService.updateById(user);
            }
        }
    }
    
    @Override
    @Transactional
    public void exemptViolation(Long id, String reply, Long handlerId, String handlerName) {
        ViolationRecord record = this.getById(id);
        if (record == null || !"待处理".equals(record.getStatus())) {
            throw new RuntimeException("记录不存在或已处理");
        }
        
        record.setHandleReply(reply);
        record.setHandledBy(handlerId);
        record.setHandledByName(handlerName);
        record.setStatus("已豁免");
        record.setHandleTime(LocalDateTime.now());
        
        this.updateById(record);
    }
    
    @Override
    public long countHandledViolations(Long userId, String violationType) {
        LambdaQueryWrapper<ViolationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ViolationRecord::getUserId, userId)
               .eq(ViolationRecord::getViolationType, violationType)
               .in(ViolationRecord::getStatus, "已处理", "已豁免");
        return this.count(wrapper);
    }
}
