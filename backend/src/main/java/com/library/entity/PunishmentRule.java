package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 违规处罚规则实体
 */
@Data
@TableName("punishment_rule")
public class PunishmentRule {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String violationType; // 违规类型：爽约/暂离超时/未签退离馆/恶意占座
    
    private String applyTimes; // 适用次数：FIRST/SECOND/THIRD_PLUS
    
    private Integer pointsDeduct; // 扣分值
    
    private Integer banDays; // 封禁天数（0表示不封禁）
    
    private Boolean isActive; // 是否启用
    
    private String description; // 规则描述
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
