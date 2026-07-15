package com.library.controller;

import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.aspect.Log;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @Log(value = "登录", module = "系统")
    @PostMapping("/login")
    public Result<User> login(@Validated @RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            User user = userService.login(loginDTO);
            // 将用户信息存入session
            session.setAttribute("user", user);
            // 不返回密码
            user.setPassword(null);
            return Result.success("登录成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            // 不返回密码
            user.setPassword(null);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Object> logout(HttpSession session) {
        session.removeAttribute("user");
        return Result.success("登出成功");
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }
}