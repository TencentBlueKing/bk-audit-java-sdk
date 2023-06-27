package com.tencent.bk.audit;

@FunctionalInterface
public interface ActionCallable<V> {
    V call();
}
