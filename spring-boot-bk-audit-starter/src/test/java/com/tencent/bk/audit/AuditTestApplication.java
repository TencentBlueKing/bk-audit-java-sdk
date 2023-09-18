package com.tencent.bk.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootApplication
@ActiveProfiles("test")
public class AuditTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditTestApplication.class, args);
    }
}
