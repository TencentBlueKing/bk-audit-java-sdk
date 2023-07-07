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
     * 审计子操作ID。比如删除作业模版操作，那么后台也会触发作业执行方案的删除（子操作）。如果需要记录子操作，需要设置 subActionIds
     */
    String[] subActionIds() default {};
}
