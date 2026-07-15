package com.library.controller;

import com.library.entity.User;
import com.library.entity.UserPoints;
import com.library.service.PointsService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 积分控制器
 */
@RestController
@RequestMapping("/points")
@CrossOrigin
public class PointsController {
    
    @Autowired
    private PointsService pointsService;
    
    /**
     * 获取当前用户积分信息
     */
    @GetMapping("/my-points")
    public Result<UserPoints> getMyPoints(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        
        try {
            UserPoints userPoints = pointsService.getUserPoints(user.getId());
            if (userPoints == null) {
                // 如果没有积分记录，初始化
                pointsService.initUserPoints(user.getId(), user.getUsername());
                userPoints = pointsService.getUserPoints(user.getId());
            }
            return Result.success(userPoints);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取积分信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定用户积分信息（管理员）
     */
    @GetMapping("/user/{userId}")
    public Result<UserPoints> getUserPoints(@PathVariable Long userId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType()))) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            UserPoints userPoints = pointsService.getUserPoints(userId);
            return Result.success(userPoints);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取积分信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取积分变动记录
     */
    @GetMapping("/records")
    public Result<PageResult> getRecords(@RequestParam(defaultValue = "1") int current,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        
        try {
            PageResult records = pointsService.getPointsRecords(current, size, user.getId());
            return Result.success(records);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取积分记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动调整积分（管理员）
     */
    @PostMapping("/adjust")
    public Result adjustPoints(@RequestParam Long userId,
                               @RequestParam Integer points,
                               @RequestParam String description,
                               HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        try {
            if (points > 0) {
                pointsService.addPoints(userId, points, "系统调整", "管理员操作", 
                                       null, description);
            } else {
                pointsService.deductPoints(userId, -points, "系统调整", "管理员操作", 
                                          null, description);
            }
            return Result.success("积分调整成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("积分调整失败: " + e.getMessage());
        }
    }
}
