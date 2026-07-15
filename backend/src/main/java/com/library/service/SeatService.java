package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Seat;
import com.library.vo.PageResult;
import com.library.vo.SeatStatusVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 座位服务接口
 */
public interface SeatService extends IService<Seat> {
    
    /**
     * 根据图书馆ID查询座位列表
     */
    List<Seat> getByLibraryId(Long libraryId);
    
    /**
     * 查询可用座位（指定时间段内未被预约的座位）
     */
    List<Seat> getAvailableSeats(Long libraryId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取图书馆所有座位及其预约状态
     */
    List<SeatStatusVO> getAllSeatsWithStatus(Long libraryId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 分页查询座位
     */
    PageResult<Seat> pageQuery(int current, int size, Long libraryId, String seatNumber, String seatType, String status);
    
    /**
     * 更新座位状态
     */
    void updateStatus(Long seatId, String status);

    /**
     * 获取座位布局信息（含行列号）
     */
    List<Seat> getSeatsLayout(Long libraryId);

    /**
     * 获取座位布局信息（含预约状态和行列号）
     */
    List<SeatStatusVO> getAllSeatsWithStatusAndLayout(Long libraryId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取单个座位的实时状态（基于当前时间）
     */
    SeatStatusVO getSeatStatusForNow(Long seatId);

    /**
     * 获取实时监控布局（管理员用，基于当前时间判断预约状态）
     * 返回所有座位 + 当前时间点的预约状态
     */
    List<SeatStatusVO> getSeatMonitorLayout(Long libraryId);
}