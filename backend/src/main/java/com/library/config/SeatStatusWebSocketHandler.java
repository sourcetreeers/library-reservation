package com.library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.vo.SeatStatusVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 座位状态 WebSocket 处理器
 * 支持按图书馆分组订阅，推送完整座位状态数据
 */
public class SeatStatusWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SeatStatusWebSocketHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 所有在线 session（sessionId -> session）
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 按图书馆分组（libraryId -> Set of sessionIds）
    private static final Map<Long, Set<String>> libraryGroups = new ConcurrentHashMap<>();

    // session 所属的图书馆（sessionId -> libraryId）
    private static final Map<String, Long> sessionLibraryMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket 连接已建立: {}, 当前在线: {}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        removeSession(session.getId());
        log.info("WebSocket 连接已关闭: {}, 当前在线: {}", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Map<String, Object> msg = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) msg.get("type");

            // 处理订阅请求
            if ("SUBSCRIBE".equals(type)) {
                Object libraryIdObj = msg.get("libraryId");
                if (libraryIdObj != null) {
                    Long libraryId = Long.valueOf(libraryIdObj.toString());
                    subscribeToLibrary(session.getId(), libraryId);
                    log.debug("Session {} 订阅了图书馆 {}", session.getId(), libraryId);
                }
            }
            // 处理取消订阅请求
            else if ("UNSUBSCRIBE".equals(type)) {
                unsubscribeFromLibrary(session.getId());
                log.debug("Session {} 取消了订阅", session.getId());
            }
        } catch (Exception e) {
            log.debug("WebSocket 消息处理失败: {} - {}", message.getPayload(), e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 传输错误, session: {}", session.getId(), exception);
        removeSession(session.getId());
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            log.error("关闭 WebSocket session 失败", e);
        }
    }

    // ==================== 订阅管理 ====================

    private void subscribeToLibrary(String sessionId, Long libraryId) {
        // 先取消之前的订阅
        Long oldLibraryId = sessionLibraryMap.get(sessionId);
        if (oldLibraryId != null && !oldLibraryId.equals(libraryId)) {
            Set<String> oldGroup = libraryGroups.get(oldLibraryId);
            if (oldGroup != null) {
                oldGroup.remove(sessionId);
            }
        }
        // 加入新分组
        sessionLibraryMap.put(sessionId, libraryId);
        libraryGroups.computeIfAbsent(libraryId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    private void unsubscribeFromLibrary(String sessionId) {
        Long libraryId = sessionLibraryMap.remove(sessionId);
        if (libraryId != null) {
            Set<String> group = libraryGroups.get(libraryId);
            if (group != null) {
                group.remove(sessionId);
                if (group.isEmpty()) {
                    libraryGroups.remove(libraryId);
                }
            }
        }
    }

    private void removeSession(String sessionId) {
        sessions.remove(sessionId);
        Long libraryId = sessionLibraryMap.remove(sessionId);
        if (libraryId != null) {
            Set<String> group = libraryGroups.get(libraryId);
            if (group != null) {
                group.remove(sessionId);
                if (group.isEmpty()) {
                    libraryGroups.remove(libraryId);
                }
            }
        }
    }

    // ==================== 广播方法 ====================

    /**
     * 广播单个座位状态变更（推送完整 SeatStatusVO 数据）
     * 仅推送给订阅了该图书馆的客户端
     */
    public static void broadcastSeatChange(Long libraryId, SeatStatusVO seatVO) {
        try {
            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("type", "SEAT_CHANGE");
            payload.put("libraryId", libraryId);
            payload.put("seat", seatVO);
            String message = objectMapper.writeValueAsString(payload);
            sendToLibraryGroup(libraryId, message);
        } catch (Exception e) {
            log.error("广播座位变更消息失败", e);
        }
    }

    /**
     * 广播图书馆全量刷新信号（通知客户端需重新 HTTP 拉取）
     * 用于批量变更场景
     */
    public static void broadcastLibraryUpdate(Long libraryId) {
        try {
            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("type", "LIBRARY_UPDATE");
            payload.put("libraryId", libraryId);
            String message = objectMapper.writeValueAsString(payload);
            sendToLibraryGroup(libraryId, message);
            // 同时也发送给未订阅的客户端（兼容旧版本）
            sendToAllUnsubscribed(message);
        } catch (Exception e) {
            log.error("广播图书馆刷新消息失败", e);
        }
    }

    /**
     * 向后兼容：广播给所有客户端（旧通知方式）
     */
    @Deprecated
    public static void broadcastSeatStatusChange(Long libraryId) {
        broadcastLibraryUpdate(libraryId);
    }

    // ==================== 发送逻辑 ====================

    private static void sendToLibraryGroup(Long libraryId, String message) {
        Set<String> group = libraryGroups.get(libraryId);
        if (group == null || group.isEmpty()) {
            return;
        }
        for (String sessionId : group) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("发送 WebSocket 消息失败, session: {}", sessionId);
                }
            }
        }
    }

    private static void sendToAllUnsubscribed(String message) {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            if (!sessionLibraryMap.containsKey(entry.getKey())) {
                WebSocketSession session = entry.getValue();
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("发送 WebSocket 消息失败, session: {}", entry.getKey());
                    }
                }
            }
        }
    }
}
