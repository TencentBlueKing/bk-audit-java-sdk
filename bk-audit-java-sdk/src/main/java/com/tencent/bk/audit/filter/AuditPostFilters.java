package com.tencent.bk.audit.filter;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * AuditPostFilter 管理
 */
@Slf4j
public class AuditPostFilters {
    private static final List<AuditPostFilter> filters = new ArrayList<>();

    public static void addFilter(AuditPostFilter filter) {
        log.info("Add AuditPostFilter: {}", filter);
        filters.add(filter);
    }

    public static List<AuditPostFilter> getFilters() {
        return filters;
    }

}
