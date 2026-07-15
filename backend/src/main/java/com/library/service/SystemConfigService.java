package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.SystemConfig;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService extends IService<SystemConfig> {
    
    /**
     * 根据配置键获取配置
     */
    SystemConfig getByKey(String configKey);
    
    /**
     * 重置每日流水号
     */
    void resetDailySequence();
}