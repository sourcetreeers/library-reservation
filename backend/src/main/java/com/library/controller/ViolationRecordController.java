package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import com.library.aspect.Log;
import com.library.entity.ViolationRecord;
import com.library.service.ViolationRecordService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 违规管理控制器（图书馆管理员端）
 */
@RestController
@RequestMapping("/admin/violation-record")
@CrossOrigin
public class ViolationRecordController {
    
    @Autowired
    private ViolationRecordService violationRecordService;
    
    /**
     * 分页查询违规记录
     */
    @GetMapping("/page")
    public Result<Page<ViolationRecord>> getPage(
            HttpSession session,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String violationType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        Page<ViolationRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<ViolationRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (userName != null && !userName.isEmpty()) {
            wrapper.like(ViolationRecord::getUserName, userName);
        }
        if (studentNo != null && !studentNo.isEmpty()) {
            wrapper.like(ViolationRecord::getStudentNo, studentNo);
        }
        if (violationType != null && !violationType.isEmpty()) {
            wrapper.eq(ViolationRecord::getViolationType, violationType);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ViolationRecord::getStatus, status);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(ViolationRecord::getViolationTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(ViolationRecord::getViolationTime, endTime + " 23:59:59");
        }
        
        wrapper.orderByDesc(ViolationRecord::getViolationTime);
        
        Page<ViolationRecord> result = violationRecordService.page(page, wrapper);
        return Result.success(result);
    }
    
    /**
     * 查看详情
     */
    @GetMapping("/{id}")
    public Result<ViolationRecord> getDetail(HttpSession session, @PathVariable Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        
        ViolationRecord record = violationRecordService.getById(id);
        return Result.success(record);
    }
    
    /**
     * 删除违规记录
     */
    @Log(value = "删除", module = "违规")
    @DeleteMapping("/{id}")
    public Result<Object> deleteRecord(HttpSession session, @PathVariable Long id) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }

        try {
            boolean removed = violationRecordService.removeById(id);
            if (removed) {
                return Result.success("删除成功");
            } else {
                return Result.error("记录不存在或已被删除");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 处理违规（执行处罚）
     */
    @Log(value = "处理", module = "违规")
    @PutMapping("/{id}/handle")
    public Result<Object> handleViolation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        Integer pointsDeduct = params.get("pointsDeduct") != null ? (Integer) params.get("pointsDeduct") : 0;
        Integer banDays = params.get("banDays") != null ? (Integer) params.get("banDays") : 0;
        String reply = params.get("reply") != null ? (String) params.get("reply") : "";
        
        try {
            violationRecordService.handleViolation(id, pointsDeduct, banDays, reply, user.getId(), user.getRealName());
            return Result.success("处罚成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 豁免违规
     */
    @Log(value = "豁免", module = "违规")
    @PutMapping("/{id}/exempt")
    public Result<Object> exemptViolation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        if (!"图书馆管理员".equals(user.getUserType()) && !"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限操作");
        }
        
        String reply = params.get("reply") != null ? (String) params.get("reply") : "";
        if (reply.isEmpty()) {
            return Result.error("请填写豁免理由");
        }
        
        try {
            violationRecordService.exemptViolation(id, reply, user.getId(), user.getRealName());
            return Result.success("豁免成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
