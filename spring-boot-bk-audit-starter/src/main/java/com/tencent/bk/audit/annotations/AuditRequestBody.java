package com.tencent.bk.audit.annotations;

import java.lang.annotation.*;

/**
 * 用于标识审计操作对应的 Http 请求 Body
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AuditRequestBody {
}
