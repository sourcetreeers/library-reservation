package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Appeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 申诉Mapper接口
 */
@Mapper
public interface AppealMapper extends BaseMapper<Appeal> {

    /**
     * 查询指定积分记录是否有待审核的申诉
     */
    @Select("SELECT COUNT(1) FROM appeal WHERE points_record_id = #{pointsRecordId} AND status = '待审核'")
    int countPendingByPointsRecordId(@Param("pointsRecordId") Long pointsRecordId);

    /**
     * 分页查询所有申诉（管理员）
     */
    List<Appeal> selectAppealPage(@Param("status") String status,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    /**
     * 查询申诉总数（管理员）
     */
    long countAppeals(@Param("status") String status);
}
