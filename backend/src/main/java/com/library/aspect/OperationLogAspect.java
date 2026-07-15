package com.library.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.entity.OperationLog;
import com.library.entity.User;
import com.library.service.OperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志AOP切面
 * 通过自定义注解 @Log 记录管理员的操作行为
 */
@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired
    private OperationLogService operationLogService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        
        // 1. 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 2. 获取当前用户（从session中获取，这里通过request attribute或直接从session获取）
        User user = null;
        if (attributes != null) {
            user = (User) attributes.getRequest().getSession().getAttribute("user");
        }
        
        // 3. 执行原方法
        Object result = null;
        boolean success = false;
        try {
            result = joinPoint.proceed();
            success = true;
        } catch (Throwable e) {
            success = false;
            throw e;
        } finally {
            // 4. 记录日志
            try {
                recordLog(joinPoint, log, request, user, success, result);
            } catch (Exception ignored) {
                // 日志记录失败不影响主流程
            }
        }
        
        return result;
    }
    
    private void recordLog(ProceedingJoinPoint joinPoint, Log log, 
                           HttpServletRequest request, User user, boolean success, Object result) {
        OperationLog operationLog = new OperationLog();
        
        // 操作人信息
        if (user != null) {
            operationLog.setOperateUser(user.getUsername());
            operationLog.setOperateUserId(user.getId());
        } else {
            operationLog.setOperateUser("未知用户");
        }
        
        // 操作类型（优先使用注解值，否则根据HTTP方法推断）
        operationLog.setOperateType(log.value());
        
        // 操作对象（使用模块名称）
        operationLog.setTargetType(log.module());
        
        // IP地址和UA
        if (request != null) {
            operationLog.setIpAddress(getClientIp(request));
            operationLog.setUserAgent(request.getHeader("User-Agent"));
        }
        
        // 操作时间
        operationLog.setCreateTime(LocalDateTime.now());
        
        // 构造详情JSON
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put("method", joinPoint.getSignature().getName());
        detailMap.put("success", success);
        
        // 提取参数信息
        Object[] args = joinPoint.getArgs();
        StringBuilder paramInfo = new StringBuilder("[");
        for (Object arg : args) {
            if (!(arg instanceof MultipartFile) && !(arg instanceof javax.servlet.http.HttpSession)) {
                try {
                    paramInfo.append(objectMapper.writeValueAsString(arg)).append(",");
                } catch (Exception ignored) {}
            }
        }
        paramInfo.append("]");
        detailMap.put("params", paramInfo.toString());
        
        try {
            operationLog.setDetail(objectMapper.writeValueAsString(detailMap));
        } catch (Exception e) {
            operationLog.setDetail(detailMap.toString());
        }
        
        // 异步保存日志
        operationLogService.log(operationLog);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
