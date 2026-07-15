package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.PunishmentRule;
import java.util.List;

/**
 * 违规规则Service
 */
public interface PunishmentRuleService extends IService<PunishmentRule> {
    
    /**
     * 根据违规类型和用户历史次数获取匹配的规则
     */
    PunishmentRule getMatchedRule(String violationType, int violationCount);
}
