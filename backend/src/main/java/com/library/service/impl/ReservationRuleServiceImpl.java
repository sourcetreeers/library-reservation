package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.ReservationRule;
import com.library.mapper.ReservationRuleMapper;
import com.library.service.ReservationRuleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约规则服务实现类
 */
@Service
public class ReservationRuleServiceImpl extends ServiceImpl<ReservationRuleMapper, ReservationRule> implements ReservationRuleService {
    
    @Override
    public void initDefaultRules() {
        // 检查是否已初始化
        long count = count();
        if (count > 0) {
            return;
        }
        
        // 可预约时间段
        ReservationRule rule1 = new ReservationRule();
        rule1.setRuleKey("reservation_time_slots");
        rule1.setRuleName("可预约时间段");
        rule1.setRuleValue("08:00-12:00,14:00-18:00,19:00-22:00");
        rule1.setDescription("每天可预约的时间段，多个时间段用逗号分隔");
        rule1.setCreateTime(LocalDateTime.now());
        rule1.setUpdateTime(LocalDateTime.now());
        save(rule1);
        
        // 信用等级规则 - 极好
        ReservationRule rule2 = new ReservationRule();
        rule2.setRuleKey("excellent_max_hours");
        rule2.setRuleName("极好等级最大预约时长");
        rule2.setRuleValue("6");
        rule2.setDescription("信用等级为极好的用户最大预约时长（小时）");
        rule2.setCreateTime(LocalDateTime.now());
        rule2.setUpdateTime(LocalDateTime.now());
        save(rule2);
        
        ReservationRule rule3 = new ReservationRule();
        rule3.setRuleKey("excellent_max_count");
        rule3.setRuleName("极好等级每日最大预约次数");
        rule3.setRuleValue("4");
        rule3.setDescription("信用等级为极好的用户每日最大预约次数");
        rule3.setCreateTime(LocalDateTime.now());
        rule3.setUpdateTime(LocalDateTime.now());
        save(rule3);
        
        // 信用等级规则 - 良好
        ReservationRule rule4 = new ReservationRule();
        rule4.setRuleKey("good_max_hours");
        rule4.setRuleName("良好等级最大预约时长");
        rule4.setRuleValue("4");
        rule4.setDescription("信用等级为良好的用户最大预约时长（小时）");
        rule4.setCreateTime(LocalDateTime.now());
        rule4.setUpdateTime(LocalDateTime.now());
        save(rule4);
        
        ReservationRule rule5 = new ReservationRule();
        rule5.setRuleKey("good_max_count");
        rule5.setRuleName("良好等级每日最大预约次数");
        rule5.setRuleValue("3");
        rule5.setDescription("信用等级为良好的用户每日最大预约次数");
        rule5.setCreateTime(LocalDateTime.now());
        rule5.setUpdateTime(LocalDateTime.now());
        save(rule5);
        
        // 信用等级规则 - 一般
        ReservationRule rule6 = new ReservationRule();
        rule6.setRuleKey("average_max_hours");
        rule6.setRuleName("一般等级最大预约时长");
        rule6.setRuleValue("2");
        rule6.setDescription("信用等级为一般的用户最大预约时长（小时）");
        rule6.setCreateTime(LocalDateTime.now());
        rule6.setUpdateTime(LocalDateTime.now());
        save(rule6);
        
        ReservationRule rule7 = new ReservationRule();
        rule7.setRuleKey("average_max_count");
        rule7.setRuleName("一般等级每日最大预约次数");
        rule7.setRuleValue("2");
        rule7.setDescription("信用等级为一般的用户每日最大预约次数");
        rule7.setCreateTime(LocalDateTime.now());
        rule7.setUpdateTime(LocalDateTime.now());
        save(rule7);
        
        // 信用等级规则 - 较差
        ReservationRule rule8 = new ReservationRule();
        rule8.setRuleKey("poor_max_hours");
        rule8.setRuleName("较差等级最大预约时长");
        rule8.setRuleValue("1");
        rule8.setDescription("信用等级为较差的用户最大预约时长（小时）");
        rule8.setCreateTime(LocalDateTime.now());
        rule8.setUpdateTime(LocalDateTime.now());
        save(rule8);
        
        ReservationRule rule9 = new ReservationRule();
        rule9.setRuleKey("poor_max_count");
        rule9.setRuleName("较差等级每日最大预约次数");
        rule9.setRuleValue("1");
        rule9.setDescription("信用等级为较差的用户每日最大预约次数");
        rule9.setCreateTime(LocalDateTime.now());
        rule9.setUpdateTime(LocalDateTime.now());
        save(rule9);
        
        // 信用等级规则 - 暂停服务
        ReservationRule rule10 = new ReservationRule();
        rule10.setRuleKey("suspended_max_hours");
        rule10.setRuleName("暂停服务等级最大预约时长");
        rule10.setRuleValue("0");
        rule10.setDescription("信用等级为暂停服务的用户最大预约时长（小时）");
        rule10.setCreateTime(LocalDateTime.now());
        rule10.setUpdateTime(LocalDateTime.now());
        save(rule10);
        
        ReservationRule rule11 = new ReservationRule();
        rule11.setRuleKey("suspended_max_count");
        rule11.setRuleName("暂停服务等级每日最大预约次数");
        rule11.setRuleValue("0");
        rule11.setDescription("信用等级为暂停服务的用户每日最大预约次数");
        rule11.setCreateTime(LocalDateTime.now());
        rule11.setUpdateTime(LocalDateTime.now());
        save(rule11);
        
        // 暂离超时时间（分钟）
        ReservationRule rule12 = new ReservationRule();
        rule12.setRuleKey("temp_leave_timeout");
        rule12.setRuleName("暂离超时时间");
        rule12.setRuleValue("20");
        rule12.setDescription("暂离状态超过此分钟数将自动签退并扣分（考试周可调整）");
        rule12.setCreateTime(LocalDateTime.now());
        rule12.setUpdateTime(LocalDateTime.now());
        save(rule12);
    }
    
    @Override
    public Map<String, String> getAllRules() {
        List<ReservationRule> rules = list();
        Map<String, String> ruleMap = new HashMap<>();
        for (ReservationRule rule : rules) {
            ruleMap.put(rule.getRuleKey(), rule.getRuleValue());
        }
        return ruleMap;
    }
    
    @Override
    public List<ReservationRule> getRulesList() {
        return list();
    }
    
    @Override
    public String getRuleValue(String ruleKey) {
        LambdaQueryWrapper<ReservationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationRule::getRuleKey, ruleKey);
        ReservationRule rule = getOne(wrapper);
        return rule != null ? rule.getRuleValue() : null;
    }
    
    @Override
    public void updateRule(String ruleKey, String ruleValue) {
        LambdaQueryWrapper<ReservationRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationRule::getRuleKey, ruleKey);
        ReservationRule rule = getOne(wrapper);
        
        if (rule != null) {
            rule.setRuleValue(ruleValue);
            rule.setUpdateTime(LocalDateTime.now());
            updateById(rule);
        }
    }
    
    @Override
    public List<ReservationRuleService.TimeSlot> getAvailableTimeSlots() {
        String slotsStr = getRuleValue("reservation_time_slots");
        if (slotsStr == null || slotsStr.isEmpty()) {
            return new ArrayList<>();
        }
        return java.util.Arrays.stream(slotsStr.split(","))
                .map(String::trim)
                .filter(s -> s.contains("-"))
                .map(s -> {
                    String[] parts = s.split("-");
                    return new ReservationRuleService.TimeSlot(parts[0].trim(), parts[1].trim());
                })
                .collect(Collectors.toList());
    }
}
