package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.OperationLog;
import java.util.Map;

/**
 * 操作日志Service
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作日志
     */
    void log(OperationLog operationLog);
    
    /**
     * 分页查询操作日志
     */
    com.baomidou.mybatisplus.core.metadata.IPage<OperationLog> getLogPage(
        int current, int size, String operateUser, String operateType, 
        String startTime, String endTime);
}
