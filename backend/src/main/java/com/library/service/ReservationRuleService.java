package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.ReservationRule;

import java.util.List;
import java.util.Map;

/**
 * 预约规则服务接口
 */
public interface ReservationRuleService extends IService<ReservationRule> {
    
    /**
     * 初始化默认规则
     */
    void initDefaultRules();
    
    /**
     * 获取所有规则（Map形式）
     */
    Map<String, String> getAllRules();
    
    /**
     * 获取所有规则列表（含完整信息）
     */
    List<ReservationRule> getRulesList();
    
    /**
     * 获取规则值
     */
    String getRuleValue(String ruleKey);
    
    /**
     * 更新规则
     */
    void updateRule(String ruleKey, String ruleValue);
    
    /**
     * 解析预约时间段，返回时间段列表
     * 格式：08:00-12:00,14:00-18:00
     */
    List<TimeSlot> getAvailableTimeSlots();
    
    /**
     * 时间段值对象
     */
    class TimeSlot {
        private String start;
        private String end;
        
        public TimeSlot() {}
        
        public TimeSlot(String start, String end) {
            this.start = start;
            this.end = end;
        }
        
        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }
        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
    }
}
