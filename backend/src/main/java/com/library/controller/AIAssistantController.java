package com.library.controller;

import com.library.vo.Result;
import com.library.entity.User;
import com.library.service.AIAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * AI智能助手控制器
 * 
 * 提供基于LLM的自然语言交互接口，
 * 通过Function Calling机制实现增删改查操作的自动化执行
 * 
 * @author AI Assistant
 * @version 1.0.0
 */
@RestController
@RequestMapping("/system-admin/ai-assistant")
public class AIAssistantController {

    @Autowired
    private AIAssistantService aiAssistantService;

    /**
     * 发送消息给AI助手（流式响应）
     * 
     * @param message 用户输入的消息
     * @param session 当前会话（用于权限校验和用户信息获取）
     * @return AI助手的响应结果（包含文本回复或操作结果）
     */
    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, String> request, HttpSession session) {
        // 1. 登录校验
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("未登录");
        }
        
        // 2. 管理员权限校验（仅图书馆管理员和系统管理员可使用AI助手）
        String userType = user.getUserType();
        if (!"图书馆管理员".equals(userType) && !"系统管理员".equals(userType)) {
            return Result.error("权限不足：仅管理员可使用AI助手");
        }
        
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }
        
        try {
            // 2. 调用AI服务处理消息
            Map<String, Object> response = aiAssistantService.processMessage(message, user);
            
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("AI助手处理失败: " + e.getMessage());
        }
    }

    /**
     * 确认敏感操作（二次确认）
     * 当AI检测到删除/禁用等危险操作时，会先询问用户是否确认
     * 用户确认后调用此接口执行实际操作
     * 
     * @param operationId 操作ID（由AI在首次响应中返回）
     * @param session 会话信息
     * @return 操作执行结果
     */
    @PostMapping("/confirm")
    public Result confirmOperation(@RequestBody Map<String, String> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("未登录");
        }
        
        // 管理员权限校验
        String userType = user.getUserType();
        if (!"图书馆管理员".equals(userType) && !"系统管理员".equals(userType)) {
            return Result.error("权限不足：仅管理员可使用AI助手");
        }
        
        String operationId = request.get("operationId");
        if (operationId == null || operationId.trim().isEmpty()) {
            return Result.error("操作ID不能为空");
        }
        
        try {
            Map<String, Object> result = aiAssistantService.executeConfirmedOperation(operationId, user);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取可用的Function列表（用于前端展示）
     * 
     * @return 所有已注册的Function Schema
     */
    @GetMapping("/functions")
    public Result getAvailableFunctions() {
        return Result.success(aiAssistantService.getRegisteredFunctions());
    }
    
    /**
     * 清空对话历史（重置AI上下文）
     * 
     * @param session 会话信息
     * @return 是否成功
     */
    @PostMapping("/clear-history")
    public Result clearHistory(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("未登录");
        }
        
        // 管理员权限校验
        String userType = user.getUserType();
        if (!"图书馆管理员".equals(userType) && !"系统管理员".equals(userType)) {
            return Result.error("权限不足：仅管理员可使用AI助手");
        }
        
        aiAssistantService.clearConversationHistory(user.getId());
        return Result.success(null);
    }
}
