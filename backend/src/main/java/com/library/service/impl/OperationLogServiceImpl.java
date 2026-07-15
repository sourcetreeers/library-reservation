package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.OperationLog;
import com.library.mapper.OperationLogMapper;
import com.library.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志Service实现
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Override
    public void log(OperationLog operationLog) {
        this.save(operationLog);
    }
    
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<OperationLog> getLogPage(
            int current, int size, String operateUser, String operateType,
            String startTime, String endTime) {
        
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (operateUser != null && !operateUser.isEmpty()) {
            wrapper.like(OperationLog::getOperateUser, operateUser);
        }
        if (operateType != null && !operateType.isEmpty()) {
            wrapper.eq(OperationLog::getOperateType, operateType);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(OperationLog::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(OperationLog::getCreateTime, endTime + " 23:59:59");
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        return this.page(page, wrapper);
    }
}
