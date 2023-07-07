package com.tencent.bk.audit.constants;

/**
 * 审计事件 Exporter 类型
 */
public enum ExporterTypeEnum {

    /**
     * 审计日志文件
     */
    LOG_FILE(Constants.LOG_FILE),
    /**
     * 标准输出流
     */
    STDOUT(Constants.STDOUT);

    public static class Constants {
        public static final String LOG_FILE = "log_file";
        public static final String STDOUT = "stdout";
    }


    ExporterTypeEnum(String type) {
        this.type = type;
    }

    private final String type;

    public String getValue() {
        return type;
    }
}
