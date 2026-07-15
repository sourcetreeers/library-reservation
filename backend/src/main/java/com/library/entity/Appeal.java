package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 违约申诉实体类
 */
@TableName("appeal")
public class Appeal {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long pointsRecordId;

    private String reason;

    private String imageUrl;

    private String status;

    private Long handlerId;

    private String reply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    public Appeal() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPointsRecordId() { return pointsRecordId; }
    public void setPointsRecordId(Long pointsRecordId) { this.pointsRecordId = pointsRecordId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getHandleTime() { return handleTime; }
    public void setHandleTime(LocalDateTime handleTime) { this.handleTime = handleTime; }
}
