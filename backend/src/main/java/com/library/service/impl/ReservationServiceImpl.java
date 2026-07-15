package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.config.SeatStatusWebSocketHandler;
import com.library.dto.ReservationDTO;
import com.library.entity.Reservation;
import com.library.entity.Seat;
import com.library.entity.SystemConfig;
import com.library.entity.User;
import com.library.entity.UserPoints;
import com.library.entity.ViolationRecord;
import com.library.entity.PunishmentRule;
import com.library.service.PunishmentRuleService;
import com.library.service.UserService;
import com.library.service.ViolationRecordService;
import com.library.mapper.ReservationMapper;
import com.library.service.PointsService;
import com.library.service.ReservationRuleService;
import com.library.service.ReservationService;
import com.library.service.SeatService;
import com.library.service.SystemConfigService;
import com.library.utils.OrderNoUtils;
import com.library.vo.PageResult;
import com.library.vo.ReservationVO;
import com.library.vo.SeatStatusVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * 预约服务实现类
 */
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {
    
    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private PointsService pointsService;
    
    @Autowired
    private SeatService seatService;
    
    @Autowired
    private ReservationRuleService reservationRuleService;
    
    @Autowired
    private ViolationRecordService violationRecordService;
    
    @Autowired
    private PunishmentRuleService punishmentRuleService;
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional
    public Reservation createReservation(Long userId, ReservationDTO reservationDTO) {
        // 验证预约时间
        LocalDateTime now = LocalDateTime.now();
        if (reservationDTO.getStartTime().isBefore(now)) {
            throw new RuntimeException("预约时间不能是过去时间");
        }
        
        if (reservationDTO.getEndTime().isBefore(reservationDTO.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        // 校验预约时间段是否在允许范围内
        String startTimeStr = reservationDTO.getStartTime().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        String endTimeStr = reservationDTO.getEndTime().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        List<ReservationRuleService.TimeSlot> slots = reservationRuleService.getAvailableTimeSlots();
        boolean inSlot = false;
        for (ReservationRuleService.TimeSlot slot : slots) {
            if (startTimeStr.compareTo(slot.getStart()) >= 0 && endTimeStr.compareTo(slot.getEnd()) <= 0) {
                inSlot = true;
                break;
            }
        }
        if (!inSlot) {
            throw new RuntimeException("预约时间不在允许的时间段内，当前可预约时段：" + 
                slots.stream().map(s -> s.getStart() + "-" + s.getEnd()).collect(java.util.stream.Collectors.joining(", ")));
        }
        
        // 校验单次最长时长 - 基于用户信用等级
        UserPoints userPoints = pointsService.getUserPoints(userId);
        if (userPoints != null) {
            Integer userMaxHours = userPoints.getMaxReservationHours();
            if (userMaxHours != null && userMaxHours > 0) {
                // 使用分钟计算，更精确
                long durationMinutes = java.time.Duration.between(reservationDTO.getStartTime(), reservationDTO.getEndTime()).toMinutes();
                long maxMinutes = userMaxHours * 60L;
                if (durationMinutes > maxMinutes) {
                    throw new RuntimeException("您的信用等级最多可预约" + userMaxHours + "小时");
                }
            } else if (userMaxHours != null && userMaxHours == 0) {
                throw new RuntimeException("您的账户已被暂停服务，无法预约");
            }
        }
        
        // 校验单日预约次数 - 基于用户信用等级
        if (userPoints != null) {
            Integer userMaxCount = userPoints.getMaxReservationCount();
            if (userMaxCount != null && userMaxCount > 0) {
                // 查询当天该用户已预约的次数（状态为已预约或已使用）
                LocalDateTime dayStart = reservationDTO.getStartTime().toLocalDate().atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1);
                
                LambdaQueryWrapper<Reservation> countWrapper = new LambdaQueryWrapper<>();
                countWrapper.eq(Reservation::getUserId, userId)
                           .ge(Reservation::getStartTime, dayStart)
                           .lt(Reservation::getStartTime, dayEnd)
                           .in(Reservation::getStatus, "已预约", "已使用");
                
                long todayCount = count(countWrapper);
                if (todayCount >= userMaxCount) {
                    throw new RuntimeException("今日预约次数已达上限（" + userMaxCount + "次）");
                }
            } else if (userMaxCount != null && userMaxCount == 0) {
                throw new RuntimeException("您的账户已被暂停服务，无法预约");
            }
        }
        
        // 检查用户时间冲突
        int userConflict = baseMapper.checkUserTimeConflict(
                userId, reservationDTO.getStartTime(), reservationDTO.getEndTime(), null);
        if (userConflict > 0) {
            throw new RuntimeException("您在该时间段已有预约，不能重复预约");
        }
        
        // 检查座位时间冲突
        int seatConflict = baseMapper.checkSeatTimeConflict(
                reservationDTO.getSeatId(), reservationDTO.getStartTime(), reservationDTO.getEndTime(), null);
        if (seatConflict > 0) {
            throw new RuntimeException("该座位在指定时间段已被预约");
        }
        
        // 生成流水号
        String orderNo = generateOrderNo();
        
        // 创建预约记录
        Reservation reservation = new Reservation();
        reservation.setOrderNo(orderNo);
        reservation.setUserId(userId);
        reservation.setLibraryId(reservationDTO.getLibraryId());
        reservation.setSeatId(reservationDTO.getSeatId());
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        reservation.setStatus("已预约");
        reservation.setQrCode(orderNo); // 二维码内容就是流水号
        
        save(reservation);
        
        // 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservationDTO.getLibraryId(), reservationDTO.getSeatId());
        
        return reservation;
    }
    
    @Override
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("只能取消自己的预约");
        }
        
        if (!"已预约".equals(reservation.getStatus())) {
            throw new RuntimeException("只能取消未使用的预约");
        }
        
        reservation.setStatus("已取消");
        updateById(reservation);
        
        // 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    public void adminCancelReservation(Long reservationId) {
        Reservation reservation = getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!"已预约".equals(reservation.getStatus())) {
            throw new RuntimeException("只能取消未使用的预约");
        }
        
        reservation.setStatus("已取消");
        updateById(reservation);
        
        // 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    public void checkIn(String orderNo) {
        Reservation reservation = getByOrderNo(orderNo);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!"已预约".equals(reservation.getStatus())) {
            throw new RuntimeException("该预约已失效");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 检查是否可以签到（允许提前20分钟签到）
        LocalDateTime earliestCheckInTime = reservation.getStartTime().minusMinutes(20);
        if (now.isBefore(earliestCheckInTime)) {
            throw new RuntimeException("未到签到时间，可提前20分钟签到");
        }
        
        // 检查是否已过期（超过结束时间）
        if (now.isAfter(reservation.getEndTime())) {
            throw new RuntimeException("预约已过期，无法签到");
        }
        
        // 允许迟到，但不能超过结束时间
        reservation.setStatus("已使用");
        reservation.setCheckInTime(now);
        updateById(reservation);
        
        // 正常履约加分
        try {
            pointsService.addPointsForNormalCheckIn(reservation.getUserId(), reservation.getId());
        } catch (Exception e) {
            log.error("签到加分失败: " + e.getMessage());
        }

        // 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    public IPage<ReservationVO> getReservationPage(int current, int size, Long userId, String status) {
        Page<ReservationVO> page = new Page<>(current, size);
        return baseMapper.selectReservationPage(page, userId, status);
    }
    
    @Override
    public PageResult<ReservationVO> getReservationPageQuery(int current, int size, Long userId, String status, 
                                                            String userName, Long libraryId, String seatNumber) {
        // 计算偏移量
        int offset = (current - 1) * size;
        
        // 查询数据
        List<ReservationVO> records = baseMapper.selectReservationPageWithCondition(
            userId, status, userName, libraryId, seatNumber, offset, size);
        
        // 查询总数
        long total = baseMapper.countReservationWithCondition(userId, status, userName, libraryId, seatNumber);
        
        // 返回分页结果
        return new PageResult<>(records, total, current, size);
    }
    
    @Override
    public Reservation getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getOrderNo, orderNo);
        return getOne(wrapper);
    }
    
    @Override
    public ReservationVO getReservationDetailByOrderNo(String orderNo) {
        return baseMapper.selectReservationDetailByOrderNo(orderNo);
    }
    
    @Override
    @Transactional
    public void handleExpiredReservations() {
        List<Reservation> expiredReservations = baseMapper.selectExpiredReservations();
        
        for (Reservation reservation : expiredReservations) {
            reservation.setStatus("爽约");
            updateById(reservation);
            
            // 根据规则表动态扣分 + 创建违规记录 + 封禁（一步完成）
            try {
                processViolationByRule(reservation, "爽约");
            } catch (Exception e) {
                log.error("爽约处理失败: " + e.getMessage());
            }
            
            // 广播座位状态变更（推送完整座位数据）
            broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
        }
        
        log.info("处理过期预约记录：" + expiredReservations.size() + " 条");
    }
    
    @Override
    @Transactional
    public void handleSoonExpiredReservations() {
        List<Reservation> soonExpiredReservations = baseMapper.selectSoonExpiredReservations();
        
        for (Reservation reservation : soonExpiredReservations) {
            // 标记为爽约，释放座位
            reservation.setStatus("爽约");
            updateById(reservation);
            
            // 释放座位
            Seat seat = seatService.getById(reservation.getSeatId());
            if (seat != null) {
                seat.setStatus("正常");
                seatService.updateById(seat);
            }
            
            // 根据规则表动态扣分 + 创建违规记录 + 封禁
            try {
                processViolationByRule(reservation, "爽约");
            } catch (Exception e) {
                log.error("爽约处理失败: " + e.getMessage());
            }
            
            // 广播座位状态变更（推送完整座位数据）
            broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
            
            log.info("释放爽约座位：订单号={}, 用户ID={}, 座位ID={}", 
                    reservation.getOrderNo(), reservation.getUserId(), reservation.getSeatId());
        }
        
        log.info("处理即将过期预约记录：" + soonExpiredReservations.size() + " 条");
    }
    
    @Override
    @Transactional
    public void handleNoShowReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询已预约且开始时间已过但未签到的记录
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getStatus, "已预约")
               .le(Reservation::getStartTime, now); // 开始时间 <= 当前时间
        
        List<Reservation> noShowReservations = list(wrapper);
        
        for (Reservation reservation : noShowReservations) {
            // 标记为爽约
            reservation.setStatus("爽约");
            updateById(reservation);
            
            // 释放座位
            Seat seat = seatService.getById(reservation.getSeatId());
            if (seat != null) {
                seat.setStatus("正常");
                seatService.updateById(seat);
            }
            
            // 根据规则表动态扣分 + 创建违规记录 + 封禁（一步完成）
            try {
                processViolationByRule(reservation, "爽约");
            } catch (Exception e) {
                log.error("爽约处理失败: " + e.getMessage());
            }
            
            // 广播座位状态变更（推送完整座位数据）
            broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
            
            log.info("处理爽约预约：订单号={}, 用户ID={}, 座位ID={}", 
                    reservation.getOrderNo(), reservation.getUserId(), reservation.getSeatId());
        }
        
        log.info("处理未签到爽约记录：" + noShowReservations.size() + " 条");
    }

    public static void m666(String[] args) {
        String src = "我是牛皮癣";

        /* 1. 编码 → 字符串 */
        String encoded = Base64.getUrlEncoder()          // 也可用 getEncoder()
                .encodeToString(src.getBytes(StandardCharsets.UTF_8));
        System.out.println("编码后 = " + encoded);

        /* 2. 解码 → 还原 */
        byte[] decodedBytes = Base64.getUrlDecoder()
                .decode(encoded);
        String decoded = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println("还原后 = " + decoded);
    }

    private void say(){
        String encoded = "5Li05rKC5aSn5a2mMjAyNOe6p+avleS4muiuvuiuoemhueebru+8jDIwMjQxMDg5MjPkuo4yMDI1LTEyLTEy5byA5Y+R";
        encoded = "5Li05rKC5aSn5a2mMjAyNOe6p-avleS4muiuvuiuoemhueebru-8jDIwMjQxMDg5MjPkuo4yMDI1LTEyLTEy5byA5Y-R";
        log.info(encoded);
    }
    
    /**
     * 生成预约流水号
     */
    private synchronized String generateOrderNo() {
        this.say();
        // 获取当日流水号
        SystemConfig config = systemConfigService.getByKey("daily_order_sequence");
        int sequence = Integer.parseInt(config.getConfigValue()) + 1;
        
        // 更新流水号
        config.setConfigValue(String.valueOf(sequence));
        systemConfigService.updateById(config);
        
        return OrderNoUtils.generateOrderNo(sequence);
    }
    
    @Override
    @Transactional
    public void studentCheckIn(Long userId, String code) {
        // 查找进行中的预约（通过流水号或订单号）
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId)
               .eq(Reservation::getStatus, "已预约")
               .and(w -> w.eq(Reservation::getOrderNo, code)
                          .or()
                          .eq(Reservation::getQrCode, code));
        
        Reservation reservation = getOne(wrapper);
        if (reservation == null) {
            throw new RuntimeException("未找到有效的预约记录");
        }
        
        // 检查是否可以签到（允许提前20分钟签到）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestCheckInTime = reservation.getStartTime().minusMinutes(20);
        
        if (now.isBefore(earliestCheckInTime)) {
            throw new RuntimeException("未到签到时间，可提前20分钟签到");
        }
        
        // 检查是否已过期
        if (now.isAfter(reservation.getEndTime())) {
            throw new RuntimeException("预约已过期，无法签到");
        }
        
        // 更新状态为已使用
        reservation.setStatus("已使用");
        reservation.setCheckInTime(now);
        updateById(reservation);
        
        // 正常履约加分
        try {
            pointsService.addPointsForNormalCheckIn(reservation.getUserId(), reservation.getId());
        } catch (Exception e) {
            log.error("签到加分失败: " + e.getMessage());
        }

        // 学生签到 - 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    @Transactional
    public void studentCheckOut(Long userId) {
        // 查找使用中的预约
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId)
               .eq(Reservation::getStatus, "已使用");
        
        Reservation reservation = getOne(wrapper);
        if (reservation == null) {
            throw new RuntimeException("当前没有使用中的预约");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInTime = reservation.getCheckInTime();
        
        // 计算使用时长占比
        long totalMinutes = java.time.Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
        long usedMinutes = java.time.Duration.between(checkInTime, now).toMinutes();
        double usageRatio = (double) usedMinutes / totalMinutes;
        
        // 更新状态
        reservation.setStatus("已完成");
        reservation.setCheckOutTime(now);
        updateById(reservation);
        
        // 如果使用时长达到60%，给予提前签退奖励
        if (usageRatio >= 0.6) {
            try {
                pointsService.addPoints(userId, 1, "提前签退", 
                    "RESERVATION", reservation.getId(), 
                    String.format("提前签退，使用时长%.0f%%", usageRatio * 100));
            } catch (Exception e) {
                log.error("提前签退加分失败: " + e.getMessage());
            }
        }
        
        // 释放座位
        Seat seat = seatService.getById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus("正常");
            seatService.updateById(seat);
        }

        // 学生签退/签退 - 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    @Transactional
    public void markTempLeave(Long userId) {
        // 查找使用中的预约
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId)
               .eq(Reservation::getStatus, "已使用");
        
        Reservation reservation = getOne(wrapper);
        if (reservation == null) {
            throw new RuntimeException("当前没有使用中的预约");
        }
        
        // 更新预约状态为暂离
        reservation.setStatus("暂离");
        reservation.setTempLeaveTime(LocalDateTime.now());
        updateById(reservation);

        // 暂离 - 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    @Transactional
    public void cancelTempLeave(Long userId) {
        // 查找暂离状态的预约
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getUserId, userId)
               .eq(Reservation::getStatus, "暂离");
        
        Reservation reservation = getOne(wrapper);
        if (reservation == null) {
            throw new RuntimeException("当前没有暂离状态的预约");
        }
        
        // 恢复为已使用状态
        reservation.setStatus("已使用");
        updateById(reservation);

        // 取消暂离 - 广播座位状态变更（推送完整座位数据）
        broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
    }
    
    @Override
    @Transactional
    public void handleTempLeaveTimeout() {
        LocalDateTime now = LocalDateTime.now();
        // 从预约规则表读取暂离超时时间，读取失败则回退20分钟
        int timeoutMinutes = 20;
        try {
            String timeoutStr = reservationRuleService.getRuleValue("temp_leave_timeout");
            if (timeoutStr != null && !timeoutStr.isEmpty()) {
                timeoutMinutes = Integer.parseInt(timeoutStr);
            }
        } catch (Exception e) {
            log.warn("读取暂离超时时间失败，使用默认值20分钟", e);
        }
        LocalDateTime timeoutThreshold = now.minusMinutes(timeoutMinutes);
        
        // 查询暂离状态且超过超时阈值的预约（使用update_time，因为markTempLeave()调用updateById时自动更新了该字段）
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Reservation> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("status", "暂离")
               .le("update_time", timeoutThreshold); // 更新时间(≈暂离时间) <= 超时阈值
        
        List<Reservation> timeoutReservations = list(wrapper);
        
        for (Reservation reservation : timeoutReservations) {
            // 标记为爽约
            reservation.setStatus("爽约");
            updateById(reservation);

            // 释放座位
            Seat seat = seatService.getById(reservation.getSeatId());
            if (seat != null) {
                seat.setStatus("正常");
                seatService.updateById(seat);
            }

            // 根据规则表动态扣分 + 创建违规记录 + 封禁（一步完成）
            try {
                processViolationByRule(reservation, "暂离超时");
            } catch (Exception e) {
                log.error("暂离超时处理失败: " + e.getMessage());
            }

            // 广播座位状态变更（推送完整座位数据）
            broadcastSeatChange(reservation.getLibraryId(), reservation.getSeatId());
            
            log.info("处理暂离超时：订单号={}, 用户ID={}, 暂离时间={}, 超时阈值={}分钟", 
                    reservation.getOrderNo(), reservation.getUserId(), reservation.getTempLeaveTime(), timeoutMinutes);
        }
        
        if (!timeoutReservations.isEmpty()) {
            log.info("处理暂离超时记录：{} 条，超时阈值={}分钟", timeoutReservations.size(), timeoutMinutes);
        }
    }
    
    /**
     * 根据规则表统一处理违规：查询规则 → 扣分 → 创建违规记录 → 执行封禁
     * 所有定时任务（爽约/暂离超时）统一调用此方法，确保积分扣除和违规记录一致
     */
    private void processViolationByRule(Reservation reservation, String violationType) {
        try {
            Long userId = reservation.getUserId();
            User user = userService.getById(userId);
            if (user == null) {
                log.error("违规处理失败：用户不存在, userId={}", userId);
                return;
            }

            // 1. 统计该用户此类型已有的违规记录数量（用于匹配累犯规则）
            long existingCount = violationRecordService.countHandledViolations(userId, violationType);

            // 2. 根据规则表查询匹配的处罚规则
            PunishmentRule rule = punishmentRuleService.getMatchedRule(violationType, (int) existingCount);

            // 默认值（兜底：如果未配置规则）
            int pointsAbs = getDefaultPoints(violationType);  // 正数绝对值
            int banDays = 0;

            if (rule != null) {
                pointsAbs = Math.abs(rule.getPointsDeduct() != null ? rule.getPointsDeduct() : pointsAbs);
                banDays = rule.getBanDays() != null ? rule.getBanDays() : 0;
                log.info("命中违规规则: 类型={}, 已有次数={}→适用=第{}次, 扣分={}分, 封禁={}天",
                        violationType, existingCount, formatApplyTimes(rule.getApplyTimes()),
                        pointsAbs, banDays);
            } else {
                log.warn("未找到违规规则匹配，使用默认值: 类型={}, 已有次数={}, 默认扣分={}分",
                        violationType, existingCount, pointsAbs);
            }

            // 3. 执行积分扣除（传入动态扣分值）
            if ("爽约".equals(violationType)) {
                pointsService.deductPointsForNoShow(userId, reservation.getId(), pointsAbs);
            } else if ("暂离超时".equals(violationType)) {
                pointsService.deductPointsForTempLeaveTimeout(userId, reservation.getId(), pointsAbs);
            } else {
                // 其他类型通用扣分
                pointsService.deductPoints(userId, pointsAbs, violationType, "系统检测",
                        reservation.getId(), violationType + "自动处理");
            }

            // 4. 创建违规记录
            ViolationRecord record = new ViolationRecord();
            record.setUserId(userId);
            record.setUserName(user.getRealName());
            record.setStudentNo(user.getUsername());
            record.setViolationType(violationType);
            record.setReservationId(reservation.getId());
            record.setOrderNo(reservation.getOrderNo());
            record.setPointsDeducted(-pointsAbs);   // 违规记录中存负数
            record.setBanDays(banDays);
            record.setStatus("已处理");              // 系统自动生成即为已处理
            record.setHandleTime(LocalDateTime.now());
            record.setViolationTime(LocalDateTime.now());
            violationRecordService.createViolation(record);

            // 5. 如果需要封禁，执行封禁
            if (banDays > 0 && user.getStatus() != null) {
                String banUntil = LocalDateTime.now().plusDays(banDays)
                        .toString().replace("T", " ").substring(0, 19);
                user.setStatus("封禁中_" + banUntil);
                userService.updateById(user);
                log.info("自动封禁用户: 用户={}, 封禁至={}", user.getRealName(), banUntil);
            }

            log.info("违规处理完成: 类型={}, 用户={}, 订单号={}, 扣分={}分, 封禁={}天",
                    violationType, user.getRealName(), reservation.getOrderNo(), pointsAbs, banDays);

        } catch (Exception e) {
            log.error("违规处理失败: " + e.getMessage(), e);
            throw e;  // 抛出让调用方 catch 并继续下一条
        }
    }

    /** 获取默认扣分绝对值（兜底用） */
    private int getDefaultPoints(String violationType) {
        switch (violationType) {
            case "爽约": return 6;
            case "暂离超时": return 3;
            default: return 3;
        }
    }

    private String formatApplyTimes(String applyTimes) {
        if (applyTimes == null) return "";
        switch (applyTimes) {
            case "FIRST": return "1";
            case "SECOND": return "2";
            case "THIRD_PLUS": return "3+";
            default: return applyTimes;
        }
    }

    /**
     * 广播单个座位状态变更（推送完整 SeatStatusVO 数据）
     */
    private void broadcastSeatChange(Long libraryId, Long seatId) {
        try {
            SeatStatusVO seatVO = seatService.getSeatStatusForNow(seatId);
            if (seatVO != null) {
                SeatStatusWebSocketHandler.broadcastSeatChange(libraryId, seatVO);
            }
        } catch (Exception e) {
            log.error("推送座位状态变更失败: libraryId={}, seatId={}", libraryId, seatId, e);
        }
    }
}