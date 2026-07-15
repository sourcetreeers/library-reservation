package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.aspect.Log;
import com.library.entity.PunishmentRule;
import com.library.entity.User;
import com.library.service.PunishmentRuleService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 违规规则配置控制器（系统管理员端）
 */
@RestController
@RequestMapping("/system-admin/punishment-rule")
@CrossOrigin
public class PunishmentRuleController {
    
    @Autowired
    private PunishmentRuleService punishmentRuleService;
    
    /**
     * 获取所有违规规则列表
     */
    @GetMapping("/list")
    public Result<List<PunishmentRule>> getList(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"系统管理员".equals(user.getUserType())) return Result.error(403, "无权限操作");
        
        LambdaQueryWrapper<PunishmentRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PunishmentRule::getViolationType)
               .orderByAsc(PunishmentRule::getApplyTimes);
        List<PunishmentRule> list = punishmentRuleService.list(wrapper);
        return Result.success(list);
    }
    
    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<Page<PunishmentRule>> getPage(
            HttpSession session,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String violationType,
            @RequestParam(required = false) Boolean isActive) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"系统管理员".equals(user.getUserType())) return Result.error(403, "无权限操作");
        
        Page<PunishmentRule> page = new Page<>(current, size);
        LambdaQueryWrapper<PunishmentRule> wrapper = new LambdaQueryWrapper<>();
        
        if (violationType != null && !violationType.isEmpty()) {
            wrapper.eq(PunishmentRule::getViolationType, violationType);
        }
        if (isActive != null) {
            wrapper.eq(PunishmentRule::getIsActive, isActive);
        }
        
        wrapper.orderByAsc(PunishmentRule::getViolationType)
               .orderByAsc(PunishmentRule::getApplyTimes);
        
        Page<PunishmentRule> result = punishmentRuleService.page(page, wrapper);
        return Result.success(result);
    }
    
    /**
     * 新增规则
     */
    @PostMapping
    public Result<Object> addRule(@RequestBody PunishmentRule rule, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"系统管理员".equals(user.getUserType())) return Result.error(403, "无权限操作");
        
        // 检查同类型同次数是否已有启用规则
        LambdaQueryWrapper<PunishmentRule> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(PunishmentRule::getViolationType, rule.getViolationType())
                    .eq(PunishmentRule::getApplyTimes, rule.getApplyTimes())
                    .eq(PunishmentRule::getIsActive, true);
        long count = punishmentRuleService.count(checkWrapper);
        if (count > 0) {
            return Result.error("该类型和适用次数的规则已存在，请先停用旧规则");
        }
        
        punishmentRuleService.save(rule);
        return Result.success("新增成功");
    }
    
    /**
     * 更新规则
     */
    @PutMapping
    public Result<Object> updateRule(@RequestBody PunishmentRule rule, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"系统管理员".equals(user.getUserType())) return Result.error(403, "无权限操作");
        
        punishmentRuleService.updateById(rule);
        return Result.success("更新成功");
    }
    
    /**
     * 删除规则
     */
    @Log(value = "删除", module = "违规规则")
    @DeleteMapping("/{id}")
    public Result<Object> deleteRule(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"系统管理员".equals(user.getUserType())) return Result.error(403, "无权限操作");
        
        punishmentRuleService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 切换规则启用状态
     */
    @PutMapping("/{id}/toggle")
    public Result<Object> toggleActive(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"系统管理员".equals(user.getUserType())) return Result.error(403, "无权限操作");
        
        PunishmentRule rule = punishmentRuleService.getById(id);
        if (rule != null) {
            rule.setIsActive(!rule.getIsActive());
            punishmentRuleService.updateById(rule);
        }
        return Result.success("操作成功");
    }
}
