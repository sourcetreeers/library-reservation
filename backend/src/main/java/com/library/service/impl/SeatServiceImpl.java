package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.library.entity.Seat;
import com.library.mapper.ReservationMapper;
import com.library.mapper.SeatMapper;
import com.library.service.SeatService;
import com.library.vo.PageResult;
import com.library.vo.SeatStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 座位服务实现类
 */
@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Override
    public List<Seat> getByLibraryId(Long libraryId) {
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getLibraryId, libraryId)
               .eq(Seat::getStatus, "正常");
        return list(wrapper);
    }
    
    @Override
    public List<Seat> getAvailableSeats(Long libraryId, LocalDateTime startTime, LocalDateTime endTime) {
        // 获取图书馆所有正常状态的座位
        List<Seat> allSeats = getByLibraryId(libraryId);
        
        // 过滤掉在指定时间段内有冲突的座位
        return allSeats.stream()
                .filter(seat -> {
                    int conflictCount = reservationMapper.checkSeatTimeConflict(
                            seat.getId(), startTime, endTime, null);
                    return conflictCount == 0;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SeatStatusVO> getAllSeatsWithStatus(Long libraryId, LocalDateTime startTime, LocalDateTime endTime) {
        System.out.println("=== 获取所有座位及其状态 ===");
        System.out.println("图书馆ID: " + libraryId);
        System.out.println("开始时间: " + startTime);
        System.out.println("结束时间: " + endTime);
        
        // 获取图书馆所有座位（包括正常、维修、停用）
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seat::getLibraryId, libraryId);
        List<Seat> allSeats = list(wrapper);
        System.out.println("找到座位数量: " + allSeats.size());
        
        // 标记每个座位是否被预约或处于不可用状态
        List<SeatStatusVO> result = allSeats.stream()
                .map(seat -> {
                    try {
                        // 检查座位是否被预约
                        int conflictCount = reservationMapper.checkSeatTimeConflict(
                                seat.getId(), startTime, endTime, null);
                        boolean isReserved = conflictCount > 0;
                        
                        // 如果座位状态不是"正常"，则视为不可用
                        boolean isUnavailable = !"正常".equals(seat.getStatus());
                        
                        System.out.println("座位 " + seat.getSeatNumber() + " (ID:" + seat.getId() + ") - 状态: " + seat.getStatus() + ", 冲突数: " + conflictCount + ", 已预约: " + isReserved + ", 不可用: " + isUnavailable);
                        return new SeatStatusVO(seat, isReserved || isUnavailable);
                    } catch (Exception e) {
                        System.err.println("检查座位 " + seat.getSeatNumber() + " 时出错: " + e.getMessage());
                        e.printStackTrace();
                        return new SeatStatusVO(seat, false);
                    }
                })
                .collect(Collectors.toList());
        
        System.out.println("=== 完成 ===");
        return result;
    }
    
    @Override
    public PageResult<Seat> pageQuery(int current, int size, Long libraryId, String seatNumber, String seatType, String status) {
        // 计算偏移量
        int offset = (current - 1) * size;
        
        // 查询数据
        List<Seat> records = baseMapper.selectPageWithCondition(libraryId, seatNumber, seatType, status, offset, size);
        
        // 查询总数
        long total = baseMapper.countWithCondition(libraryId, seatNumber, seatType, status);
        
        // 返回分页结果
        return new PageResult<>(records, total, current, size);
    }
    
    @Override
    public void updateStatus(Long seatId, String status) {
        Seat seat = getById(seatId);
        if (seat != null) {
            seat.setStatus(status);
            updateById(seat);
        }
    }

    @Override
    public List<Seat> getSeatsLayout(Long libraryId) {
        LambdaQueryWrapper<Seat> wrapper = Wrappers.<Seat>lambdaQuery()
                .eq(Seat::getLibraryId, libraryId)
                .eq(Seat::getStatus, "正常")
                .orderByAsc(Seat::getRowNum)
                .orderByAsc(Seat::getColNum);
        return list(wrapper);
    }

    @Override
    public List<SeatStatusVO> getAllSeatsWithStatusAndLayout(Long libraryId, LocalDateTime startTime, LocalDateTime endTime) {
        // 获取图书馆所有座位
        LambdaQueryWrapper<Seat> wrapper = Wrappers.<Seat>lambdaQuery()
                .eq(Seat::getLibraryId, libraryId)
                .orderByAsc(Seat::getRowNum)
                .orderByAsc(Seat::getColNum);
        List<Seat> allSeats = list(wrapper);

        // 标记每个座位是否被预约或处于不可用状态
        LocalDateTime now = LocalDateTime.now();
        return allSeats.stream()
                .map(seat -> {
                    int conflictCount = reservationMapper.checkSeatTimeConflict(
                            seat.getId(), startTime, endTime, null);
                    boolean isReserved = conflictCount > 0;
                    SeatStatusVO vo = new SeatStatusVO(seat, isReserved);
                    // 填充实时预约状态（用于监控页面区分已预约/使用中/暂离）
                    if (isReserved) {
                        vo.setReservationStatus(getCurrentReservationStatus(seat.getId(), now));
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SeatStatusVO getSeatStatusForNow(Long seatId) {
        Seat seat = getById(seatId);
        if (seat == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        int conflictCount = reservationMapper.checkSeatTimeConflict(seatId, now, now.plusMinutes(1), null);
        boolean isReserved = conflictCount > 0;
        SeatStatusVO vo = new SeatStatusVO(seat, isReserved);
        if (isReserved) {
            vo.setReservationStatus(getCurrentReservationStatus(seatId, now));
        }
        return vo;
    }

    @Override
    public List<SeatStatusVO> getSeatMonitorLayout(Long libraryId) {
        LambdaQueryWrapper<Seat> wrapper = Wrappers.<Seat>lambdaQuery()
                .eq(Seat::getLibraryId, libraryId)
                .orderByAsc(Seat::getRowNum)
                .orderByAsc(Seat::getColNum);
        List<Seat> allSeats = list(wrapper);

        LocalDateTime now = LocalDateTime.now();
        return allSeats.stream()
                .map(seat -> {
                    int conflictCount = reservationMapper.checkSeatTimeConflict(
                            seat.getId(), now, now.plusMinutes(1), null);
                    boolean isReserved = conflictCount > 0;
                    SeatStatusVO vo = new SeatStatusVO(seat, isReserved);
                    if (isReserved) {
                        vo.setReservationStatus(getCurrentReservationStatus(seat.getId(), now));
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询座位当前有效预约的状态（已预约/已使用/暂离）
     */
    private String getCurrentReservationStatus(Long seatId, LocalDateTime now) {
        LambdaQueryWrapper<com.library.entity.Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.library.entity.Reservation::getSeatId, seatId)
               .le(com.library.entity.Reservation::getStartTime, now)
               .ge(com.library.entity.Reservation::getEndTime, now)
               .in(com.library.entity.Reservation::getStatus, "已预约", "已使用", "暂离")
               .last("LIMIT 1");
        com.library.entity.Reservation reservation = reservationMapper.selectOne(wrapper);
        if (reservation != null) {
            String status = reservation.getStatus();
            if ("已使用".equals(status)) return "已使用";
            if ("暂离".equals(status)) return "暂离";
            return "已预约";
        }
        return "无";
    }
}