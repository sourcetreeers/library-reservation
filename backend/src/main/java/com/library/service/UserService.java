package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.User;
import com.library.vo.PageResult;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     */
    User login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     */
    User register(RegisterDTO registerDTO);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 分页查询用户列表（手写SQL）
     */
    PageResult<User> getUserPageQuery(int current, int size, String username, String realName, String userType, String status);
    
    /**
     * 切换用户状态
     */
    void toggleUserStatus(Long userId);
    
    /**
     * 修改用户类型
     */
    void changeUserType(Long userId, String userType);
}