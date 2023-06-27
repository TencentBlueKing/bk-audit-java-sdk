package com.tencent.bk.audit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作错误
 */
@Data
@NoArgsConstructor
public class ErrorInfo {
    /**
     * 错误码
     */
    private Integer errorCode;
    /**
     * 错误描述
     */
    private String errorMessage;

    public ErrorInfo(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
