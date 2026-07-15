package com.library.controller;

import com.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务控制器 - 用于手动触发定时任务（测试用）
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {
    
    @Autowired
    private ReservationService reservationService;
    
    /**
     * 手动触发处理即将过期的预约任务
     */
    @PostMapping("/handleSoonExpired")
    public Map<String, Object> handleSoonExpiredReservations() {
        Map<String, Object> result = new HashMap<>();
        try {
            reservationService.handleSoonExpiredReservations();
            result.put("success", true);
            result.put("message", "手动执行处理即将过期预约任务成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "手动执行处理即将过期预约任务失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 手动触发处理已过期的预约任务
     */
    @PostMapping("/handleExpired")
    public Map<String, Object> handleExpiredReservations() {
        Map<String, Object> result = new HashMap<>();
        try {
            reservationService.handleExpiredReservations();
            result.put("success", true);
            result.put("message", "手动执行处理已过期预约任务成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "手动执行处理已过期预约任务失败：" + e.getMessage());
        }
        return result;
    }
}