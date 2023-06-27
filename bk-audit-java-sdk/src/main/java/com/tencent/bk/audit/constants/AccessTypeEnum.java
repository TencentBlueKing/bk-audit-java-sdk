package com.tencent.bk.audit.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 访问方式
 */
public enum AccessTypeEnum {
    /**
     * 网页
     */
    WEB(0),
    /**
     * API网关
     */
    API(1),
    /**
     * 控制台
     */
    CONSOLE(2),
    /**
     * 未知
     */
    OTHER(-1);

    private final int type;

    AccessTypeEnum(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

    public static AccessTypeEnum valOf(String value) {
        if (StringUtils.isEmpty(value)) {
            return OTHER;
        }

        try {
            return valOf(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return OTHER;
        }
    }

    public static AccessTypeEnum valOf(int value) {
        for (AccessTypeEnum type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return OTHER;
    }
}
