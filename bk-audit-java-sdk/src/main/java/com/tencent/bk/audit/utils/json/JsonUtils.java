package com.tencent.bk.audit.utils.json;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON工具
 */
@Slf4j
public class JsonUtils {

    private static final Map<String, JsonMapper> JSON_MAPPERS = new ConcurrentHashMap<>();

    /**
     * 从Json串中解析成bean对象,支持参数泛型
     *
     * @param jsonString    json
     * @param typeReference 类型
     * @param <T>           bean Class 类型
     * @return bean
     */
    public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) throws JsonParseException {
        return JSON_MAPPERS.computeIfAbsent("__all__", s -> JsonMapper.alwaysOutputMapper()).fromJson(jsonString,
                typeReference);
    }

    /**
     * 从Json串中解析成bean对象
     *
     * @param jsonString json
     * @param beanClass  bean Class 类型
     * @param <T>        bean Class 类型
     * @return bean
     */
    public static <T> T fromJson(String jsonString, Class<T> beanClass) throws JsonParseException {
        return JSON_MAPPERS.computeIfAbsent("__all__", s -> JsonMapper.alwaysOutputMapper()).fromJson(jsonString,
                beanClass);
    }

    /**
     * 创建输出所有字段的Json，不管字段值是默认值 还是等于 null 还是空集合的字段，全输出,可用于外部接口协议输出
     *
     * @param bean bean
     * @param <T>  bean
     * @return json
     */
    public static <T> String toJson(T bean) throws JsonParseException {
        return JSON_MAPPERS.computeIfAbsent("__all__", s -> JsonMapper.alwaysOutputMapper()).toJson(bean);
    }

    public static <T> String toNonEmptyJson(T bean) {
        return JSON_MAPPERS.computeIfAbsent("__non_empty__", s -> JsonMapper.nonEmptyMapper()).toJson(bean);
    }

    public static <T> String toNonDefault(T bean) {
        return JSON_MAPPERS.computeIfAbsent("__non_default__", s -> JsonMapper.nonDefaultMapper()).toJson(bean);
    }

}
