package com.tencent.bk.audit;

import com.tencent.bk.audit.filter.AuditPostFilter;
import com.tencent.bk.audit.filter.AuditPostFilters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 审计SDK初始化
 */
@Slf4j
public class AuditApplicationRunner implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("AuditApplicationRunner start");
        Map<String, AuditPostFilter> auditPostFilterMap =
                applicationContext.getBeansOfType(AuditPostFilter.class);
        if (MapUtils.isEmpty(auditPostFilterMap)) {
            return;
        }
        List<AuditPostFilter> filters = new ArrayList<>(auditPostFilterMap.values());
        AnnotationAwareOrderComparator.sort(filters);
        filters.forEach(AuditPostFilters::addFilter);
        log.info("AuditApplicationRunner run success");
    }
}
