package com.tencent.bk.audit.annotations;

import java.lang.annotation.*;

/**
 * 审计事件 - 属性注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AuditAttribute {

    /**
     * 属性名
     */
    String name();

    /**
     * 属性值
     */
    String value();
}
