package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.model.ErrorInfo;

/**
 * 默认的审计操作异常错误解析器
 */
public class DefaultAuditExceptionResolver implements AuditExceptionResolver {
    @Override
    public ErrorInfo resolveException(Throwable e) {
        return new ErrorInfo(Constants.RESULT_CODE_ERROR, Constants.RESULT_ERROR_DESC);
    }
}
