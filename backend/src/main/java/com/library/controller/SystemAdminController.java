package com.library.controller;

import com.library.entity.PointsRecord;
import com.library.entity.ReservationRule;
import com.library.entity.User;
import com.library.aspect.Log;
import com.library.service.PointsService;
import com.library.service.ReservationRuleService;
import com.library.service.UserService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 系统管理员控制器
 */
@RestController
@RequestMapping("/system-admin")
@CrossOrigin
public class SystemAdminController {
    
    private static final Logger log = LoggerFactory.getLogger(SystemAdminController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointsService pointsService;
    
    @Autowired
    private ReservationRuleService reservationRuleService;
    
    /**
     * 获取所有用户（系统管理员）
     */
    @GetMapping("/users")
    public Result<PageResult> getAllUsers(@RequestParam(defaultValue = "1") int current,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String userType,
                                          HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            PageResult result = userService.getUserPageQuery(current, size, null, null, userType, null);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息（系统管理员）
     */
    @Log(value = "修改", module = "用户")
    @PutMapping("/user/{userId}")
    public Result updateUser(@PathVariable Long userId,
                            @RequestBody User updateUser,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        try {
            // 不允许修改用户类型为系统管理员
            if ("系统管理员".equals(updateUser.getUserType())) {
                return Result.error("不能将用户设置为系统管理员");
            }
            
            User existingUser = userService.getById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            existingUser.setRealName(updateUser.getRealName());
            existingUser.setPhone(updateUser.getPhone());
            existingUser.setEmail(updateUser.getEmail());
            existingUser.setUserType(updateUser.getUserType());
            existingUser.setStatus(updateUser.getStatus());
            
            userService.updateById(existingUser);
            return Result.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增图书馆管理员账号（系统管理员）
     */
    @Log(value = "新增", module = "管理员账号")
    @PostMapping("/librarian")
    public Result createLibrarian(@RequestBody User newUser, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        try {
            // 检查用户名是否已存在
            if (userService.getByUsername(newUser.getUsername()) != null) {
                return Result.error("用户名已存在");
            }
            
            newUser.setUserType("图书馆管理员");
            newUser.setStatus("正常");
            
            userService.save(newUser);
            
            // 初始化积分
            pointsService.initUserPoints(newUser.getId(), newUser.getUsername());
            
            return Result.success("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有用户积分记录（系统管理员）
     */
    @GetMapping("/points-records")
    public Result<PageResult> getAllPointsRecords(@RequestParam(defaultValue = "1") int current,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) String changeType,
                                                  HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            // 这里需要扩展PointsService以支持管理员查询所有记录
            PageResult records = pointsService.getAllPointsRecords(current, size, userId, changeType);
            return Result.success(records);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取积分记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有预约规则（系统管理员）
     */
    @GetMapping("/rules")
    public Result<List<ReservationRule>> getAllRules(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限访问");
        }
        
        try {
            List<ReservationRule> rules = reservationRuleService.getRulesList();
            return Result.success(rules);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取预约规则失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新单条预约规则（系统管理员）
     */
    @Log(value = "修改", module = "预约规则")
    @PutMapping("/rule")
    public Result updateRule(@RequestParam String ruleKey,
                             @RequestParam String ruleValue,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        try {
            // 记录修改日志
            String oldValue = reservationRuleService.getRuleValue(ruleKey);
            log.info("系统管理员 {} 修改规则 {}: {} -> {}", 
                    user.getUsername(), ruleKey, oldValue, ruleValue);
            
            reservationRuleService.updateRule(ruleKey, ruleValue);
            return Result.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新规则失败: " + e.getMessage());
        }
    }
}
