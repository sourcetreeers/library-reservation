package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 占座举报实体类
 */
@TableName("complaint")
public class Complaint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reporterId;

    private Long occupantId;

    private Long seatId;

    private Long libraryId;

    private Long reporterReservationId;

    private Long occupantReservationId;

    private String description;

    private String status;

    private Long handlerId;

    private String handlerReply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    // 联表查询字段（非数据库字段）
    @TableField(exist = false)
    private String reporterName;
    @TableField(exist = false)
    private String occupantName;
    @TableField(exist = false)
    private String libraryName;
    @TableField(exist = false)
    private String seatNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private LocalDateTime reporterReservationStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private LocalDateTime reporterReservationEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private LocalDateTime occupantReservationStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private LocalDateTime occupantReservationEndTime;

    @TableField(exist = false)
    private String imageUrl;

    public Complaint() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }

    public Long getOccupantId() { return occupantId; }
    public void setOccupantId(Long occupantId) { this.occupantId = occupantId; }

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }

    public Long getLibraryId() { return libraryId; }
    public void setLibraryId(Long libraryId) { this.libraryId = libraryId; }

    public Long getReporterReservationId() { return reporterReservationId; }
    public void setReporterReservationId(Long reporterReservationId) { this.reporterReservationId = reporterReservationId; }

    public Long getOccupantReservationId() { return occupantReservationId; }
    public void setOccupantReservationId(Long occupantReservationId) { this.occupantReservationId = occupantReservationId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }

    public String getHandlerReply() { return handlerReply; }
    public void setHandlerReply(String handlerReply) { this.handlerReply = handlerReply; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getHandleTime() { return handleTime; }
    public void setHandleTime(LocalDateTime handleTime) { this.handleTime = handleTime; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getOccupantName() { return occupantName; }
    public void setOccupantName(String occupantName) { this.occupantName = occupantName; }

    public String getLibraryName() { return libraryName; }
    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getReporterReservationStartTime() { return reporterReservationStartTime; }
    public void setReporterReservationStartTime(LocalDateTime reporterReservationStartTime) { this.reporterReservationStartTime = reporterReservationStartTime; }

    public LocalDateTime getReporterReservationEndTime() { return reporterReservationEndTime; }
    public void setReporterReservationEndTime(LocalDateTime reporterReservationEndTime) { this.reporterReservationEndTime = reporterReservationEndTime; }

    public LocalDateTime getOccupantReservationStartTime() { return occupantReservationStartTime; }
    public void setOccupantReservationStartTime(LocalDateTime occupantReservationStartTime) { this.occupantReservationStartTime = occupantReservationStartTime; }

    public LocalDateTime getOccupantReservationEndTime() { return occupantReservationEndTime; }
    public void setOccupantReservationEndTime(LocalDateTime occupantReservationEndTime) { this.occupantReservationEndTime = occupantReservationEndTime; }
}
