package com.tencent.bk.audit.utils;

import com.tencent.bk.audit.constants.AuditAttributeNames;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VariableResolverTest {

    @Test
    void resolveVariables() {
        String str = "Launch job task ({{" + AuditAttributeNames.INSTANCE_ID + "}})[{{" +
                AuditAttributeNames.INSTANCE_NAME + "}}]";
        Map<String, String> variables = new HashMap<>();
        variables.put(AuditAttributeNames.INSTANCE_ID, "1000");
        variables.put(AuditAttributeNames.INSTANCE_NAME, "Audit test job");
        String result = VariableResolver.resolveVariables(str, variables);

        assertThat(result).isEqualTo("Launch job task (1000)[Audit test job]");
    }
}