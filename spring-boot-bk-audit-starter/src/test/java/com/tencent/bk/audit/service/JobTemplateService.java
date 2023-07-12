package com.tencent.bk.audit.service;

import com.tencent.bk.audit.annotations.ActionAuditRecord;
import com.tencent.bk.audit.annotations.AuditInstanceRecord;
import com.tencent.bk.audit.model.JobPlan;
import com.tencent.bk.audit.model.JobTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

/**
 * 作业模版 Service
 */
@Service
public class JobTemplateService {
    private final Random random = new SecureRandom();
    private final JobPlanService jobPlanService;

    @Autowired
    public JobTemplateService(JobPlanService jobPlanService) {
        this.jobPlanService = jobPlanService;
    }

    public JobTemplate getTemplateById(long templateId) {
        JobTemplate template = new JobTemplate();
        template.setId(templateId);
        template.setName(buildTemplateName(templateId));
        template.setDescription("job_template_desc_" + templateId);
        return template;
    }

    private String buildTemplateName(long templateId) {
        return "job_template_" + templateId;
    }

    @ActionAuditRecord(
            actionId = "create_job_template",
            instance = @AuditInstanceRecord(
                    resourceType = "job_template",
                    instanceIds = "#$?.id",
                    instanceNames = "#$?.name"
            ),
            content = "Create job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public JobTemplate createJobTemplate(String templateName, String templateDesc) {
        JobTemplate template = new JobTemplate();
        long templateId = Math.abs(random.nextLong());
        template.setId(templateId);
        template.setName(templateName);
        template.setDescription(templateDesc);
        return template;
    }

    @ActionAuditRecord(
            actionId = "delete_job_template",
            instance = @AuditInstanceRecord(
                    resourceType = "job_template",
                    instanceIds = "#templateId",
                    instanceNames = "#$?.name"
            ),
            content = "Delete job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public JobTemplate deleteJobTemplate(Long templateId) {
        JobTemplate jobTemplate = getTemplateById(templateId);
        deleteJobPlansByTemplateId(templateId);
        return jobTemplate;
    }

    private void deleteJobPlansByTemplateId(long templateId) {
        List<JobPlan> planList = jobPlanService.getPlanByTemplateId(templateId);
        planList.forEach(plan -> jobPlanService.deleteJobPlan(plan.getId()));
    }


}
