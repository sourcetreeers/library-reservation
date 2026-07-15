package com.library.task;

import com.library.entity.Reservation;
import com.library.service.NotificationService;
import com.library.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 预约相关定时任务
 */
@Component
public class ReservationTask {
    
    private static final Logger log = LoggerFactory.getLogger(ReservationTask.class);
    
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private NotificationService notificationService;
    
    /**
     * 每5分钟执行一次，处理未签到的预约（爽约）
     * 到预约开始时间未签到，自动标记为爽约并释放座位
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void handleNoShowReservations() {
        log.info("开始执行定时任务：处理未签到爽约");
        try {
            reservationService.handleNoShowReservations();
            log.info("定时任务执行完成：处理未签到爽约");
        } catch (Exception e) {
            log.error("定时任务执行失败：处理未签到爽约", e);
        }
    }
    
    /**
     * 每10分钟执行一次，处理即将过期的预约
     * 查询当前时间已超过"预约结束时间前20分钟"的记录，将状态设置为"爽约"
     * 例如：预约结束时间是15:00，那么在14:40之后就会被标记为爽约
     */
    @Scheduled(cron = "0 0/10 * * * ? ")
    public void handleSoonExpiredReservations() {
        log.info("开始执行定时任务：处理即将过期的预约");
        try {
            reservationService.handleSoonExpiredReservations();
            log.info("定时任务执行完成：处理即将过期的预约");
        } catch (Exception e) {
            log.error("定时任务执行失败：处理即将过期的预约", e);
        }
    }
    
    /**
     * 每小时执行一次，处理已经过期的预约
     * 作为兜底机制，处理已经超过结束时间的预约
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // 60分钟 = 60 * 60 * 1000毫秒
    public void handleExpiredReservations() {
        log.info("开始执行定时任务：处理已过期的预约");
        try {
            reservationService.handleExpiredReservations();
            log.info("定时任务执行完成：处理已过期的预约");
        } catch (Exception e) {
            log.error("定时任务执行失败：处理已过期的预约", e);
        }
    }
    
    /**
     * 每5分钟执行一次，处理暂离超时的预约
     * 暂离超过30分钟未返回，自动标记为爽约并扣分
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void handleTempLeaveTimeout() {
        log.info("开始执行定时任务：处理暂离超时");
        try {
            reservationService.handleTempLeaveTimeout();
            log.info("定时任务执行完成：处理暂离超时");
        } catch (Exception e) {
            log.error("定时任务执行失败：处理暂离超时", e);
        }
    }

    /**
     * 每5分钟执行一次，推送30分钟后的预约提醒通知
     */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void pushUpcomingReminders() {
        log.info("开始执行定时任务：推送预约提醒");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderWindow = now.plusMinutes(30);
            
            // 查询30分钟后开始的预约（未签到）
            List<Reservation> upcoming = reservationService.lambdaQuery()
                    .eq(Reservation::getStatus, "已预约")
                    .between(Reservation::getStartTime, now, reminderWindow)
                    .list();
            
            for (Reservation reservation : upcoming) {
                String title = "预约即将开始";
                String content = String.format("您有预约将于 %s 开始，座位编号：%d，请按时签到",
                        reservation.getStartTime().toLocalTime().toString().substring(0, 5),
                        reservation.getSeatId());
                notificationService.createNotification(
                        reservation.getUserId(), title, content, "预约提醒", "/mobile/my-reservations");
                log.info("推送预约提醒：用户={}, 订单号={}", reservation.getUserId(), reservation.getOrderNo());
            }
            
            log.info("定时任务执行完成：推送预约提醒，共 {} 条", upcoming.size());
        } catch (Exception e) {
            log.error("定时任务执行失败：推送预约提醒", e);
        }
    }
}