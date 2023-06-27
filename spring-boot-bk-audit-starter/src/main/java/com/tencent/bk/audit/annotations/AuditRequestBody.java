package com.tencent.bk.audit.annotations;

import java.lang.annotation.*;

/**
 * 用于标识审计数据-请求Body
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AuditRequestBody {
}
