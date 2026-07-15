package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 举报Mapper
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

    /**
     * 管理员分页查询举报
     */
    List<Complaint> selectComplaintPage(@Param("status") String status,
                                        @Param("offset") int offset,
                                        @Param("size") int size);

    /**
     * 统计举报总数
     */
    long countComplaints(@Param("status") String status);
}
