package com.tencent.bk.audit.controller;

import com.tencent.bk.audit.annotations.AuditEntry;
import com.tencent.bk.audit.annotations.AuditRequestBody;
import com.tencent.bk.audit.model.CreateJobTemplateRequest;
import com.tencent.bk.audit.model.ExecuteScriptRequest;
import com.tencent.bk.audit.model.JobTemplate;
import com.tencent.bk.audit.service.JobExecuteService;
import com.tencent.bk.audit.service.JobTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/audit/action")
public class AuditTestController {
    private final JobTemplateService jobTemplateService;
    private final JobExecuteService jobExecuteService;

    @Autowired
    public AuditTestController(JobTemplateService jobTemplateService,
                               JobExecuteService jobExecuteService) {
        this.jobTemplateService = jobTemplateService;
        this.jobExecuteService = jobExecuteService;
    }

    @AuditEntry(
            actionId = "view_job_template"
    )
    @GetMapping("/scope/{scopeType}/{scopeId}/template/{templateId}")
    public JobTemplate getJobTemplateById(
            @PathVariable("scopeType") String scopeType,
            @PathVariable("scopeId") String scopeId,
            @PathVariable("templateId") Long templateId) {
        return jobTemplateService.getTemplateById(Long.valueOf(scopeId), templateId);
    }

    @AuditEntry(
            actionId = "create_job_template"
    )
    @PostMapping("/scope/{scopeType}/{scopeId}/template")
    public JobTemplate createJobTemplate(
            @PathVariable("scopeType") String scopeType,
            @PathVariable("scopeId") String scopeId,
            @AuditRequestBody
            @RequestBody
                    CreateJobTemplateRequest request) {
        return jobTemplateService.createJobTemplate(Long.valueOf(scopeId), request.getName(), request.getDescription());
    }

    /**
     * 删除作业模版，同时删除作业模版包含的作业执行方案
     *
     * @param templateId 作业模版 ID
     */
    @AuditEntry(
            actionId = "delete_job_template",
            subActionIds = "delete_job_plan"
    )
    @DeleteMapping("/scope/{scopeType}/{scopeId}/template/{templateId}")
    public void deleteJobTemplate(
            @PathVariable("scopeType") String scopeType,
            @PathVariable("scopeId") String scopeId,
            @PathVariable("templateId") Long templateId) {
        jobTemplateService.deleteJobTemplate(Long.valueOf(scopeId), templateId);
    }


    @AuditEntry(
            actionId = "execute_script"
    )
    @PostMapping("/executeScript")
    public void executeScript(@AuditRequestBody
                              @RequestBody
                                      ExecuteScriptRequest request) {
        jobExecuteService.executeScript(request.getScriptId(), request.getHosts());
    }
}
