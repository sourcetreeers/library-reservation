package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 分页查询用户列表（手写SQL）
     */
    List<User> selectUserPageWithCondition(@Param("offset") int offset,
                                          @Param("size") int size,
                                          @Param("username") String username,
                                          @Param("realName") String realName,
                                          @Param("userType") String userType,
                                          @Param("status") String status);
    
    /**
     * 查询用户总数
     */
    long countUserWithCondition(@Param("username") String username,
                               @Param("realName") String realName,
                               @Param("userType") String userType,
                               @Param("status") String status);
}