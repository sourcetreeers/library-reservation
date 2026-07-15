package com.library.controller;

import com.library.service.ReservationRuleService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约规则公开接口控制器
 * 提供给前台（移动端预约页）获取可配置的规则数据
 */
@RestController
@RequestMapping("/reservation-rule")
@CrossOrigin
public class ReservationRuleController {

    @Autowired
    private ReservationRuleService reservationRuleService;

    /**
     * 获取可预约时间段列表
     * 前端预约页据此动态渲染时间选择器
     */
    @GetMapping("/time-slots")
    public Result<List<ReservationRuleService.TimeSlot>> getTimeSlots() {
        try {
            List<ReservationRuleService.TimeSlot> slots = reservationRuleService.getAvailableTimeSlots();
            return Result.success(slots);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取可预约时间段失败: " + e.getMessage());
        }
    }
}
