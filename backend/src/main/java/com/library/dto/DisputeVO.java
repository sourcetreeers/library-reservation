package com.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 争议统一视图对象（合并申诉和举报）
 */
public class DisputeVO {

    private Long id;
    /** 类型：complaint(举报) / appeal(申诉) */
    private String type;

    // ====== 通用字段 ======
    /** 发起人ID */
    private Long reporterId;
    /** 发起人姓名 */
    private String reporterName;
    /** 关联对方ID（举报的被举报人/申诉无） */
    private Long targetId;
    /** 关联对方姓名 */
    private String targetName;
    /** 内容描述（举报的description / 申诉的reason） */
    private String content;
    /** 状态 */
    private String status;
    /** 处理回复 */
    private String reply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    // ====== 举报特有字段 ======
    private String seatNumber;
    private String libraryName;
    private String imageUrl;

    /** 举报人预约开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reporterReservationStartTime;
    /** 举报人预约结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reporterReservationEndTime;
    /** 占座者预约开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occupantReservationStartTime;
    /** 占座者预约结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occupantReservationEndTime;

    // ====== 申诉特有字段 ======
    private Long pointsRecordId;

    public DisputeVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getHandleTime() { return handleTime; }
    public void setHandleTime(LocalDateTime handleTime) { this.handleTime = handleTime; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getLibraryName() { return libraryName; }
    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }

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

    public Long getPointsRecordId() { return pointsRecordId; }
    public void setPointsRecordId(Long pointsRecordId) { this.pointsRecordId = pointsRecordId; }
}
