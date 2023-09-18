package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.audit.context.AuditContext;
import com.tencent.bk.audit.example.MultiAuditEventExample;
import com.tencent.bk.audit.example.SingleAuditEventExample;
import com.tencent.bk.audit.example.SubAuditEventExample;
import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.model.AuditEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AuditTest {
    private static final EventExporter eventExporter;
    private static final AuditClient auditClient;

    static {
        eventExporter = mock(EventExporter.class);
        AuditExceptionResolver auditExceptionResolver = new DefaultAuditExceptionResolver();
        auditClient = new AuditClient(eventExporter, auditExceptionResolver);
    }

    @AfterEach
    void reset() {
        Mockito.reset(eventExporter);
    }


    @Test
    @DisplayName("验证审计 - 一次操作（请求）产生一个审计事件")
    void testSingleAuditEvent() {
        SingleAuditEventExample example = new SingleAuditEventExample(auditClient);
        example.run();

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(1);
        AuditEvent auditEvent = auditEvents.stream().findAny().orElse(null);
        assertThat(auditEvent).isNotNull();
        assertNotNull(auditEvent.getId());
        assertEquals("bk_job", auditEvent.getSystemId());
        assertEquals("execute_job_plan", auditEvent.getActionId());
        assertEquals("job_plan", auditEvent.getResourceTypeId());
        assertEquals("1000", auditEvent.getInstanceId());
        assertEquals("test_audit_execute_job_plan", auditEvent.getInstanceName());
        assertEquals("3a84858499bd71d674bc40d4f73cb41a", auditEvent.getRequestId());
        assertEquals("admin", auditEvent.getUsername());
        assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
        assertEquals("127.0.0.1", auditEvent.getAccessSourceIp());
        assertEquals("Chrome", auditEvent.getAccessUserAgent());
        assertEquals(AccessTypeEnum.CONSOLE.getValue(), auditEvent.getAccessType());
        assertEquals("Execute job plan [test_audit_execute_job_plan](1000)", auditEvent.getContent());
        assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
        assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
        assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
        assertNotNull(auditEvent.getStartTime());
        assertNotNull(auditEvent.getEndTime());
    }

    @Test
    @DisplayName("验证审计 - 一次操作（请求）产生多个审计事件")
    void testMultiAuditEvent() {
        MultiAuditEventExample example = new MultiAuditEventExample(auditClient);
        example.run();

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(2);
        assertThat(auditEvents).extracting("instanceId").containsOnly("1000", "1001");
        assertThat(auditEvents).extracting("instanceName").containsOnly("plan1", "plan2");
        assertThat(auditEvents).extracting("content")
                .containsOnly("Edit job plan [plan1](1000)", "Edit job plan [plan2](1001)");
        auditEvents.forEach(auditEvent -> {
            assertThat(auditEvent).isNotNull();
            assertNotNull(auditEvent.getId());
            assertEquals("bk_job", auditEvent.getSystemId());
            assertEquals("edit_job_plan", auditEvent.getActionId());
            assertEquals("job_plan", auditEvent.getResourceTypeId());
            assertEquals("3a84858499bd71d674bc40d4f73cb41a", auditEvent.getRequestId());
            assertEquals("admin", auditEvent.getUsername());
            assertEquals("127.0.0.1", auditEvent.getAccessSourceIp());
            assertEquals("Chrome", auditEvent.getAccessUserAgent());
            assertEquals(AccessTypeEnum.CONSOLE.getValue(), auditEvent.getAccessType());
            assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
            assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
            assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
            assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
            assertNotNull(auditEvent.getStartTime());
            assertNotNull(auditEvent.getEndTime());
        });
    }

    @Test
    @DisplayName("验证审计 - 一次操作（请求）包含多个 Action")
    void testSubAuditEvent() {
        SubAuditEventExample example = new SubAuditEventExample(auditClient);
        example.run();

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(3);
        List<AuditEvent> mainEvents = auditEvents.stream()
                .filter(auditEvent -> auditEvent.getActionId().equals("delete_job_template"))
                .collect(Collectors.toList());
        assertThat(mainEvents).hasSize(1);
        AuditEvent mainEvent = mainEvents.get(0);
        assertThat(mainEvent).isNotNull();
        assertNotNull(mainEvent.getId());
        assertEquals("bk_job", mainEvent.getSystemId());
        assertEquals("delete_job_template", mainEvent.getActionId());
        assertEquals("job_template", mainEvent.getResourceTypeId());
        assertEquals("3a84858499bd71d674bc40d4f73cb41a", mainEvent.getRequestId());
        assertEquals("admin", mainEvent.getUsername());
        assertEquals("127.0.0.1", mainEvent.getAccessSourceIp());
        assertEquals("Chrome", mainEvent.getAccessUserAgent());
        assertEquals(AccessTypeEnum.CONSOLE.getValue(), mainEvent.getAccessType());
        assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), mainEvent.getUserIdentifyType());
        assertEquals("bk_audit_event", mainEvent.getAuditEventSignature());
        assertEquals(Constants.RESULT_CODE_SUCCESS, mainEvent.getResultCode());
        assertEquals(Constants.RESULT_SUCCESS_DESC, mainEvent.getResultContent());
        assertNotNull(mainEvent.getStartTime());
        assertNotNull(mainEvent.getEndTime());
        assertEquals("1000", mainEvent.getInstanceId());
        assertEquals("job_template_1", mainEvent.getInstanceName());

        List<AuditEvent> subEvents = auditEvents.stream()
                .filter(auditEvent -> auditEvent.getActionId().equals("delete_job_plan"))
                .collect(Collectors.toList());
        assertThat(subEvents).extracting("instanceId").containsOnly("1001", "1002");
        assertThat(subEvents).extracting("instanceName").containsOnly("plan1", "plan2");
        assertThat(subEvents).extracting("content")
                .containsOnly("Delete job plan [plan1](1001)", "Delete job plan [plan2](1002)");
        subEvents.forEach(auditEvent -> {
            assertThat(auditEvent).isNotNull();
            assertNotNull(auditEvent.getId());
            assertEquals("bk_job", auditEvent.getSystemId());
            assertEquals("delete_job_plan", auditEvent.getActionId());
            assertEquals("job_plan", auditEvent.getResourceTypeId());
            assertEquals("3a84858499bd71d674bc40d4f73cb41a", auditEvent.getRequestId());
            assertEquals("admin", auditEvent.getUsername());
            assertEquals("127.0.0.1", auditEvent.getAccessSourceIp());
            assertEquals("Chrome", auditEvent.getAccessUserAgent());
            assertEquals(AccessTypeEnum.CONSOLE.getValue(), auditEvent.getAccessType());
            assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
            assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
            assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
            assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
            assertNotNull(auditEvent.getStartTime());
            assertNotNull(auditEvent.getEndTime());
        });
    }

    @Test
    @DisplayName("验证审计操作异常事件")
    void testAuditActionException() {
        AuditContext auditContext = auditClient.auditContextBuilder("execute_job_plan")
                .setRequestId("3a84858499bd71d674bc40d4f73cb41a")
                .setSystemId("bk_job")
                .setAccessSourceIp("127.0.0.1")
                .setAccessType(AccessTypeEnum.CONSOLE)
                .setAccessUserAgent("Chrome")
                .setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL)
                .setUsername("admin")
                .build();
        try {
            auditClient.audit(auditContext, () -> {
                ActionAuditContext.builder("execute_job_plan")
                        .setResourceType("job_plan")
                        .setInstanceId("1000")
                        .setInstanceName("test_audit_execute_job_plan")
                        .setContent("Execute job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                        .build()
                        .wrapActionRunnable(() -> {
                            throw new RuntimeException("Execute job plan error");
                        })
                        .run();
            });
        } catch (Throwable e) {
            // 捕获异常，继续测试用例执行
        }

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(1);
        AuditEvent auditEvent = auditEvents.stream().findAny().orElse(null);
        assertThat(auditEvent).isNotNull();
        assertNotNull(auditEvent.getId());
        assertEquals("bk_job", auditEvent.getSystemId());
        assertEquals("execute_job_plan", auditEvent.getActionId());
        assertEquals("3a84858499bd71d674bc40d4f73cb41a", auditEvent.getRequestId());
        assertEquals("admin", auditEvent.getUsername());
        assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
        assertEquals("127.0.0.1", auditEvent.getAccessSourceIp());
        assertEquals("Chrome", auditEvent.getAccessUserAgent());
        assertEquals(AccessTypeEnum.CONSOLE.getValue(), auditEvent.getAccessType());
        assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
        assertEquals(Constants.RESULT_CODE_ERROR, auditEvent.getResultCode());
        assertEquals(Constants.RESULT_ERROR_DESC, auditEvent.getResultContent());
        assertNotNull(auditEvent.getStartTime());
        assertNotNull(auditEvent.getEndTime());
    }


}
