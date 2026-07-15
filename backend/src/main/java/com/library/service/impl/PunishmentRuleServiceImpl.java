package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.PunishmentRule;
import com.library.mapper.PunishmentRuleMapper;
import com.library.service.PunishmentRuleService;
import org.springframework.stereotype.Service;

/**
 * 违规规则Service实现
 */
@Service
public class PunishmentRuleServiceImpl extends ServiceImpl<PunishmentRuleMapper, PunishmentRule> implements PunishmentRuleService {
    
    @Override
    public PunishmentRule getMatchedRule(String violationType, int violationCount) {
        // 确定适用次数级别
        String applyTimes;
        if (violationCount == 0) {
            // 第1次违规（当前这条）
            applyTimes = "FIRST";
        } else if (violationCount == 1) {
            // 已有1次，这是第2次
            applyTimes = "SECOND";
        } else {
            // 第3次及以上
            applyTimes = "THIRD_PLUS";
        }
        
        LambdaQueryWrapper<PunishmentRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PunishmentRule::getViolationType, violationType)
               .eq(PunishmentRule::getApplyTimes, applyTimes)
               .eq(PunishmentRule::getIsActive, true)
               .last("LIMIT 1");
        
        return this.getOne(wrapper);
    }
}
