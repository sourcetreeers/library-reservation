package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.PointsRecord;
import com.library.entity.UserPoints;
import com.library.vo.PageResult;

import java.util.List;

/**
 * 积分服务接口
 */
public interface PointsService extends IService<UserPoints> {
    
    /**
     * 初始化用户积分（新用户注册时调用）
     */
    void initUserPoints(Long userId, String studentNo);
    
    /**
     * 获取用户积分信息
     */
    UserPoints getUserPoints(Long userId);
    
    /**
     * 添加积分
     */
    void addPoints(Long userId, Integer points, String changeType, String sourceType, 
                   Long reservationId, String description);
    
    /**
     * 扣除积分
     */
    void deductPoints(Long userId, Integer points, String changeType, String sourceType, 
                      Long reservationId, String description);
    
    /**
     * 正常履约加分
     */
    void addPointsForNormalCheckIn(Long userId, Long reservationId);
    
    /**
     * 提前签退加分
     */
    void addPointsForEarlyCheckOut(Long userId, Long reservationId);
    
    /**
     * 爽约扣分
     */
    void deductPointsForNoShow(Long userId, Long reservationId);
    
    /**
     * 爽约扣分（指定扣分数值）
     */
    void deductPointsForNoShow(Long userId, Long reservationId, int points);
    
    /**
     * 暂离超时扣分
     */
    void deductPointsForTempLeaveTimeout(Long userId, Long reservationId);
    
    /**
     * 暂离超时扣分（指定扣分数值）
     */
    void deductPointsForTempLeaveTimeout(Long userId, Long reservationId, int points);
    
    /**
     * 未签退离馆扣分
     */
    void deductPointsForNoCheckOut(Long userId, Long reservationId);
    
    /**
     * 更新信用等级和权益
     */
    void updateCreditLevel(UserPoints userPoints);
    
    /**
     * 查询积分变动记录
     */
    PageResult<PointsRecord> getPointsRecords(int current, int size, Long userId);
    
    /**
     * 查询所有用户积分记录（管理员）
     */
    PageResult<PointsRecord> getAllPointsRecords(int current, int size, Long userId, String changeType);
    
    /**
     * 占座扣分（管理员审核后调用）
     */
    void deductPointsForOccupyingSeat(Long userId, Long reservationId);
    
    /**
     * 积分返还（管理员确认举报后，返还被误扣的积分）
     */
    void restorePointsForComplaint(Long userId, Long reservationId);
    
    /**
     * 计算时间衰减后的有效积分
     */
    Integer calculateEffectivePoints(Long userId);
}
