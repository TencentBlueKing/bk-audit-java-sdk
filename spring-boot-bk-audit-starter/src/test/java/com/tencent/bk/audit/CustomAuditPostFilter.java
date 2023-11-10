package com.tencent.bk.audit;

import com.tencent.bk.audit.filter.AuditPostFilter;
import com.tencent.bk.audit.model.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomAuditPostFilter implements AuditPostFilter {

    @Override
    public AuditEvent map(AuditEvent auditEvent) {
        auditEvent.addExtendData("test", "SpringBootAuditPostFilterTest");
        return auditEvent;
    }
}
