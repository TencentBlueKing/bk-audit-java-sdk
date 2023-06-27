package com.tencent.bk.audit.constants;

/**
 * 审计 - 常量定义
 */
public interface Constants {
    /**
     * 审计事件标识。用于审计平台识别审计日志
     */
    String AUDIT_EVENT_SIGNATURE = "bk_audit_event";

    /**
     * 审计日志输出 LOGGER 名称
     */
    String AUDIT_LOGGER_NAME = "bk_audit";

    /**
     * 操作结果 - 成功
     */
    int RESULT_CODE_SUCCESS = 0;

    /**
     * 操作结果 - 错误
     */
    int RESULT_CODE_ERROR = -1;

    /**
     * 操作成功 - 描述
     */
    String RESULT_SUCCESS_DESC = "Success";

    /**
     * 操作成功 - 描述
     */
    String RESULT_ERROR_DESC = "Error";

}
