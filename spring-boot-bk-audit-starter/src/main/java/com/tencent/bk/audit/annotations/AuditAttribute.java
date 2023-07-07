package com.tencent.bk.audit.annotations;

import java.lang.annotation.*;

/**
 * 自定义审计上下文属性
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
