package com.library.service.impl;

import com.library.dto.DisputeQuery;
import com.library.dto.DisputeVO;
import com.library.dto.HandleDisputeDTO;
import com.library.entity.Appeal;
import com.library.entity.Complaint;
import com.library.entity.PointsRecord;
import com.library.entity.Reservation;
import com.library.mapper.PointsRecordMapper;
import com.library.service.AppealService;
import com.library.service.ComplaintService;
import com.library.service.DisputeService;
import com.library.service.ReservationService;
import com.library.vo.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 争议统一处理服务实现
 */
@Service
public class DisputeServiceImpl implements DisputeService {

    private static final Logger log = LoggerFactory.getLogger(DisputeServiceImpl.class);

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private AppealService appealService;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private ReservationService reservationService;

    @Override
    public PageResult<DisputeVO> getDisputePage(DisputeQuery query) {
        String typeFilter = query.getType();
        String statusFilter = query.getStatus();
        int current = query.getCurrent() != null ? query.getCurrent() : 1;
        int size = query.getSize() != null ? query.getSize() : 10;

        List<DisputeVO> allRecords = new ArrayList<>();

        // 查询举报记录（未指定类型或指定为complaint时）
        if (typeFilter == null || "complaint".equals(typeFilter)) {
            PageResult<Complaint> complaintPage = complaintService.getComplaintPage(
                    statusFilter, 1, Integer.MAX_VALUE);
            for (Complaint c : complaintPage.getRecords()) {
                DisputeVO vo = new DisputeVO();
                vo.setId(c.getId());
                vo.setType("complaint");
                vo.setReporterId(c.getReporterId());
                vo.setReporterName(c.getReporterName());
                vo.setTargetId(c.getOccupantId());
                vo.setTargetName(c.getOccupantName());
                vo.setContent(c.getDescription());
                vo.setStatus(c.getStatus());
                vo.setReply(c.getHandlerReply());
                vo.setCreateTime(c.getCreateTime());
                vo.setHandleTime(c.getHandleTime());
                vo.setSeatNumber(c.getSeatNumber());
                vo.setLibraryName(c.getLibraryName());
                vo.setImageUrl(c.getImageUrl());
                vo.setReporterReservationStartTime(c.getReporterReservationStartTime());
                vo.setReporterReservationEndTime(c.getReporterReservationEndTime());
                vo.setOccupantReservationStartTime(c.getOccupantReservationStartTime());
                vo.setOccupantReservationEndTime(c.getOccupantReservationEndTime());
                allRecords.add(vo);
            }
        }

        // 查询申诉记录（未指定类型或指定为appeal时）
        if (typeFilter == null || "appeal".equals(typeFilter)) {
            PageResult<Appeal> appealPage = appealService.getAppealPage(
                    statusFilter, 1, Integer.MAX_VALUE);
            for (Appeal a : appealPage.getRecords()) {
                DisputeVO vo = new DisputeVO();
                vo.setId(a.getId());
                vo.setType("appeal");
                vo.setReporterId(a.getUserId());
                vo.setReporterName(null); // 申诉的发起人名称需联表查询，此处留空或后续补充
                vo.setContent(a.getReason());
                vo.setStatus(a.getStatus());
                vo.setReply(a.getReply());
                vo.setCreateTime(a.getCreateTime());
                vo.setHandleTime(a.getHandleTime());
                vo.setPointsRecordId(a.getPointsRecordId());
                vo.setImageUrl(a.getImageUrl());

                // 通过 pointsRecordId 查积分记录，再查预约记录获取时间
                if (a.getPointsRecordId() != null) {
                    PointsRecord record = pointsRecordMapper.selectById(a.getPointsRecordId());
                    if (record != null && record.getReservationId() != null) {
                        Reservation reservation = reservationService.getById(record.getReservationId());
                        if (reservation != null) {
                            vo.setReporterReservationStartTime(reservation.getStartTime());
                            vo.setReporterReservationEndTime(reservation.getEndTime());
                        }
                    }
                }

                allRecords.add(vo);
            }
        }

        // 按创建时间倒序排序
        allRecords.sort(Comparator.comparing(DisputeVO::getCreateTime).reversed());

        long total = allRecords.size();

        // 手动分页
        int offset = (current - 1) * size;
        int end = Math.min(offset + size, allRecords.size());
        List<DisputeVO> pageRecords = offset < allRecords.size()
                ? allRecords.subList(offset, end)
                : new ArrayList<>();

        return new PageResult<>(pageRecords, total, current, size);
    }

    @Override
    public void handleDispute(Long id, Long handlerId, HandleDisputeDTO dto) {
        if ("complaint".equals(dto.getType())) {
            log.info("分发处理举报: id={}, status={}", id, dto.getStatus());
            complaintService.handleComplaint(id, handlerId, dto.getStatus(), dto.getReply());
        } else if ("appeal".equals(dto.getType())) {
            log.info("分发处理申诉: id={}, status={}", id, dto.getStatus());
            appealService.handleAppeal(id, handlerId, dto.getStatus(), dto.getReply());
        } else {
            throw new RuntimeException("不支持的争议类型: " + dto.getType());
        }
    }
}
