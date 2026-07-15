package com.library.aspect;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标记在Controller方法上，AOP切面会自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    /**
     * 操作类型：登录/新增/修改/删除/查询/导出/其他
     */
    String value() default "其他";
    
    /**
     * 操作对象/模块名称：如 图书室/座位/用户/公告/规则/违规/反馈
     */
    String module() default "";
}
