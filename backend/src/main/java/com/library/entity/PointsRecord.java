package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 积分变动记录实体类
 */
@TableName("points_record")
public class PointsRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String studentNo; // 学工号
    
    private Integer pointsChange; // 积分变动（正数为加分，负数为减分）
    
    private Integer pointsBefore; // 变动前积分
    
    private Integer pointsAfter; // 变动后积分
    
    private String changeType; // 变动类型：正常履约/提前签退/爽约/暂离超时/未签退离馆/系统调整
    
    private String sourceType; // 来源类型：预约签到/预约签退/系统检测/管理员操作
    
    private Long reservationId; // 关联的预约ID（可选）
    
    private String description; // 详细描述
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public PointsRecord() {}

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

    public Integer getPointsChange() {
        return pointsChange;
    }

    public void setPointsChange(Integer pointsChange) {
        this.pointsChange = pointsChange;
    }

    public Integer getPointsBefore() {
        return pointsBefore;
    }

    public void setPointsBefore(Integer pointsBefore) {
        this.pointsBefore = pointsBefore;
    }

    public Integer getPointsAfter() {
        return pointsAfter;
    }

    public void setPointsAfter(Integer pointsAfter) {
        this.pointsAfter = pointsAfter;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
