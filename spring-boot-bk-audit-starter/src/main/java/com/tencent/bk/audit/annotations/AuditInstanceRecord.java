package com.tencent.bk.audit.annotations;

import java.lang.annotation.*;

/**
 * 用于标识操作实例
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AuditInstanceRecord {
    /**
     * 操作实例资源类型ID
     */
    String resourceType() default "";

    /**
     * 操作实例ID - SpEL表达式
     */
    String instanceIds() default "";

    /**
     * 操作实例名称 - SpEL 表达式
     */
    String instanceNames() default "";

    /**
     * 原始实例 - SpEL 表达式
     */
    String originInstances() default "";

    /**
     * 当前实例 - SpEL 表达式
     */
    String instances() default "";
}
