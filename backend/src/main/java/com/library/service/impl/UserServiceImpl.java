package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.PointsService;
import com.library.service.UserService;
import com.library.utils.MD5Utils;
import com.library.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private PointsService pointsService;
    
    @Override
    public User login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!MD5Utils.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (!"正常".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }
        
        return user;
    }
    
    @Override
    public User register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (getByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(MD5Utils.encrypt(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setUserType("学生");
        user.setStatus("正常");
        
        // 保存用户
        save(user);
        
        // 初始化用户积分
        pointsService.initUserPoints(user.getId(), user.getUsername());
        
        return user;
    }
    
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }
    
    @Override
    public PageResult<User> getUserPageQuery(int current, int size, String username, String realName, String userType, String status) {
        // 计算偏移量
        int offset = (current - 1) * size;
        
        // 查询数据
        java.util.List<User> records = baseMapper.selectUserPageWithCondition(offset, size, username, realName, userType, status);
        
        // 查询总数
        long total = baseMapper.countUserWithCondition(username, realName, userType, status);
        
        return new PageResult<>(records, total, current, size);
    }
    
    @Override
    public void toggleUserStatus(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 不能禁用自己
        // 这里简化处理，实际应该从session中获取当前用户ID进行比较
        
        // 切换状态
        String newStatus = "正常".equals(user.getStatus()) ? "禁用" : "正常";
        user.setStatus(newStatus);
        updateById(user);
    }
    
    @Override
    public void changeUserType(Long userId, String userType) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证用户类型
        if (!userType.equals("学生") && !userType.equals("图书馆管理员")) {
            throw new RuntimeException("用户类型只能是'学生'或'图书馆管理员'");
        }
        
        // 修改用户类型
        user.setUserType(userType);
        updateById(user);
    }
}