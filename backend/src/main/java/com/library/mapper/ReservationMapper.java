package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Reservation;
import com.library.vo.ReservationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约记录Mapper接口
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    /**
     * 查询预约记录详情列表（MyBatis Plus分页）
     */
    IPage<ReservationVO> selectReservationPage(Page<ReservationVO> page, 
                                               @Param("userId") Long userId,
                                               @Param("status") String status);
    
    /**
     * 分页查询预约记录详情列表（手写SQL）
     */
    List<ReservationVO> selectReservationPageWithCondition(@Param("userId") Long userId,
                                                          @Param("status") String status,
                                                          @Param("userName") String userName,
                                                          @Param("libraryId") Long libraryId,
                                                          @Param("seatNumber") String seatNumber,
                                                          @Param("offset") int offset,
                                                          @Param("size") int size);
    
    /**
     * 查询预约记录总数
     */
    long countReservationWithCondition(@Param("userId") Long userId,
                                      @Param("status") String status,
                                      @Param("userName") String userName,
                                      @Param("libraryId") Long libraryId,
                                      @Param("seatNumber") String seatNumber);
    
    /**
     * 检查座位时间冲突
     */
    int checkSeatTimeConflict(@Param("seatId") Long seatId,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("excludeId") Long excludeId);
    
    /**
     * 检查用户时间冲突
     */
    int checkUserTimeConflict(@Param("userId") Long userId,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("excludeId") Long excludeId);
    
    /**
     * 查询过期未签到的预约记录
     */
    List<Reservation> selectExpiredReservations();
    
    /**
     * 查询即将过期的预约记录（距离结束时间不足20分钟）
     */
    List<Reservation> selectSoonExpiredReservations();
    
    /**
     * 根据流水号查询预约记录详情
     */
    ReservationVO selectReservationDetailByOrderNo(@Param("orderNo") String orderNo);
}