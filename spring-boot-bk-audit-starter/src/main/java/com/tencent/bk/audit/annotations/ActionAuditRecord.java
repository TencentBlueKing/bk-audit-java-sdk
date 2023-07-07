package com.tencent.bk.audit.annotations;


import com.tencent.bk.audit.AuditEventBuilder;
import com.tencent.bk.audit.DefaultAuditEventBuilder;

import java.lang.annotation.*;

/**
 * 用于标识操作审计记录
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ActionAuditRecord {

    /**
     * 操作ID
     */
    String actionId() default "";

    /**
     * 事件描述。支持引用 attributes 中定义的属性
     */
    String content() default "";

    /**
     * 资源实例定义
     */
    AuditInstanceRecord instance() default @AuditInstanceRecord;

    /**
     * 事件其它属性
     */
    AuditAttribute[] attributes() default {};

    /**
     * 自定义审计事件Builder
     */
    Class<? extends AuditEventBuilder> builder() default DefaultAuditEventBuilder.class;
}
