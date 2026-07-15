package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 轮播图实体
 */
@Data
@TableName("banner")
public class Banner {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title; // 轮播图标题
    
    private String imageUrl; // 图片地址
    
    private String linkUrl; // 跳转链接
    
    private Integer sortOrder; // 排序
    
    private String status; // 状态：启用/禁用
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
