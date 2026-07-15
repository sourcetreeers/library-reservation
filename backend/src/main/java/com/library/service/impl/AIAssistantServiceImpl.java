package com.library.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.*;
import com.library.service.*;
import com.library.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AI智能助手服务实现类
 */
@Service
public class AIAssistantServiceImpl implements AIAssistantService {
    
    @Value("${ai.api-key:}")
    private String apiKey;
    
    @Value("${ai.model:qwen-plus}")
    private String model;
    
    @Value("${ai.api-url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // 业务Service注入
    @Autowired
    private UserService userService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private SeatService seatService;
    
    @Autowired
    private LibraryService libraryService;
    
    @Autowired
    private ViolationRecordService violationRecordService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    // 对话历史缓存 (userId -> messageList)
    private final Map<Long, List<Map<String, String>>> conversationHistory = new ConcurrentHashMap<>();
    
    // 待确认的操作缓存 (operationId -> operationDetails)
    private final Map<String, Map<String, Object>> pendingOperations = new ConcurrentHashMap<>();
    
    // 系统提示词 - 定义AI助手的行为、知识库和可用的功能
    private static final String SYSTEM_PROMPT = 
        "你是「图书馆座位预订系统」的智能知识助手，同时具备操作执行能力。\n\n" +
        
        "=== 📚 系统知识库 ===\n\n" +
        
        "## 一、预约规则\n" +
        "1. 每位学生每天最多预约 **2次**，每次最长 **4小时**\n" +
        "2. 预约时间范围为 **8:00-22:00**，需提前至少 **30分钟** 预约\n" +
        "3. 签到时限：预约开始后 **15分钟内** 未签到视为违约\n" +
        "4. 取消预约需在开始前 **10分钟** 以上操作\n" +
        "5. 逾期未签到或未取消将记入违规记录\n" +
        "6. 预约成功后会收到系统通知，包含座位号和预约时间\n\n" +
        
        "## 二、违规处理标准\n" +
        "- **轻度违规**（迟到/早退）：扣 2 积分，警告\n" +
        "- **中度违规**（占座不用/超时占用）：扣 5 积分，禁用预约功能 3 天\n" +
        "- **重度违规**（多次违规/恶意操作）：扣 10 积分，禁用预约功能 7-15 天\n" +
        "- 累计 **3次** 重度违规将永久禁用账号预约权限\n" +
        "- 违规记录可在个人中心查看，对处理结果有异议可申诉\n\n" +
        
        "## 三、座位类型说明\n" +
        "- **普通区**：开放式座位，无需特殊权限，适合一般学习\n" +
        "- **安静区**：需要保持安静，适合深度自习和阅读\n" +
        "- **研讨室**：可供 4-6 人小组讨论，需提前申请，配备白板\n" +
        "- **电子阅览区**：配备台式电脑、电源插座和网络接口\n" +
        "- **靠窗区**：自然光线充足，视野开阔（如适用）\n\n" +
        
        "## 四、用户角色与权限\n" +
        "- **学生 (student)**：可预约座位、查看/取消个人预约、查看个人违规记录、签到签退\n" +
        "- **图书馆管理员 (librarian)**：管理所有预约记录、管理座位状态、处理违规记录、发布公告、管理所属图书馆\n" +
        "- **系统管理员 (admin)**：全部权限 + 用户管理（启用/禁用/修改角色）+ 图书馆增删改 + 系统配置\n\n" +
        
        "## 五、开放时间\n" +
        "- 周一至周五：**8:00 - 22:00**\n" +
        "- 周末及节假日：**9:00 - 21:00**\n" +
        "- 寒暑假期间开放时间另行通知，请关注系统公告\n" +
        "- 节假日（国庆、春节等）可能调整，以最新公告为准\n\n" +
        
        "## 六、预约流程\n" +
        "1. 登录系统 → 选择日期和时间段 → 选择图书馆 → 选择座位类型 → 选座 → 确认预约\n" +
        "2. 预约成功后需在规定时间内 **签到**（到座后扫码或手动签到）\n" +
        "3. 使用完毕后需 **签退** 释放座位\n" +
        "4. 如需提前离开可在线取消或签退，避免被记为违规\n\n" +
        
        "## 七、常见问题解答 (FAQ)\n" +
        "**Q: 忘记签到怎么办？**\n" +
        "A: 联系图书馆管理员说明情况，管理员可酌情免除本次违约记录。建议设置签到提醒。\n\n" +
        "**Q: 座位故障如何报修？**\n" +
        "A: 通过系统的反馈功能提交报修信息，或在座位详情页点击「报修」按钮。也可直接联系当值管理员。\n\n" +
        "**Q: 如何申诉违规记录？**\n" +
        "A: 在个人中心 → 违规记录详情中点击「申诉」按钮，填写申诉理由和相关证明，等待管理员审核（通常1-3个工作日）。\n\n" +
        "**Q: 可以帮别人预约吗？**\n\n" +
        "A: 不可以。每个账号只能为自己预约座位，禁止转让或代订。发现此类行为将视为违规处理。\n\n" +
        "**Q: 预约后临时有事去不了怎么办？**\n" +
        "A: 请在预约开始前10分钟以上登录系统取消预约，这样不会产生违规记录。临近开始时间无法取消时可联系管理员。\n\n" +
        "**Q: 为什么显示没有可用座位？**\n" +
        "A: 可能原因：该时段已被约满、所选图书馆未开放该时段、或座位正在维护中。建议更换时间段或其他图书馆。\n\n" +
        "**Q: 积分有什么用？**\n" +
        "A: 积分影响用户信用等级。积分过低可能限制预约权限。可通过正常使用系统、参与活动等方式恢复积分。\n\n" +

        "=== 🛠️ 可执行操作（Function Calling） ===\n\n" +
        "当用户要求执行以下操作时，调用对应函数：\n\n" +
        "### 用户管理\n" +
        "- query_users：查询用户列表（支持按用户名、真实姓名、类型筛选）\n" +
        "- toggle_user_status：启用/禁用用户账号\n" +
        "- change_user_type：修改用户角色类型\n\n" +
        "### 座位预约管理\n" +
        "- query_reservations：查询预约记录（支持按状态、用户名、图书馆筛选）\n" +
        "- cancel_reservation：取消预约\n\n" +
        "### 座位管理\n" +
        "- query_seats：查询座位列表（支持按图书馆、座位号、类型筛选）\n" +
        "- update_seat：更新座位信息\n\n" +
        "### 违规记录管理\n" +
        "- query_violations：查询违规记录列表\n" +
        "- handle_violation：处理违规记录\n\n" +
        "### 公告管理\n" +
        "- create_notice：创建新公告\n" +
        "- update_notice：更新公告内容\n" +
        "- delete_notice：删除公告\n\n" +
        "### 图书馆管理\n" +
        "- query_libraries：查询图书馆列表\n" +
        "- create_library：创建新图书馆\n" +
        "- update_library：更新图书馆信息\n" +
        "- delete_library：删除图书馆\n\n" +
        
        "## 工作原则（重要！）\n" +
        "1. **优先判断意图**：分析用户问题是「知识咨询」还是「操作请求」\n" +
        "2. **知识咨询类问题**（关于规则、政策、流程、FAQ等）：直接用上述知识库准确回答，不要编造规则，引用具体条款\n" +
        "3. **操作请求类问题**（查询/创建/修改/删除数据）：识别意图后调用对应的函数执行\n" +
        "4. 对于删除、禁用等不可逆操作，必须先询问用户确认\n" +
        "5. 如果信息不足（如缺少ID或具体参数），主动询问用户\n" +
        "6. 回复要简洁明了，突出关键信息，使用中文回复\n" +
        "7. 如果知识库中没有答案，诚实告知用户并建议联系管理员";
    
    @Override
    public Map<String, Object> processMessage(String message, User user) {
        List<Map<String, String>> history = conversationHistory.computeIfAbsent(
            user.getId(), k -> new ArrayList<>()
        );
        
        List<Map<String, Object>> functions = buildFunctionSchemas();
        Map<String, Object> llmResponse = callLLMApi(message, history, functions);
        
        return parseResponse(llmResponse, user, history);
    }
    
    @Override
    public Map<String, Object> executeConfirmedOperation(String operationId, User user) {
        Map<String, Object> operation = pendingOperations.get(operationId);
        if (operation == null) {
            return createErrorResponse("操作已过期或不存在");
        }
        
        try {
            String functionName = (String) operation.get("functionName");
            JSONObject arguments = JSON.parseObject(operation.get("arguments").toString());
            
            Map<String, Object> result = executeFunction(functionName, arguments, user);
            
            pendingOperations.remove(operationId);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("type", "operation_result");
            resultMap.put("data", result);
            resultMap.put("reply", formatOperationResult(functionName, result));
            resultMap.put("functionCalled", functionName);
            return resultMap;
        } catch (Exception e) {
            return createErrorResponse("操作执行失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, Object>> getRegisteredFunctions() {
        return buildFunctionSchemas();
    }
    
    @Override
    public void clearConversationHistory(Long userId) {
        conversationHistory.remove(userId);
    }

    // ==================== Java 8 兼容性工具方法 ====================
    
    @SafeVarargs
    private static <K, V> Map<K, V> mapOf(Object... keyValuePairs) {
        Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put((K) keyValuePairs[i], (V) keyValuePairs[i + 1]);
        }
        return Collections.unmodifiableMap(map);
    }
    
    @SafeVarargs
    private static <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return Collections.unmodifiableList(list);
    }

    // ==================== LLM API 调用 ====================
    
    private Map<String, Object> callLLMApi(String userMessage, 
                                           List<Map<String, String>> history,
                                           List<Map<String, Object>> functions) {
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(mapOf("role", "system", "content", SYSTEM_PROMPT));
            
            int startIdx = Math.max(0, history.size() - 40);
            for (int i = startIdx; i < history.size(); i++) {
                messages.add(mapOf(
                    "role", history.get(i).get("role"),
                    "content", history.get(i).get("content")
                ));
            }
            
            messages.add(mapOf("role", "user", "content", userMessage));
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("functions", functions);
            if (functions != null && !functions.isEmpty()) {
                requestBody.put("function_call", "auto");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.POST, entity, String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return JSON.parseObject(response.getBody());
            } else {
                throw new RuntimeException("LLM API调用失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("LLM服务异常: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseResponse(Map<String, Object> llmResponse, 
                                              User user,
                                              List<Map<String, String>> history) {
        try {
            Object choicesObj = llmResponse.get("choices");
            if (!(choicesObj instanceof List)) {
                return createTextResponse("抱歉，我暂时无法处理您的请求。请稍后重试。");
            }
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) choicesObj;
            if (choices.isEmpty()) {
                return createTextResponse("抱歉，我没有生成有效的响应。");
            }
            
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            
            history.add(mapOf("role", "user", "content", 
                ((Map<String, Object>) choices.get(choices.size()-1).get("message")).getOrDefault("content", "").toString()));
            
            if (message.containsKey("function_call")) {
                Map<String, Object> functionCall = (Map<String, Object>) message.get("function_call");
                String functionName = (String) functionCall.get("name");
                String argumentsStr = (String) functionCall.get("arguments");
                JSONObject arguments = JSON.parseObject(argumentsStr);
                
                if (isSensitiveOperation(functionName)) {
                    String operationId = UUID.randomUUID().toString();
                    pendingOperations.put(operationId, mapOf(
                        "functionName", functionName,
                        "arguments", arguments,
                        "userId", user.getId(),
                        "timestamp", System.currentTimeMillis()
                    ));
                    
                    history.add(mapOf(
                        "role", "assistant",
                        "content", "[需要确认] 函数调用: " + functionName
                    ));
                    
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("type", "confirmation_required");
                    resultMap.put("reply", generateConfirmationPrompt(functionName, arguments));
                    resultMap.put("operationId", operationId);
                    resultMap.put("functionCalled", functionName);
                    resultMap.put("data", mapOf("function", functionName, "arguments", arguments));
                    return resultMap;
                } else {
                    history.add(mapOf(
                        "role", "assistant",
                        "content", "[调用函数] " + functionName
                    ));
                    
                    Map<String, Object> result = executeFunction(functionName, arguments, user);
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("type", "operation_result");
                    resultMap.put("data", result);
                    resultMap.put("reply", formatOperationResult(functionName, result));
                    resultMap.put("functionCalled", functionName);
                    return resultMap;
                }
            } else {
                String reply = (String) message.getOrDefault("content", "抱歉，我没有理解您的意思。请换个说法试试？");
                history.add(mapOf("role", "assistant", "content", reply));
                return createTextResponse(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("响应解析错误: " + e.getMessage());
        }
    }
    
    // ==================== 函数分发 ====================
    
    private Map<String, Object> executeFunction(String functionName, JSONObject arguments, User user) {
        switch (functionName) {
            case "query_users": return executeQueryUsers(arguments);
            case "get_user_detail": return executeGetUserDetail(arguments);
            case "toggle_user_status": return executeToggleUserStatus(arguments);
            case "change_user_type": return executeChangeUserType(arguments);
            case "query_reservations": return executeQueryReservations(arguments);
            case "cancel_reservation": return executeCancelReservation(arguments);
            case "get_reservation_detail": return executeGetReservationDetail(arguments);
            case "query_seats": return executeQuerySeats(arguments);
            case "update_seat": return executeUpdateSeat(arguments);
            case "query_violations": return executeQueryViolations(arguments);
            case "handle_violation": return executeHandleViolation(arguments);
            case "create_notice": return executeCreateNotice(arguments);
            case "update_notice": return executeUpdateNotice(arguments);
            case "delete_notice": return executeDeleteNotice(arguments);
            case "query_libraries": return executeQueryLibraries(arguments);
            case "create_library": return executeCreateLibrary(arguments);
            case "update_library": return executeUpdateLibrary(arguments);
            case "delete_library": return executeDeleteLibrary(arguments);
            default: throw new RuntimeException("未知函数: " + functionName);
        }
    }
    
    // ==================== Function Schema 定义 ====================
    
    private List<Map<String, Object>> buildFunctionSchemas() {
        List<Map<String, Object>> functions = new ArrayList<>();
        
        // 用户管理
        functions.add(createFunctionSchema(
            "query_users", "查询用户列表，支持多条件筛选",
            mapOf("type", "object",
                "properties", mapOf(
                    "username", mapOf("type", "string", "description", "用户名（模糊搜索）"),
                    "realName", mapOf("type", "string", "description", "真实姓名"),
                    "userType", mapOf("type", "string", "enum", Arrays.asList("student", "librarian", "admin"), "description", "用户类型"),
                    "status", mapOf("type", "string", "enum", Arrays.asList("0", "1"), "description", "状态：0=禁用，1=启用"),
                    "current", mapOf("type", "integer", "description", "页码，默认1"),
                    "size", mapOf("type", "integer", "description", "每页条数，默认10")
                ),
                "required", listOf()
            )
        ));
        
        functions.add(createFunctionSchema(
            "toggle_user_status", "启用或禁用用户账号（敏感操作，需确认）",
            mapOf("type", "object",
                "properties", mapOf(
                    "userId", mapOf("type", "integer", "description", "用户ID"),
                    "action", mapOf("type", "string", "enum", Arrays.asList("enable", "disable"), "description", "操作：启用或禁用")
                ),
                "required", Arrays.asList("userId", "action")
            )
        ));
        
        functions.add(createFunctionSchema(
            "change_user_type", "修改用户的角色类型（敏感操作，需确认）",
            mapOf("type", "object",
                "properties", mapOf(
                    "userId", mapOf("type", "integer", "description", "用户ID"),
                    "newType", mapOf("type", "string", "enum", Arrays.asList("student", "librarian", "admin"), "description", "新的用户类型")
                ),
                "required", Arrays.asList("userId", "newType")
            )
        ));
        
        // 预约管理
        functions.add(createFunctionSchema(
            "query_reservations", "查询预约记录列表，支持多条件筛选",
            mapOf("type", "object",
                "properties", mapOf(
                    "status", mapOf("type", "string", "enum", Arrays.asList("RESERVED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED", "EXPIRED"), "description", "预约状态"),
                    "userName", mapOf("type", "string", "description", "预约人用户名"),
                    "libraryId", mapOf("type", "integer", "description", "图书馆ID"),
                    "current", mapOf("type", "integer", "description", "页码"),
                    "size", mapOf("type", "integer", "description", "每页条数")
                ),
                "required", listOf()
            )
        ));
        
        functions.add(createFunctionSchema(
            "cancel_reservation", "取消预约（敏感操作，需确认）",
            mapOf("type", "object",
                "properties", mapOf(
                    "reservationId", mapOf("type", "integer", "description", "预约记录ID")
                ),
                "required", Arrays.asList("reservationId")
            )
        ));
        
        // 座位管理
        functions.add(createFunctionSchema(
            "query_seats", "查询座位列表",
            mapOf("type", "object",
                "properties", mapOf(
                    "libraryId", mapOf("type", "integer", "description", "图书馆ID"),
                    "seatNumber", mapOf("type", "string", "description", "座位号"),
                    "status", mapOf("type", "string", "description", "座位状态"),
                    "current", mapOf("type", "integer", "description", "页码"),
                    "size", mapOf("type", "integer", "description", "每页条数")
                ),
                "required", listOf()
            )
        ));
        
        // 违规记录
        functions.add(createFunctionSchema(
            "query_violations", "查询违规记录列表",
            mapOf("type", "object",
                "properties", mapOf(
                    "userName", mapOf("type", "string", "description", "违规用户名"),
                    "status", mapOf("type", "string", "enum", Arrays.asList("PENDING", "PROCESSED", "APPEALED"), "description", "处理状态"),
                    "current", mapOf("type", "integer", "description", "页码"),
                    "size", mapOf("type", "integer", "description", "每页条数")
                ),
                "required", listOf()
            )
        ));
        
        // 公告管理
        functions.add(createFunctionSchema(
            "create_notice", "创建新公告",
            mapOf("type", "object",
                "properties", mapOf(
                    "title", mapOf("type", "string", "description", "公告标题"),
                    "content", mapOf("type", "string", "description", "公告正文内容"),
                    "type", mapOf("type", "string", "enum", Arrays.asList("SYSTEM", "NOTICE", "WARNING"), "description", "公告类型")
                ),
                "required", Arrays.asList("title", "content")
            )
        ));
        
        functions.add(createFunctionSchema(
            "delete_notice", "删除公告（敏感操作，需确认）",
            mapOf("type", "object",
                "properties", mapOf(
                    "noticeId", mapOf("type", "integer", "description", "公告ID")
                ),
                "required", Arrays.asList("noticeId")
            )
        ));
        
        // 图书馆管理
        functions.add(createFunctionSchema(
            "query_libraries", "查询图书馆列表",
            mapOf("type", "object",
                "properties", new HashMap<>(),
                "required", listOf()
            )
        ));
        
        functions.add(createFunctionSchema(
            "create_library", "创建新图书馆",
            mapOf("type", "object",
                "properties", mapOf(
                    "name", mapOf("type", "string", "description", "图书室名称"),
                    "address", mapOf("type", "string", "description", "地址"),
                    "description", mapOf("type", "string", "description", "描述")
                ),
                "required", Arrays.asList("name")
            )
        ));
        
        functions.add(createFunctionSchema(
            "delete_library", "删除图书馆（敏感操作，需确认）",
            mapOf("type", "object",
                "properties", mapOf(
                    "libraryId", mapOf("type", "integer", "description", "图书馆ID")
                ),
                "required", Arrays.asList("libraryId")
            )
        ));
        
        return functions;
    }
    
    private Map<String, Object> createFunctionSchema(String name, String description, Map<String, Object> parameters) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", name);
        schema.put("description", description);
        schema.put("parameters", parameters);
        return schema;
    }
    
    // ==================== 敏感操作判断 ====================
    
    private boolean isSensitiveOperation(String functionName) {
        Set<String> sensitiveOps = new HashSet<>(Arrays.asList(
            "toggle_user_status", "change_user_type", "cancel_reservation",
            "delete_notice", "delete_library"
        ));
        return sensitiveOps.contains(functionName);
    }
    
    private String generateConfirmationPrompt(String functionName, JSONObject arguments) {
        switch (functionName) {
            case "toggle_user_status":
                return String.format("即将%s用户 [ID: %s]，此操作会影响该用户的登录权限。\n\n请确认是否继续？如确认请点击「确认执行」按钮。",
                    "disable".equals(arguments.getString("action")) ? "禁用" : "启用",
                    arguments.getInteger("userId"));
            case "change_user_type":
                return String.format("即将修改用户 [ID: %s] 的角色为 [%s]，这可能影响其权限。\n\n请确认是否继续？",
                    arguments.getInteger("userId"), arguments.getString("newType"));
            case "cancel_reservation":
                return String.format("即将取消预约记录 [ID: %s]，此操作无法撤销。\n\n请确认是否继续？",
                    arguments.getInteger("reservationId"));
            case "delete_notice":
                return String.format("即将删除公告 [ID: %s]，此操作无法撤销。\n\n请确认是否继续？",
                    arguments.getInteger("noticeId"));
            case "delete_library":
                return String.format("即将删除图书馆 [ID: %s]，此操作无法撤销。\n\n请确认是否继续？",
                    arguments.getInteger("libraryId"));
            default:
                return String.format("即将执行操作: %s\n参数: %s\n\n请确认是否继续？", 
                    functionName, arguments.toJSONString());
        }
    }

    // ==================== 真实业务逻辑实现 ====================

    /**
     * 查询用户列表
     */
    private Map<String, Object> executeQueryUsers(JSONObject args) {
        try {
            int current = args.containsKey("current") ? args.getIntValue("current") : 1;
            int size = args.containsKey("size") ? args.getIntValue("size") : 10;
            String username = args.getString("username");
            String realName = args.getString("realName");
            String userType = args.getString("userType");
            String status = args.getString("status");
            
            PageResult<User> pageResult = userService.getUserPageQuery(current, size, username, realName, userType, status);
            
            List<Map<String, Object>> records = pageResult.getRecords().stream().map(u -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", u.getId());
                item.put("username", u.getUsername());
                item.put("realName", u.getRealName());
                item.put("phone", u.getPhone());
                item.put("userType", u.getUserType());
                item.put("status", u.getStatus());
                return item;
            }).collect(Collectors.toList());
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", records);
            data.put("total", pageResult.getTotal());
            data.put("current", current);
            data.put("size", size);
            
            return mapOf("success", true, "message", "查询成功", "data", data);
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     */
    private Map<String, Object> executeGetUserDetail(JSONObject args) {
        try {
            Long userId = args.getLong("userId");
            User user = userService.getById(userId);
            if (user == null) {
                return mapOf("success", false, "message", "用户不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("phone", user.getPhone());
            data.put("userType", user.getUserType());
            data.put("status", user.getStatus());
            return mapOf("success", true, "message", "查询成功", "data", data);
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 切换用户状态
     */
    private Map<String, Object> executeToggleUserStatus(JSONObject args) {
        try {
            Long userId = args.getLong("userId");
            String action = args.getString("action");
            userService.toggleUserStatus(userId);
            boolean enabled = "enable".equals(action);
            return mapOf("success", true,
                "message", String.format("用户[ID:%d] 已成功%s", userId, enabled ? "启用" : "禁用"),
                "userId", userId, "newStatus", enabled ? "1" : "0");
        } catch (Exception e) {
            return mapOf("success", false, "message", "操作失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户类型
     */
    private Map<String, Object> executeChangeUserType(JSONObject args) {
        try {
            Long userId = args.getLong("userId");
            String newType = args.getString("newType");
            userService.changeUserType(userId, newType);
            String typeName = "student".equals(newType) ? "学生" : 
                              ("librarian".equals(newType) ? "图书管理员" : "系统管理员");
            return mapOf("success", true,
                "message", String.format("用户[ID:%d] 角色已修改为: %s", userId, typeName),
                "userId", userId, "newType", newType);
        } catch (Exception e) {
            return mapOf("success", false, "message", "操作失败: " + e.getMessage());
        }
    }

    /**
     * 查询预约记录
     */
    private Map<String, Object> executeQueryReservations(JSONObject args) {
        try {
            LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
            
            if (args.containsKey("status") && args.getString("status") != null) {
                wrapper.eq(Reservation::getStatus, args.getString("status"));
            }
            if (args.containsKey("libraryId") && args.getLong("libraryId") != null) {
                wrapper.eq(Reservation::getLibraryId, args.getLong("libraryId"));
            }
            wrapper.orderByDesc(Reservation::getCreateTime);
            
            List<Reservation> list = reservationService.list(wrapper);
            
            List<Map<String, Object>> records = list.stream().limit(50).map(r -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", r.getId());
                item.put("orderNo", r.getOrderNo());
                item.put("userId", r.getUserId());
                item.put("libraryId", r.getLibraryId());
                item.put("seatId", r.getSeatId());
                item.put("startTime", r.getStartTime() != null ? r.getStartTime().toString() : "");
                item.put("endTime", r.getEndTime() != null ? r.getEndTime().toString() : "");
                item.put("status", r.getStatus());
                return item;
            }).collect(Collectors.toList());
            
            return mapOf("success", true, "message", "查询成功",
                "data", mapOf("records", records, "total", list.size()));
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    private Map<String, Object> executeCancelReservation(JSONObject args) {
        try {
            Long reservationId = args.getLong("reservationId");
            Reservation reservation = reservationService.getById(reservationId);
            if (reservation == null) {
                return mapOf("success", false, "message", "预约记录不存在");
            }
            reservation.setStatus("已取消");
            reservation.setUpdateTime(java.time.LocalDateTime.now());
            reservationService.updateById(reservation);
            return mapOf("success", true,
                "message", String.format("预约记录[ID:%d] 已取消", reservationId),
                "reservationId", reservationId);
        } catch (Exception e) {
            return mapOf("success", false, "message", "操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取预约详情
     */
    private Map<String, Object> executeGetReservationDetail(JSONObject args) {
        try {
            Long reservationId = args.getLong("reservationId");
            Reservation r = reservationService.getById(reservationId);
            if (r == null) {
                return mapOf("success", false, "message", "预约记录不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", r.getId());
            data.put("orderNo", r.getOrderNo());
            data.put("userId", r.getUserId());
            data.put("libraryId", r.getLibraryId());
            data.put("seatId", r.getSeatId());
            data.put("startTime", r.getStartTime() != null ? r.getStartTime().toString() : "");
            data.put("endTime", r.getEndTime() != null ? r.getEndTime().toString() : "");
            data.put("status", r.getStatus());
            return mapOf("success", true, "message", "查询成功", "data", data);
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询座位列表
     */
    private Map<String, Object> executeQuerySeats(JSONObject args) {
        try {
            LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>();
            
            if (args.containsKey("libraryId") && args.getLong("libraryId") != null) {
                wrapper.eq(Seat::getLibraryId, args.getLong("libraryId"));
            }
            if (args.containsKey("seatNumber") && args.getString("seatNumber") != null) {
                wrapper.like(Seat::getSeatNumber, args.getString("seatNumber"));
            }
            
            List<Seat> seats = seatService.list(wrapper);
            List<Map<String, Object>> records = seats.stream().map(s -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", s.getId());
                item.put("seatNumber", s.getSeatNumber());
                item.put("libraryId", s.getLibraryId());
                item.put("seatType", s.getSeatType());
                item.put("status", s.getStatus());
                return item;
            }).collect(Collectors.toList());
            
            return mapOf("success", true, "message", "查询成功",
                "data", mapOf("records", records, "total", records.size()));
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 更新座位信息
     */
    private Map<String, Object> executeUpdateSeat(JSONObject args) {
        try {
            Long seatId = args.getLong("seatId");
            Seat seat = seatService.getById(seatId);
            if (seat == null) {
                return mapOf("success", false, "message", "座位不存在");
            }
            if (args.containsKey("seatNumber")) seat.setSeatNumber(args.getString("seatNumber"));
            if (args.containsKey("seatType")) seat.setSeatType(args.getString("seatType"));
            if (args.containsKey("status")) seat.setStatus(args.getString("status"));
            seatService.updateById(seat);
            return mapOf("success", true, "message", "座位信息更新成功");
        } catch (Exception e) {
            return mapOf("success", false, "message", "更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询违规记录
     */
    private Map<String, Object> executeQueryViolations(JSONObject args) {
        try {
            LambdaQueryWrapper<ViolationRecord> wrapper = new LambdaQueryWrapper<>();
            
            if (args.containsKey("status") && args.getString("status") != null) {
                wrapper.eq(ViolationRecord::getStatus, args.getString("status"));
            }
            wrapper.orderByDesc(ViolationRecord::getViolationTime);
            
            List<ViolationRecord> list = violationRecordService.list(wrapper);
            List<Map<String, Object>> records = list.stream().limit(50).map(v -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", v.getId());
                item.put("userName", v.getUserName());
                item.put("studentNo", v.getStudentNo());
                item.put("violationType", v.getViolationType());
                item.put("pointsDeducted", v.getPointsDeducted());
                item.put("banDays", v.getBanDays());
                item.put("status", v.getStatus());
                item.put("violationTime", v.getViolationTime() != null ? v.getViolationTime().toString() : "");
                return item;
            }).collect(Collectors.toList());
            
            return mapOf("success", true, "message", "查询成功",
                "data", mapOf("records", records, "total", list.size()));
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 处理违规记录
     */
    private Map<String, Object> executeHandleViolation(JSONObject args) {
        try {
            Long violationId = args.getLong("violationId");
            ViolationRecord record = violationRecordService.getById(violationId);
            if (record == null) {
                return mapOf("success", false, "message", "违规记录不存在");
            }
            record.setStatus("PROCESSED");
            record.setHandledByName("AI助手");
            record.setHandleTime(java.time.LocalDateTime.now());
            violationRecordService.updateById(record);
            return mapOf("success", true, "message", "违规记录已处理");
        } catch (Exception e) {
            return mapOf("success", false, "message", "操作失败: " + e.getMessage());
        }
    }

    /**
     * 创建公告
     */
    private Map<String, Object> executeCreateNotice(JSONObject args) {
        try {
            Announcement announcement = new Announcement();
            announcement.setTitle(args.getString("title"));
            announcement.setContent(args.getString("content"));
            announcement.setType(args.containsKey("type") ? args.getString("type") : "NOTICE");
            announcement.setStatus("1"); // 已发布
            announcement.setCreateTime(java.time.LocalDateTime.now());
            announcementService.save(announcement);
            return mapOf("success", true,
                "message", String.format("公告「%s」创建成功！", args.getString("title")),
                "noticeId", announcement.getId());
        } catch (Exception e) {
            return mapOf("success", false, "message", "创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新公告
     */
    private Map<String, Object> executeUpdateNotice(JSONObject args) {
        try {
            Long noticeId = args.getLong("noticeId");
            Announcement notice = announcementService.getById(noticeId);
            if (notice == null) {
                return mapOf("success", false, "message", "公告不存在");
            }
            if (args.containsKey("title")) notice.setTitle(args.getString("title"));
            if (args.containsKey("content")) notice.setContent(args.getString("content"));
            if (args.containsKey("type")) notice.setType(args.getString("type"));
            notice.setUpdateTime(java.time.LocalDateTime.now());
            announcementService.updateById(notice);
            return mapOf("success", true, "message", "公告更新成功");
        } catch (Exception e) {
            return mapOf("success", false, "message", "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除公告
     */
    private Map<String, Object> executeDeleteNotice(JSONObject args) {
        try {
            Long noticeId = args.getLong("noticeId");
            announcementService.removeById(noticeId);
            return mapOf("success", true,
                "message", String.format("公告[ID:%d] 已删除", noticeId),
                "noticeId", noticeId);
        } catch (Exception e) {
            return mapOf("success", false, "message", "删除失败: " + e.getMessage());
        }
    }

    /**
     * 查询图书馆列表
     */
    private Map<String, Object> executeQueryLibraries(JSONObject args) {
        try {
            List<Library> libraries = libraryService.list();
            List<Map<String, Object>> records = libraries.stream().map(lib -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", lib.getId());
                item.put("name", lib.getName());
                item.put("address", lib.getAddress());
                item.put("description", lib.getDescription());
                item.put("status", lib.getStatus());
                return item;
            }).collect(Collectors.toList());
            
            return mapOf("success", true, "message", "查询成功",
                "data", mapOf("records", records));
        } catch (Exception e) {
            return mapOf("success", false, "message", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 创建图书馆
     */
    private Map<String, Object> executeCreateLibrary(JSONObject args) {
        try {
            Library library = new Library();
            library.setName(args.getString("name"));
            library.setAddress(args.getString("address"));
            library.setDescription(args.getString("description"));
            library.setStatus("正常");
            library.setCreateTime(java.time.LocalDateTime.now());
            libraryService.save(library);
            return mapOf("success", true,
                "message", String.format("图书室「%s」创建成功！", args.getString("name")),
                "libraryId", library.getId(), "libraryName", library.getName());
        } catch (Exception e) {
            return mapOf("success", false, "message", "创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新图书馆
     */
    private Map<String, Object> executeUpdateLibrary(JSONObject args) {
        try {
            Long libraryId = args.getLong("libraryId");
            Library library = libraryService.getById(libraryId);
            if (library == null) {
                return mapOf("success", false, "message", "图书馆不存在");
            }
            if (args.containsKey("name")) library.setName(args.getString("name"));
            if (args.containsKey("address")) library.setAddress(args.getString("address"));
            if (args.containsKey("description")) library.setDescription(args.getString("description"));
            library.setUpdateTime(java.time.LocalDateTime.now());
            libraryService.updateById(library);
            return mapOf("success", true, "message", "图书馆信息更新成功");
        } catch (Exception e) {
            return mapOf("success", false, "message", "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除图书馆
     */
    private Map<String, Object> executeDeleteLibrary(JSONObject args) {
        try {
            Long libraryId = args.getLong("libraryId");
            libraryService.removeById(libraryId);
            return mapOf("success", true,
                "message", String.format("图书馆[ID:%d] 已删除", libraryId),
                "libraryId", libraryId);
        } catch (Exception e) {
            return mapOf("success", false, "message", "删除失败: " + e.getMessage());
        }
    }

    // ==================== 响应格式化 ====================
    
    private Map<String, Object> createTextResponse(String text) {
        return mapOf("type", "text", "reply", text);
    }
    
    private Map<String, Object> createErrorResponse(String errorMsg) {
        return mapOf("type", "error", "reply", errorMsg, "error", errorMsg);
    }
    
    private String formatOperationResult(String functionName, Map<String, Object> result) {
        Boolean success = (Boolean) result.get("success");
        String message = (String) result.get("message");
        Object data = result.get("data");
        
        if (!Boolean.TRUE.equals(success)) {
            return "❌ 操作失败！\n\n" + message + "\n\n请检查后重试或联系技术支持。";
        }
        
        // 根据不同函数类型格式化输出
        StringBuilder reply = new StringBuilder();
        reply.append("✅ 操作完成！\n\n");
        reply.append(message);
        
        // 查询类操作 - 展示数据摘要
        if (functionName.startsWith("query_") && data instanceof Map) {
            Map<?, ?> dataMap = (Map<? ,?>) data;
            Object records = dataMap.get("records");
            Object total = dataMap.get("total");
            
            if (records instanceof List) {
                List<?> list = (List<?>) records;
                reply.append(String.format("\n\n📊 共找到 **%d** 条记录：", total != null ? total : list.size()));
                
                // 最多显示5条详细数据
                int showCount = Math.min(list.size(), 5);
                for (int i = 0; i < showCount; i++) {
                    Object item = list.get(i);
                    if (item instanceof Map) {
                        Map<?, ?> itemMap = (Map<? ,?>) item;
                        reply.append(String.format("\n%d. %s", i + 1, formatDataItem(functionName, itemMap)));
                    }
                }
                if (list.size() > 5) {
                    reply.append(String.format("\n... 还有 %d 条记录", list.size() - 5));
                }
            }
        }
        // 创建类操作 - 展示创建的记录ID
        else if (functionName.startsWith("create_")) {
            if (result.containsKey("libraryId")) {
                reply.append(String.format("\n\n📌 图书室ID: %d", result.get("libraryId")));
            }
            if (result.containsKey("noticeId")) {
                reply.append(String.format("\n📌 公告ID: %d", result.get("noticeId")));
            }
        }
        // 删除/取消类操作 - 展示影响的记录
        else if (functionName.startsWith("delete_") || functionName.equals("cancel_reservation")) {
            if (result.containsKey("libraryId")) {
                reply.append(String.format("\n🗑️ 图书室ID: %d", result.get("libraryId")));
            }
            if (result.containsKey("noticeId")) {
                reply.append(String.format("\n🗑️ 公告ID: %d", result.get("noticeId")));
            }
            if (result.containsKey("reservationId")) {
                reply.append(String.format("\n🗑️ 预约ID: %d", result.get("reservationId")));
            }
        }
        // 用户状态切换
        else if (functionName.equals("toggle_user_status")) {
            reply.append(String.format("\n👤 用户ID: %s → 状态: %s", 
                result.get("userId"), "1".equals(result.get("newStatus")) ? "✅已启用" : "❌已禁用"));
        }
        else if (functionName.equals("change_user_type")) {
            reply.append(String.format("\n👤 用户ID: %s → 新角色: %s",
                result.get("userId"), result.get("newType")));
        }
        
        reply.append("\n\n_（由 AI 助手自动执行）_");
        return reply.toString();
    }
    
    /**
     * 格式化单条数据为可读文本
     */
    private String formatDataItem(String functionName, Map<?, ?> item) {
        switch (functionName) {
            case "query_users":
                return String.format("[%s] %s (%s) - %s", 
                    item.get("id"), item.get("realName"), item.get("username"),
                    "1".equals(item.get("status")) ? "正常" : "禁用");
            case "query_reservations":
                return String.format("[#%s] 座位:%s | 时间:%s ~ %s | 状态:%s",
                    item.get("id"), item.get("seatId"), 
                    truncate(item.get("startTime").toString(), 16),
                    truncate(item.get("status").toString(), 10), item.get("status"));
            case "query_seats":
                return String.format("[#%s] %s (%s) - %s",
                    item.get("id"), item.get("seatNumber"), item.get("seatType"), item.get("status"));
            case "query_violations":
                return String.format("[#%s] %s (%s) - %s [%s]",
                    item.get("id"), item.get("userName"), item.get("studentNo"),
                    item.get("violationType"), item.get("status"));
            case "query_libraries":
                return String.format("[#%d] %s - %s %s",
                    item.get("id"), item.get("name"), 
                    item.get("address") != null ? item.get("address") : "",
                    item.get("status"));
            default:
                return item.toString();
        }
    }
    
    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen) + "..." : str;
    }
}
