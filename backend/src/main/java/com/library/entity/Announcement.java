package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
@TableName("announcement")
public class Announcement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title; // 公告标题
    
    private String content; // 公告内容
    
    private String type; // 公告类型：普通公告/维修通知/紧急通知
    
    private Integer priority; // 优先级
    
    private String status; // 状态：发布中/已下架
    
    private LocalDateTime publishTime; // 发布时间
    
    private LocalDateTime expireTime; // 过期时间
    
    private String createBy; // 创建人
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
