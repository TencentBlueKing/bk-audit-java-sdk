package com.tencent.bk.audit.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变量解析
 */
@Slf4j
public class VariableResolver {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(\\{\\{(.*?)}})");

    /**
     * 原始字符串的变量进行替换
     *
     * @param rawStr    原始字符串
     * @param variables 变量名、变量值
     * @return 替换后的字符串
     */
    public static String resolveVariables(String rawStr, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return rawStr;
        }

        String resultStr = rawStr;

        Matcher matcher = VARIABLE_PATTERN.matcher(rawStr);
        while (matcher.find()) {
            String placeHolder = matcher.group(1);
            String variableName = matcher.group(2);
            if (variables.containsKey(variableName)) {
                String value = variables.get(variableName);
                if (value != null) {
                    resultStr = resultStr.replace(placeHolder, value);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("There is no value to replace {} in {},variables:{}", placeHolder, rawStr, variables);
                }
            }
        }
        return resultStr;
    }
}
