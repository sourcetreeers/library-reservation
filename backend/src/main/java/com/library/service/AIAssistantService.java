package com.library.service;

import com.library.entity.User;
import java.util.List;
import java.util.Map;

/**
 * AI智能助手服务接口
 * 
 * 定义AI助手的核心能力：
 * 1. 自然语言理解与意图识别
 * 2. Function Calling（工具调用）
 * 3. 对话上下文管理
 * 4. 敏感操作确认机制
 */
public interface AIAssistantService {
    
    /**
     * 处理用户消息（核心方法）
     * 
     * @param message 用户输入的自然语言消息
     * @param user 当前用户信息
     * @return 响应结果Map，包含:
     *         - reply: AI的文本回复
     *         - type: "text" | "operation_result" | "confirmation_required"
     *         - data: 操作结果数据（如果type为operation_result）
     *         - operationId: 待确认操作ID（如果type为confirmation_required）
     *         - functionCalled: 调用的函数名（如果有）
     */
    Map<String, Object> processMessage(String message, User user);
    
    /**
     * 执行已确认的敏感操作
     * 
     * @param operationId 操作ID
     * @param user 当前用户
     * @return 操作执行结果
     */
    Map<String, Object> executeConfirmedOperation(String operationId, User user);
    
    /**
     * 获取所有已注册的Function Schema
     * 
     * @return Function定义列表
     */
    List<Map<String, Object>> getRegisteredFunctions();
    
    /**
     * 清空指定用户的对话历史
     * 
     * @param userId 用户ID
     */
    void clearConversationHistory(Long userId);
}
