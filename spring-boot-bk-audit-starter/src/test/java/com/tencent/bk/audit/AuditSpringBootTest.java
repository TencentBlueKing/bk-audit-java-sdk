package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.model.*;
import com.tencent.bk.audit.utils.json.JsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuditSpringBootTest {

    private final MockMvc mockMvc;
    private final EventExporter eventExporter;

    @Autowired
    public AuditSpringBootTest(MockMvc mockMvc, EventExporter eventExporter) {
        this.mockMvc = mockMvc;
        this.eventExporter = eventExporter;
    }

    @AfterEach
    void reset() {
        Mockito.reset(eventExporter);
    }

    @Test
    @DisplayName("审计操作 - 基础测试案例1")
    public void testSimpleAudit1() throws Exception {
        String requestId = UUID.randomUUID().toString();
        this.mockMvc.perform(
                get("/test/audit/action/getJobTemplateById/template/1")
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler")
                        .header(DefaultAuditRequestProvider.HEADER_REQUEST_ID, requestId)
                        .header(DefaultAuditRequestProvider.HEADER_ACCESS_TYPE, AccessTypeEnum.WEB.getValue())
                        .header(DefaultAuditRequestProvider.HEADER_USER_IDENTIFY_TYPE,
                                UserIdentifyTypeEnum.PERSONAL.getValue())
                        .header("User-Agent", "Chrome")
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler"))
                .andExpect(status().isOk());

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(1);
        AuditEvent auditEvent = auditEvents.stream().findAny().orElse(null);
        assertThat(auditEvent).isNotNull();
        assertNotNull(auditEvent.getId());
        assertEquals("view_job_template", auditEvent.getActionId());
        assertEquals("job_template", auditEvent.getResourceTypeId());
        assertEquals("1", auditEvent.getInstanceId());
        assertEquals("job_template_1", auditEvent.getInstanceName());
        assertEquals(requestId, auditEvent.getRequestId());
        assertEquals("tyler", auditEvent.getUsername());
        assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
        assertNotNull(auditEvent.getAccessSourceIp());
        assertEquals("Chrome", auditEvent.getAccessUserAgent());
        assertEquals(AccessTypeEnum.WEB.getValue(), auditEvent.getAccessType());
        assertEquals("View job template [job_template_1](1)", auditEvent.getContent());
        assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
        assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
        assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
        assertNotNull(auditEvent.getStartTime());
        assertNotNull(auditEvent.getEndTime());

    }

    @Test
    @DisplayName("审计操作 - 基础测试案例2")
    public void testSimpleAudit2() throws Exception {
        String requestId = UUID.randomUUID().toString();
        CreateJobTemplateRequest request = new CreateJobTemplateRequest();
        request.setName("test_audit_create_job_template");
        request.setDescription("test_audit_create_job_template_desc");
        MvcResult mockResult = this.mockMvc.perform(
                post("/test/audit/action/createJobTemplate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(request))
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler")
                        .header(DefaultAuditRequestProvider.HEADER_REQUEST_ID, requestId)
                        .header(DefaultAuditRequestProvider.HEADER_ACCESS_TYPE, AccessTypeEnum.WEB.getValue())
                        .header(DefaultAuditRequestProvider.HEADER_USER_IDENTIFY_TYPE,
                                UserIdentifyTypeEnum.PERSONAL.getValue())
                        .header("User-Agent", "Chrome")
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler"))
                .andExpect(status().isOk())
                .andReturn();
        JobTemplate createdJobTemplate = JsonUtils.fromJson(
                mockResult.getResponse().getContentAsString(), JobTemplate.class);

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(1);
        AuditEvent auditEvent = auditEvents.stream().findAny().orElse(null);
        assertThat(auditEvent).isNotNull();
        assertNotNull(auditEvent.getId());
        assertEquals("create_job_template", auditEvent.getActionId());
        assertEquals("job_template", auditEvent.getResourceTypeId());
        assertEquals(String.valueOf(createdJobTemplate.getId()), auditEvent.getInstanceId());
        assertEquals("test_audit_create_job_template", auditEvent.getInstanceName());
        assertEquals(requestId, auditEvent.getRequestId());
        assertEquals("tyler", auditEvent.getUsername());
        assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
        assertNotNull(auditEvent.getAccessSourceIp());
        assertEquals("Chrome", auditEvent.getAccessUserAgent());
        assertEquals(AccessTypeEnum.WEB.getValue(), auditEvent.getAccessType());
        assertEquals(
                "Create job template [test_audit_create_job_template](" + createdJobTemplate.getId() + ")",
                auditEvent.getContent());
        assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
        assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
        assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
        assertNotNull(auditEvent.getStartTime());
        assertNotNull(auditEvent.getEndTime());

    }


    @Test
    @DisplayName("审计操作 - 测试一次请求包含多个操作")
    public void testAuditMultiAction() throws Exception {
        String requestId = UUID.randomUUID().toString();
        this.mockMvc.perform(
                delete("/test/audit/action/deleteJobTemplate/template/1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler")
                        .header(DefaultAuditRequestProvider.HEADER_REQUEST_ID, requestId)
                        .header(DefaultAuditRequestProvider.HEADER_ACCESS_TYPE, AccessTypeEnum.WEB.getValue())
                        .header(DefaultAuditRequestProvider.HEADER_USER_IDENTIFY_TYPE,
                                UserIdentifyTypeEnum.PERSONAL.getValue())
                        .header("User-Agent", "Chrome")
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler"))
                .andExpect(status().isOk())
                .andReturn();

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
        assertEquals(mainEvent.getInstanceId(), "1000");
        assertEquals(mainEvent.getInstanceName(), "job_template_1000");
        assertEquals(mainEvent.getResourceTypeId(), "job_template");
        assertEquals(mainEvent.getActionId(), "delete_job_template");
        assertEquals(mainEvent.getContent(), "Delete job template [job_template_1000](1000)");

        List<AuditEvent> subEvents = auditEvents.stream()
                .filter(auditEvent -> auditEvent.getActionId().equals("delete_job_plan"))
                .collect(Collectors.toList());
        assertThat(subEvents).hasSize(2);
        assertThat(subEvents).extracting("instanceId").containsOnly("1", "2");
        assertThat(subEvents).extracting("instanceName").containsOnly("job_plan_1", "job_plan_2");
        assertThat(subEvents).extracting("content")
                .containsOnly("Delete job plan [job_plan_1](1)", "Delete job plan [job_plan_2](2)");

        auditEvents.forEach(auditEvent -> {
            assertThat(auditEvent).isNotNull();
            assertNotNull(auditEvent.getId());
            assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
            assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
            assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
            assertNotNull(auditEvent.getStartTime());
            assertNotNull(auditEvent.getEndTime());
            assertEquals(requestId, auditEvent.getRequestId());
            assertEquals("tyler", auditEvent.getUsername());
            assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
            assertNotNull(auditEvent.getAccessSourceIp());
            assertEquals("Chrome", auditEvent.getAccessUserAgent());
            assertEquals(AccessTypeEnum.WEB.getValue(), auditEvent.getAccessType());
        });
    }

    @Test
    @DisplayName("审计操作 - 测试自定义审计事件生成")
    public void testCustomAuditEventBuilder() throws Exception {
        String requestId = UUID.randomUUID().toString();
        ExecuteScriptRequest request = new ExecuteScriptRequest();
        request.setScriptId(1000L);
        List<Host> hosts = new ArrayList<>(2);
        hosts.add(new Host(1L, "127.0.0.1"));
        hosts.add(new Host(2L, "127.0.0.2"));
        request.setHosts(hosts);
        this.mockMvc.perform(
                post("/test/audit/action/executeScript")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(request))
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler")
                        .header(DefaultAuditRequestProvider.HEADER_REQUEST_ID, requestId)
                        .header(DefaultAuditRequestProvider.HEADER_ACCESS_TYPE, AccessTypeEnum.WEB.getValue())
                        .header(DefaultAuditRequestProvider.HEADER_USER_IDENTIFY_TYPE,
                                UserIdentifyTypeEnum.PERSONAL.getValue())
                        .header("User-Agent", "Chrome")
                        .header(DefaultAuditRequestProvider.HEADER_USERNAME, "tyler"))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<Collection<AuditEvent>> argument = ArgumentCaptor.forClass(Collection.class);
        verify(eventExporter).export(argument.capture());
        verify(eventExporter).export(anyList());

        Collection<AuditEvent> auditEvents = argument.getValue();
        assertThat(auditEvents).hasSize(1);
        AuditEvent auditEvent = auditEvents.stream().findAny().orElse(null);
        assertThat(auditEvent).isNotNull();
        assertNotNull(auditEvent.getId());
        assertEquals("execute_script", auditEvent.getActionId());
        assertEquals("host", auditEvent.getResourceTypeId());
        assertEquals("1,2", auditEvent.getInstanceId());
        assertEquals("127.0.0.1,127.0.0.2", auditEvent.getInstanceName());
        assertEquals(requestId, auditEvent.getRequestId());
        assertEquals("tyler", auditEvent.getUsername());
        assertEquals(UserIdentifyTypeEnum.PERSONAL.getValue(), auditEvent.getUserIdentifyType());
        assertNotNull(auditEvent.getAccessSourceIp());
        assertEquals("Chrome", auditEvent.getAccessUserAgent());
        assertEquals(AccessTypeEnum.WEB.getValue(), auditEvent.getAccessType());
        assertEquals("Execute script [script_1000](1000)", auditEvent.getContent());
        assertEquals("bk_audit_event", auditEvent.getAuditEventSignature());
        assertEquals(Constants.RESULT_CODE_SUCCESS, auditEvent.getResultCode());
        assertEquals(Constants.RESULT_SUCCESS_DESC, auditEvent.getResultContent());
        assertNotNull(auditEvent.getStartTime());
        assertNotNull(auditEvent.getEndTime());
    }
}
