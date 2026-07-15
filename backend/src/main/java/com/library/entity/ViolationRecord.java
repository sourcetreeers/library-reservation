package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 违规记录实体
 */
@Data
@TableName("violation_record")
public class ViolationRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId; // 违规用户ID
    
    private String userName; // 用户姓名
    
    private String studentNo; // 学号
    
    private String violationType; // 违规类型：爽约/暂离超时/未签退离馆/恶意占座
    
    private Long reservationId; // 关联预约ID
    
    private String orderNo; // 预约流水号
    
    private Long ruleId; // 触发的规则ID
    
    private Integer pointsDeducted; // 实际扣分
    
    private Integer banDays; // 实际封禁天数
    
    private String status; // 状态：待处理/已处理/已豁免
    
    private String handleReply; // 处理备注
    
    private Long handledBy; // 处理人ID
    
    private String handledByName; // 处理人姓名
    
    private LocalDateTime violationTime; // 违规发生时间
    
    private LocalDateTime handleTime; // 处理时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
