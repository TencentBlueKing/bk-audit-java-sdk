package com.tencent.bk.audit;

import com.tencent.bk.audit.model.ErrorInfo;

/**
 * 审计操作中抛出的异常处理
 */
public interface AuditExceptionResolver {
    ErrorInfo resolveException(Throwable e);
}
