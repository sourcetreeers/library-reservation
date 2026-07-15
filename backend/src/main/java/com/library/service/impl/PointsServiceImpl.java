package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Notification;
import com.library.entity.PointsRecord;
import com.library.entity.UserPoints;
import com.library.mapper.NotificationMapper;
import com.library.mapper.PointsRecordMapper;
import com.library.mapper.UserPointsMapper;
import com.library.service.NotificationService;
import com.library.service.PointsService;
import com.library.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 积分服务实现类
 */
@Service
public class PointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints> implements PointsService {
    
    @Autowired
    private UserPointsMapper userPointsMapper;
    
    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private NotificationService notificationService;
    
    // 每日加分上限
    private static final int DAILY_POINTS_LIMIT = 2;
    
    @Override
    @Transactional
    public void initUserPoints(Long userId, String studentNo) {
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userId);
        userPoints.setStudentNo(studentNo);
        userPoints.setTotalPoints(80); // 初始积分80分
        userPoints.setCurrentPoints(80); // 80分对应"良好"等级
        userPoints.setCreditLevel("良好");
        userPoints.setMaxReservationHours(4); // 良好等级：4小时
        userPoints.setMaxReservationCount(3); // 良好等级：3次/天
        userPoints.setLastUpdateTime(LocalDateTime.now());
        userPoints.setCreateTime(LocalDateTime.now());
        userPoints.setUpdateTime(LocalDateTime.now());
        
        userPointsMapper.insert(userPoints);
    }
    
    @Override
    public UserPoints getUserPoints(Long userId) {
        LambdaQueryWrapper<UserPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPoints::getUserId, userId);
        UserPoints userPoints = userPointsMapper.selectOne(wrapper);
        
        if (userPoints != null) {
            // 计算时间衰减后的有效积分
            Integer effectivePoints = calculateEffectivePoints(userId);
            userPoints.setCurrentPoints(effectivePoints);
            
            // 更新信用等级和权益
            updateCreditLevel(userPoints);
        }
        
        return userPoints;
    }
    
    @Override
    @Transactional
    public void addPoints(Long userId, Integer points, String changeType, String sourceType, 
                          Long reservationId, String description) {
        UserPoints userPoints = getUserPointsWithoutUpdate(userId);
        if (userPoints == null) {
            throw new RuntimeException("用户积分记录不存在");
        }
        
        // 检查今日加分是否达到上限（只针对正常履约和提前签退）
        if ("正常履约".equals(changeType) || "提前签退".equals(changeType)) {
            int todayAddedPoints = getTodayAddedPoints(userId);
            if (todayAddedPoints >= DAILY_POINTS_LIMIT) {
                return; // 今日加分已达上限
            }
            // 确保不超过每日上限
            int canAddPoints = Math.min(points, DAILY_POINTS_LIMIT - todayAddedPoints);
            if (canAddPoints <= 0) {
                return;
            }
            points = canAddPoints;
        }
        
        Integer pointsBefore = userPoints.getTotalPoints();
        Integer pointsAfter = pointsBefore + points;
        
        // 更新总积分
        userPoints.setTotalPoints(pointsAfter);
        userPoints.setLastUpdateTime(LocalDateTime.now());
        userPoints.setUpdateTime(LocalDateTime.now());
        userPointsMapper.updateById(userPoints);
        
        // 创建积分记录
        createPointsRecord(userId, userPoints.getStudentNo(), points, pointsBefore, 
                          pointsAfter, changeType, sourceType, reservationId, description);
        
        // 创建通知
        String title = "积分增加";
        String type = "加分通知";
        String linkUrl = "/mobile/my-points";
        notificationService.createNotification(userId, title, description, type, linkUrl);
    }
    
    @Override
    @Transactional
    public void deductPoints(Long userId, Integer points, String changeType, String sourceType, 
                             Long reservationId, String description) {
        UserPoints userPoints = getUserPointsWithoutUpdate(userId);
        if (userPoints == null) {
            throw new RuntimeException("用户积分记录不存在");
        }
        
        Integer pointsBefore = userPoints.getTotalPoints();
        Integer pointsAfter = pointsBefore - points;
        
        // 积分可以为负数，但最低不低于-60
        if (pointsAfter < -60) {
            pointsAfter = -60;
        }
        
        // 更新总积分
        userPoints.setTotalPoints(pointsAfter);
        userPoints.setLastUpdateTime(LocalDateTime.now());
        userPoints.setUpdateTime(LocalDateTime.now());
        userPointsMapper.updateById(userPoints);
        
        // 创建积分记录
        createPointsRecord(userId, userPoints.getStudentNo(), -points, pointsBefore, 
                          pointsAfter, changeType, sourceType, reservationId, description);
        
        // 创建通知
        String title = "积分扣减";
        String type = "扣分通知";
        String linkUrl = "/mobile/my-points";
        notificationService.createNotification(userId, title, description, type, linkUrl);
    }
    
    @Override
    @Transactional
    public void addPointsForNormalCheckIn(Long userId, Long reservationId) {
        addPoints(userId, 1, "正常履约", "预约签到", reservationId, "按时签到履约");
    }
    
    @Override
    @Transactional
    public void addPointsForEarlyCheckOut(Long userId, Long reservationId) {
        addPoints(userId, 1, "提前签退", "预约签退", reservationId, "提前签退，良好行为");
    }
    
    @Override
    @Transactional
    public void deductPointsForNoShow(Long userId, Long reservationId) {
        deductPointsForNoShow(userId, reservationId, 6);
    }
    
    @Override
    @Transactional
    public void deductPointsForNoShow(Long userId, Long reservationId, int points) {
        deductPoints(userId, points, "爽约", "系统检测", reservationId, "预约后未签到，视为爽约，扣" + points + "分");
    }
    
    @Override
    @Transactional
    public void deductPointsForTempLeaveTimeout(Long userId, Long reservationId) {
        deductPointsForTempLeaveTimeout(userId, reservationId, 3);
    }
    
    @Override
    @Transactional
    public void deductPointsForTempLeaveTimeout(Long userId, Long reservationId, int points) {
        deductPoints(userId, points, "暂离超时", "系统检测", reservationId, "暂离超时未返回，扣" + points + "分");
    }
    
    @Override
    @Transactional
    public void deductPointsForNoCheckOut(Long userId, Long reservationId) {
        deductPoints(userId, 3, "未签退离馆", "系统检测", reservationId, "离馆未签退");
    }
    
    @Override
    @Transactional
    public void deductPointsForOccupyingSeat(Long userId, Long reservationId) {
        deductPoints(userId, 3, "占座", "管理员操作", reservationId, "超时占座，经管理员确认");
    }
    
    @Override
    @Transactional
    public void restorePointsForComplaint(Long userId, Long reservationId) {
        // 直接调用 addPoints，不检查每日加分上限（管理员恢复积分不受限制）
        UserPoints userPoints = getUserPointsWithoutUpdate(userId);
        if (userPoints == null) {
            throw new RuntimeException("用户积分记录不存在");
        }
        
        // 查询该预约最近的扣分记录
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getReservationId, reservationId)
               .eq(PointsRecord::getUserId, userId)
               .lt(PointsRecord::getPointsChange, 0)  // 扣分记录
               .orderByDesc(PointsRecord::getCreateTime)
               .last("LIMIT 1");
        PointsRecord deductionRecord = pointsRecordMapper.selectOne(wrapper);
        
        if (deductionRecord == null) {
            log.warn("未找到预约 " + reservationId + " 用户的扣分记录，跳过积分返还");
            return;
        }
        
        int restorePoints = -deductionRecord.getPointsChange(); // 取正数
        
        Integer pointsBefore = userPoints.getTotalPoints();
        Integer pointsAfter = pointsBefore + restorePoints;
        
        userPoints.setTotalPoints(pointsAfter);
        userPoints.setLastUpdateTime(LocalDateTime.now());
        userPoints.setUpdateTime(LocalDateTime.now());
        userPointsMapper.updateById(userPoints);
        
        // 创建积分记录（免罚返还）
        createPointsRecord(userId, userPoints.getStudentNo(), restorePoints, pointsBefore, 
                          pointsAfter, "系统调整", "管理员操作", reservationId, "占座举报审核通过，返还积分");
        
        // 创建通知
        String title = "积分返还";
        String type = "系统通知";
        String linkUrl = "/mobile/my-points";
        notificationService.createNotification(userId, title, 
            "您的举报已审核通过，因占座问题被扣除的积分已返还", type, linkUrl);
    }
    
    @Override
    public void updateCreditLevel(UserPoints userPoints) {
        Integer effectivePoints = userPoints.getCurrentPoints();
        String creditLevel;
        Integer maxHours;
        Integer maxCount;
        
        if (effectivePoints >= 90) {
            creditLevel = "极好";
            maxHours = 6;
            maxCount = 4;
        } else if (effectivePoints >= 75) {
            creditLevel = "良好";
            maxHours = 4;
            maxCount = 3;
        } else if (effectivePoints >= 60) {
            creditLevel = "一般";
            maxHours = 2;
            maxCount = 2;
        } else if (effectivePoints >= 40) {
            creditLevel = "较差";
            maxHours = 1;
            maxCount = 1;
        } else {
            creditLevel = "暂停服务";
            maxHours = 0;
            maxCount = 0;
        }
        
        userPoints.setCreditLevel(creditLevel);
        userPoints.setMaxReservationHours(maxHours);
        userPoints.setMaxReservationCount(maxCount);
        userPoints.setUpdateTime(LocalDateTime.now());
        
        userPointsMapper.updateById(userPoints);
    }
    
    @Override
    public PageResult<PointsRecord> getPointsRecords(int current, int size, Long userId) {
        int offset = (current - 1) * size;
        
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.orderByDesc(PointsRecord::getCreateTime);
        
        List<PointsRecord> records = pointsRecordMapper.selectList(
            new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreateTime)
                .last("LIMIT " + offset + ", " + size)
        );
        
        long total = pointsRecordMapper.selectCount(
            new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
        );
        
        return new PageResult<>(records, total, current, size);
    }
    
    @Override
    public PageResult<PointsRecord> getAllPointsRecords(int current, int size, Long userId, String changeType) {
        int offset = (current - 1) * size;
        
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(PointsRecord::getUserId, userId);
        }
        if (changeType != null && !changeType.isEmpty()) {
            wrapper.eq(PointsRecord::getChangeType, changeType);
        }
        wrapper.orderByDesc(PointsRecord::getCreateTime);
        
        List<PointsRecord> records = pointsRecordMapper.selectList(
            wrapper.last("LIMIT " + offset + ", " + size)
        );
        
        long total = pointsRecordMapper.selectCount(wrapper);
        
        return new PageResult<>(records, total, current, size);
    }
    
    @Override
    public Integer calculateEffectivePoints(Long userId) {
        // 获取所有积分记录
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.orderByDesc(PointsRecord::getCreateTime);
        List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);
        
        if (records.isEmpty()) {
            return 80; // 默认初始积分
        }
        
        LocalDateTime now = LocalDateTime.now();
        double effectivePoints = 0;
        
        // 时间衰减算法：近期行为权重高，历史行为权重低
        for (PointsRecord record : records) {
            long daysDiff = ChronoUnit.DAYS.between(record.getCreateTime(), now);
            double weight;
            
            if (daysDiff <= 7) {
                weight = 1.0; // 7天内，权重100%
            } else if (daysDiff <= 30) {
                weight = 0.8; // 30天内，权重80%
            } else if (daysDiff <= 90) {
                weight = 0.6; // 90天内，权重60%
            } else if (daysDiff <= 180) {
                weight = 0.4; // 180天内，权重40%
            } else {
                weight = 0.2; // 超过180天，权重20%
            }
            
            effectivePoints += record.getPointsChange() * weight;
        }
        
        // 基础分80分（与初始积分一致）+ 加权积分
        int result = 80 + (int) Math.round(effectivePoints);
        
        // 限制在合理范围内
        if (result > 140) {
            result = 140;
        }
        if (result < 0) {
            result = 0;
        }
        
        return result;
    }
    
    /**
     * 获取用户积分（不更新）
     */
    private UserPoints getUserPointsWithoutUpdate(Long userId) {
        LambdaQueryWrapper<UserPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPoints::getUserId, userId);
        return userPointsMapper.selectOne(wrapper);
    }
    
    /**
     * 创建积分记录
     */
    private void createPointsRecord(Long userId, String studentNo, Integer pointsChange, 
                                    Integer pointsBefore, Integer pointsAfter,
                                    String changeType, String sourceType, 
                                    Long reservationId, String description) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setStudentNo(studentNo);
        record.setPointsChange(pointsChange);
        record.setPointsBefore(pointsBefore);
        record.setPointsAfter(pointsAfter);
        record.setChangeType(changeType);
        record.setSourceType(sourceType);
        record.setReservationId(reservationId);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
        
        pointsRecordMapper.insert(record);
    }
    
    /**
     * 获取今日已加分数
     */
    private int getTodayAddedPoints(Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.in(PointsRecord::getChangeType, "正常履约", "提前签退");
        wrapper.ge(PointsRecord::getCreateTime, startOfDay);
        wrapper.le(PointsRecord::getCreateTime, endOfDay);
        
        List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);
        
        return records.stream()
                     .mapToInt(PointsRecord::getPointsChange)
                     .sum();
    }
}
