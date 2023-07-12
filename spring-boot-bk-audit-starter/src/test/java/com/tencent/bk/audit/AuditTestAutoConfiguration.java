package com.tencent.bk.audit;

import com.tencent.bk.audit.exporter.EventExporter;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AuditTestAutoConfiguration {

    @Bean("mockEventExporter")
    EventExporter mockEventExporter() {
        return Mockito.mock(EventExporter.class);
    }

}
