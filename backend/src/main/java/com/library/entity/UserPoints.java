package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 用户积分实体类
 */
@TableName("user_points")
public class UserPoints {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String studentNo; // 学工号
    
    private Integer totalPoints; // 总积分（可超出140）
    
    private Integer currentPoints; // 当前有效积分（最高按100计算权益）
    
    private String creditLevel; // 信用等级：极好/良好/一般/较差/暂停服务
    
    private Integer maxReservationHours; // 最大预约时长（小时）
    
    private Integer maxReservationCount; // 每日最大预约次数
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public UserPoints() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public Integer getMaxReservationHours() {
        return maxReservationHours;
    }

    public void setMaxReservationHours(Integer maxReservationHours) {
        this.maxReservationHours = maxReservationHours;
    }

    public Integer getMaxReservationCount() {
        return maxReservationCount;
    }

    public void setMaxReservationCount(Integer maxReservationCount) {
        this.maxReservationCount = maxReservationCount;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
