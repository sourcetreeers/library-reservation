package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Complaint;
import com.library.entity.Reservation;
import com.library.entity.Seat;
import com.library.entity.User;
import com.library.entity.ViolationRecord;
import com.library.mapper.ComplaintMapper;
import com.library.service.ComplaintService;
import com.library.service.NotificationService;
import com.library.service.PointsService;
import com.library.service.ReservationService;
import com.library.service.SeatService;
import com.library.service.UserService;
import com.library.service.ViolationRecordService;
import com.library.vo.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 举报服务实现类
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {

    private static final Logger log = LoggerFactory.getLogger(ComplaintServiceImpl.class);

    @Autowired
    private ComplaintMapper complaintMapper;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ViolationRecordService violationRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public void createComplaint(Long userId, Long reservationId, String description, String imageUrl) {
        // 检查该预约是否已提交过举报（待处理状态的）
        LambdaQueryWrapper<Complaint> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Complaint::getReporterReservationId, reservationId)
                    .eq(Complaint::getStatus, "待处理");
        long existingCount = complaintMapper.selectCount(checkWrapper);
        if (existingCount > 0) {
            throw new RuntimeException("该预约已有举报正在处理中");
        }

        // 查询举报人的预约信息
        Reservation reporterReservation = reservationService.getById(reservationId);
        if (reporterReservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 查询占座者信息：同一座位、前一个时间段的预约，且已签到（状态为已使用）
        LambdaQueryWrapper<Reservation> occupantWrapper = new LambdaQueryWrapper<>();
        occupantWrapper.eq(Reservation::getSeatId, reporterReservation.getSeatId())
                       .eq(Reservation::getStatus, "已使用")
                       .le(Reservation::getEndTime, reporterReservation.getStartTime())
                       .orderByDesc(Reservation::getEndTime)
                       .last("LIMIT 1");
        Reservation occupantReservation = reservationService.getOne(occupantWrapper);

        Complaint complaint = new Complaint();
        complaint.setReporterId(userId);
        if (occupantReservation != null) {
            complaint.setOccupantId(occupantReservation.getUserId());
            complaint.setOccupantReservationId(occupantReservation.getId());
        }
        complaint.setSeatId(reporterReservation.getSeatId());
        complaint.setLibraryId(reporterReservation.getLibraryId());
        complaint.setReporterReservationId(reservationId);
        complaint.setDescription(description);
        complaint.setImageUrl(imageUrl);
        complaint.setStatus("待处理");
        complaint.setCreateTime(LocalDateTime.now());
        complaintMapper.insert(complaint);

        log.info("举报提交成功：举报人={}, 预约ID={}, 占座者ID={}, 有图片={}",
                userId, reservationId,
                occupantReservation != null ? occupantReservation.getUserId() : "未知",
                imageUrl != null && !imageUrl.isEmpty());
    }

    @Override
    public PageResult<Complaint> getUserComplaints(Long userId, int current, int size) {
        int offset = (current - 1) * size;

        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Complaint::getReporterId, userId);
        wrapper.orderByDesc(Complaint::getCreateTime);

        List<Complaint> records = complaintMapper.selectList(wrapper.last("LIMIT " + offset + ", " + size));
        long total = complaintMapper.selectCount(wrapper);

        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<Complaint> getComplaintPage(String status, int current, int size) {
        int offset = (current - 1) * size;
        List<Complaint> records = complaintMapper.selectComplaintPage(status, offset, size);
        long total = complaintMapper.countComplaints(status);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    @Transactional
    public void handleComplaint(Long complaintId, Long handlerId, String status, String reply) {
        Complaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("举报记录不存在");
        }
        if (!"待处理".equals(complaint.getStatus())) {
            throw new RuntimeException("该举报已被处理");
        }

        complaint.setStatus(status);
        complaint.setHandlerId(handlerId);
        complaint.setHandlerReply(reply);
        complaint.setHandleTime(LocalDateTime.now());
        complaintMapper.updateById(complaint);

        if ("已确认".equals(status)) {
            // 已确认：对占座者扣分 + 返还举报者积分 + 释放座位
            if (complaint.getOccupantId() != null) {
                try {
                    pointsService.deductPointsForOccupyingSeat(
                            complaint.getOccupantId(),
                            complaint.getOccupantReservationId());
                } catch (Exception e) {
                    log.error("占座扣分失败: " + e.getMessage());
                }

                // 自动创建恶意占座违规记录
                try {
                    User occupant = userService.getById(complaint.getOccupantId());
                    String orderNo = null;
                    if (complaint.getOccupantReservationId() != null) {
                        Reservation res = reservationService.getById(complaint.getOccupantReservationId());
                        if (res != null) orderNo = res.getOrderNo();
                    }
                    ViolationRecord record = new ViolationRecord();
                    record.setUserId(complaint.getOccupantId());
                    record.setUserName(occupant != null ? occupant.getRealName() : "未知用户");
                    record.setStudentNo(occupant != null ? occupant.getUsername() : "");
                    record.setViolationType("恶意占座");
                    record.setReservationId(complaint.getOccupantReservationId());
                    record.setOrderNo(orderNo);
                    record.setPointsDeducted(-3);
                    record.setBanDays(0);
                    record.setStatus("待处理");
                    record.setViolationTime(LocalDateTime.now());
                    violationRecordService.createViolation(record);
                    log.info("自动创建恶意占座违规记录：用户={}, 举报ID={}", 
                            complaint.getOccupantId(), complaint.getId());
                } catch (Exception e) {
                    log.error("创建恶意占座违规记录失败: " + e.getMessage(), e);
                }
            }

            restorePointsAndNotify(complaint, reply,
                    "您的举报已审核确认，占座者已被扣分，您的积分已返还。",
                    "您因超时占座被举报，经管理员确认已被扣分。");

            // 释放座位
            releaseSeat(complaint.getReporterReservationId());

            log.info("举报处理完成：举报ID={}, 举报人={}, 占座者={}, 已确认",
                    complaintId, complaint.getReporterId(), complaint.getOccupantId());

        } else if ("仅返还".equals(status)) {
            // 仅返还：只返还举报者积分，不对占座者扣分
            try {
                pointsService.restorePointsForComplaint(
                        complaint.getReporterId(),
                        complaint.getReporterReservationId());
            } catch (Exception e) {
                log.error("积分返还失败: " + e.getMessage());
            }

            String reporterContent = (reply != null && !reply.trim().isEmpty()) 
                ? "您的举报已审核，积分已返还。" + reply
                : "您的举报已审核，积分已返还。";
            notificationService.createNotification(complaint.getReporterId(), "举报处理结果",
                    reporterContent, "系统通知", "/mobile/my-points");

            // 释放座位
            releaseSeat(complaint.getReporterReservationId());

            log.info("举报处理完成：举报ID={}, 举报人={}, 仅返还积分（未扣分）",
                    complaintId, complaint.getReporterId());

        } else if ("已驳回".equals(status)) {
            String content = (reply != null && !reply.trim().isEmpty())
                ? "您的举报已被驳回，回复：" + reply
                : "您的举报已被驳回。";
            notificationService.createNotification(complaint.getReporterId(), "举报处理结果",
                    content, "系统通知", "/mobile/my-points");

            log.info("举报已驳回：举报ID={}, 处理人={}", complaintId, handlerId);
        }
    }

    /**
     * 返还积分并发送通知（通用方法）
     */
    private void restorePointsAndNotify(Complaint complaint, String reply,
                                         String defaultReporterMsg, String defaultOccupantMsg) {
        // 返还举报者积分
        try {
            pointsService.restorePointsForComplaint(
                    complaint.getReporterId(),
                    complaint.getReporterReservationId());
        } catch (Exception e) {
            log.error("积分返还失败: " + e.getMessage());
        }

        // 通知举报人
        String reporterContent = (reply != null && !reply.trim().isEmpty()) 
            ? defaultReporterMsg + "管理员回复：" + reply
            : defaultReporterMsg;
        notificationService.createNotification(complaint.getReporterId(), "举报处理结果",
                reporterContent, "系统通知", "/mobile/my-points");

        // 通知被举报人
        if (complaint.getOccupantId() != null) {
            String occupantContent = (reply != null && !reply.trim().isEmpty())
                ? defaultOccupantMsg + reply
                : defaultOccupantMsg;
            notificationService.createNotification(complaint.getOccupantId(), "违规通知",
                    occupantContent, "系统通知", "/mobile/my-points");
        }
    }

    /**
     * 释放座位
     */
    private void releaseSeat(Long reservationId) {
        Reservation reporterReservation = reservationService.getById(reservationId);
        if (reporterReservation != null) {
            Seat seat = seatService.getById(reporterReservation.getSeatId());
            if (seat != null) {
                seat.setStatus("正常");
                seatService.updateById(seat);
            }
        }
    }
}
