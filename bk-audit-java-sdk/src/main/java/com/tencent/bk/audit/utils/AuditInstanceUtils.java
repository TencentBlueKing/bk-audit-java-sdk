package com.tencent.bk.audit.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 审计资源实例处理工具类
 */
public class AuditInstanceUtils {
    /**
     * @param instances 资源实例列表
     * @param f         instance -> instanceId function
     * @param <T>       资源实例类型
     * @return 资源实例ID，按照审计中心要求多个实例ID之间用","分割
     */
    public static <T> String extractInstanceIds(Collection<T> instances, Function<T, String> f) {
        if (CollectionUtils.isEmpty(instances)) {
            return null;
        }
        return instances.stream().map(f).collect(Collectors.joining(","));
    }

    /**
     * 资源实例转换
     *
     * @param instances 资源实例列表
     * @param f         转换Function
     * @param <T>       资源实例类型
     * @param <R>       转换类型
     * @return 转换之后的列表
     */
    public static <T, R> List<R> mapInstanceList(Collection<T> instances, Function<T, R> f) {
        return instances.stream().map(f).collect(Collectors.toList());
    }
}
