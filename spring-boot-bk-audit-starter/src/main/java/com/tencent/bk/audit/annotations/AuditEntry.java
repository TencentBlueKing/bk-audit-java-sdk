package com.tencent.bk.audit.annotations;

import java.lang.annotation.*;

/**
 * 用于标识操作审计入口
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AuditEntry {
    /**
     * 操作ID
     */
    String actionId() default "";

    /**
     * 审计子操作ID
     */
    String[] subActionIds() default {};
}
