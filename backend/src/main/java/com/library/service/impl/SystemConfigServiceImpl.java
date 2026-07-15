package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.SystemConfig;
import com.library.mapper.SystemConfigMapper;
import com.library.service.SystemConfigService;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务实现类
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {
    
    @Override
    public SystemConfig getByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        return getOne(wrapper);
    }
    
    @Override
    public void resetDailySequence() {
        SystemConfig config = getByKey("daily_order_sequence");
        if (config != null) {
            config.setConfigValue("0");
            updateById(config);
        }
    }
}