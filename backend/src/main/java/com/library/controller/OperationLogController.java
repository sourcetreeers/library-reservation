package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.entity.OperationLog;
import com.library.entity.User;
import com.library.service.OperationLogService;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/system-admin/operation-log")
@CrossOrigin
public class OperationLogController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    public Result<IPage<OperationLog>> getPage(
            HttpSession session,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String operateUser,
            @RequestParam(required = false) String operateType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        if (!"系统管理员".equals(user.getUserType())) {
            return Result.error(403, "无权限访问");
        }
        
        IPage<OperationLog> page = operationLogService.getLogPage(current, size, operateUser, operateType, startTime, endTime);
        return Result.success(page);
    }
}
