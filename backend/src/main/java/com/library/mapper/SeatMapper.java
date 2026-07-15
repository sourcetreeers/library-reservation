package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 座位Mapper接口
 */
@Mapper
public interface SeatMapper extends BaseMapper<Seat> {
    
    /**
     * 分页查询座位
     */
    List<Seat> selectPageWithCondition(@Param("libraryId") Long libraryId,
                                      @Param("seatNumber") String seatNumber,
                                      @Param("seatType") String seatType,
                                      @Param("status") String status,
                                      @Param("offset") int offset,
                                      @Param("size") int size);
    
    /**
     * 查询总数
     */
    long countWithCondition(@Param("libraryId") Long libraryId,
                           @Param("seatNumber") String seatNumber,
                           @Param("seatType") String seatType,
                           @Param("status") String status);
}