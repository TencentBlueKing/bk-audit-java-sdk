package com.tencent.bk.audit.service;

import com.tencent.bk.audit.annotations.ActionAuditRecord;
import com.tencent.bk.audit.annotations.AuditInstanceRecord;
import com.tencent.bk.audit.exception.NotFoundException;
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


    @ActionAuditRecord(
            actionId = "view_job_template",
            instance = @AuditInstanceRecord(
                    resourceType = "job_template",
                    instanceIds = "#templateId",
                    instanceNames = "#$?.name"
            ),
            scopeType = "'biz'",
            scopeId = "#bizId",
            content = "View job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public JobTemplate getTemplateById(Long bizId, long templateId) {
        if (templateId == 0) {
            throw new NotFoundException();
        }
        JobTemplate template = new JobTemplate();
        template.setBizId(bizId);
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
            scopeType = "'biz'",
            scopeId = "#bizId",
            content = "Create job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public JobTemplate createJobTemplate(Long bizId, String templateName, String templateDesc) {
        JobTemplate template = new JobTemplate();
        long templateId = Math.abs(random.nextLong());
        template.setId(templateId);
        template.setName(templateName);
        template.setDescription(templateDesc);
        template.setBizId(bizId);
        return template;
    }

    @ActionAuditRecord(
            actionId = "delete_job_template",
            instance = @AuditInstanceRecord(
                    resourceType = "job_template",
                    instanceIds = "#templateId",
                    instanceNames = "#$?.name"
            ),
            scopeType = "'biz'",
            scopeId = "#bizId",
            content = "Delete job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public JobTemplate deleteJobTemplate(Long bizId, Long templateId) {
        JobTemplate jobTemplate = getTemplateById(bizId, templateId);
        deleteJobPlansByTemplateId(bizId, templateId);
        return jobTemplate;
    }

    private void deleteJobPlansByTemplateId(Long bizId, long templateId) {
        List<JobPlan> planList = jobPlanService.getPlanByTemplateId(templateId);
        planList.forEach(plan -> jobPlanService.deleteJobPlan(bizId, plan.getId()));
    }
}
