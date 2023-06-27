package com.tencent.bk.audit.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 操作人账号类型
 */
public enum UserIdentifyTypeEnum {
    /**
     * 个人账号
     */
    PERSONAL(0),
    /**
     * 平台账号
     */
    PLATFORM(1),
    /**
     * 未知
     */
    UNKNOWN(-1);

    private final int userIdentifyType;

    UserIdentifyTypeEnum(int userIdentifyType) {
        this.userIdentifyType = userIdentifyType;
    }

    public int getValue() {
        return userIdentifyType;
    }

    public static UserIdentifyTypeEnum valOf(String value) {
        if (StringUtils.isEmpty(value)) {
            return UNKNOWN;
        }

        try {
            return valOf(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }

    public static UserIdentifyTypeEnum valOf(int value) {
        for (UserIdentifyTypeEnum type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
