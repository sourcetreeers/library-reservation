package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String operateUser; // 操作人用户名
    
    private Long operateUserId; // 操作人ID
    
    private String operateType; // 操作类型：登录/新增/修改/删除/查询/导出/其他
    
    private String targetType; // 操作对象类型
    
    private Long targetId; // 操作对象ID
    
    private String detail; // 操作详情（JSON格式）
    
    private String ipAddress; // 操作IP地址
    
    private String userAgent; // 浏览器UA
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
