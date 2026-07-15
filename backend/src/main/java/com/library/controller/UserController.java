package com.library.controller;

import com.library.entity.User;
import com.library.aspect.Log;
import com.library.service.UserService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(@RequestParam(defaultValue = "1") int current,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String username,
                                        @RequestParam(required = false) String realName,
                                        @RequestParam(required = false) String userType,
                                        @RequestParam(required = false) String status,
                                        HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "只有图书馆管理员可以查看用户列表");
        }
        
        // 图书馆管理员只能查看学生
        if ("图书馆管理员".equals(user.getUserType())) {
            userType = "学生"; // 强制查询学生
        }
        
        // 使用手写SQL分页查询
        PageResult<User> result = userService.getUserPageQuery(
            current, size, username, realName, userType, status);
        return Result.success(result);
    }
    
    /**
     * 切换用户状态（启用/禁用）
     */
    @Log(value = "修改", module = "用户")
    @PutMapping("/{id}/toggle-status")
    public Result<Object> toggleStatus(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "只有管理员可以操作用户状态");
        }
        
        try {
            // 图书馆管理员只能操作学生
            if ("图书馆管理员".equals(user.getUserType())) {
                User targetUser = userService.getById(id);
                if (targetUser == null || !"学生".equals(targetUser.getUserType())) {
                    return Result.error(403, "只能对学生进行操作");
                }
            }
            
            userService.toggleUserStatus(id);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改用户类型
     */
    @Log(value = "修改", module = "用户")
    @PutMapping("/{id}/change-type")
    public Result<Object> changeUserType(@PathVariable Long id, 
                                        @RequestBody java.util.Map<String, String> request, 
                                        HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "只有管理员可以修改用户类型");
        }
        
        // 图书馆管理员不能修改用户类型
        if ("图书馆管理员".equals(user.getUserType())) {
            return Result.error(403, "图书馆管理员不能修改用户类型");
        }
        
        String userType = request.get("userType");
        if (userType == null || (!userType.equals("学生") && !userType.equals("图书馆管理员"))) {
            return Result.error("用户类型只能是'学生'或'图书馆管理员'");
        }
        
        try {
            userService.changeUserType(id, userType);
            return Result.success("用户类型修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }
    
    /**
     * 更新当前用户信息
     */
    @PutMapping("/update")
    public Result<Object> updateUserInfo(@RequestBody User updateUser, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        try {
            // 只允许修改部分字段
            user.setRealName(updateUser.getRealName());
            user.setPhone(updateUser.getPhone());
            user.setEmail(updateUser.getEmail());
            
            userService.updateById(user);
            
            // 更新session中的用户信息
            session.setAttribute("user", user);
            
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}