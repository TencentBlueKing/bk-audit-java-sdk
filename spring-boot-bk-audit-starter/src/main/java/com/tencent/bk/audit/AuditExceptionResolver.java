package com.tencent.bk.audit;

import com.tencent.bk.audit.model.ErrorInfo;

public interface AuditExceptionResolver {
    ErrorInfo resolveException(Throwable e);
}
