package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.model.ErrorInfo;

public class DefaultAuditExceptionResolver implements AuditExceptionResolver {
    @Override
    public ErrorInfo resolveException(Throwable e) {
        return new ErrorInfo(Constants.RESULT_CODE_ERROR, Constants.RESULT_ERROR_DESC);
    }
}
