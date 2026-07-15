package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.ViolationRecord;

/**
 * 违规记录Service
 */
public interface ViolationRecordService extends IService<ViolationRecord> {
    
    /**
     * 创建违规记录
     */
    ViolationRecord createViolation(ViolationRecord record);
    
    /**
     * 处理违规（执行处罚）
     */
    void handleViolation(Long id, Integer pointsDeduct, Integer banDays, String reply, Long handlerId, String handlerName);
    
    /**
     * 豁免违规
     */
    void exemptViolation(Long id, String reply, Long handlerId, String handlerName);
    
    /**
     * 统计用户某类型违规已处理+豁免的数量
     */
    long countHandledViolations(Long userId, String violationType);
}
