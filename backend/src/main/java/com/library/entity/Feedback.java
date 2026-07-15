package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 反馈实体
 */
@Data
@TableName("feedback")
public class Feedback {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId; // 提交人ID
    
    private String userName; // 提交人姓名
    
    private String studentNo; // 学号
    
    private String type; // 反馈类型：建议/投诉/报修/其他
    
    private String title; // 标题
    
    private String content; // 内容
    
    private String imageUrl; // 图片URL
    
    private String status; // 状态：待处理/已回复
    
    private Long handlerId; // 处理人ID
    
    private String handlerName; // 处理人姓名
    
    private String reply; // 回复内容
    
    private String handleResult; // 处理结果：已采纳/已驳回/已转交
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 提交时间
    
    private LocalDateTime handleTime; // 处理时间
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
