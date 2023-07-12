package com.tencent.bk.audit.service;

import com.tencent.bk.audit.annotations.ActionAuditRecord;
import com.tencent.bk.audit.annotations.AuditInstanceRecord;
import com.tencent.bk.audit.model.JobPlan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

/**
 * 作业执行方案 Service
 */
@Service
public class JobPlanService {

    public JobPlan getPlanById(long planId) {
        JobPlan plan = new JobPlan();
        plan.setId(planId);
        plan.setName(buildPlanName(planId));
        return plan;
    }

    public List<JobPlan> getPlanByTemplateId(long templateId) {
        List<JobPlan> plans = new ArrayList<>(2);
        JobPlan plan1 = new JobPlan();
        plan1.setId(1L);
        plan1.setName(buildPlanName(plan1.getId()));
        plans.add(plan1);

        JobPlan plan2 = new JobPlan();
        plan2.setId(2L);
        plan2.setName(buildPlanName(plan2.getId()));
        plans.add(plan2);
        return plans;
    }

    private String buildPlanName(long planId) {
        return "job_plan_" + planId;
    }

    @ActionAuditRecord(
            actionId = "delete_job_plan",
            instance = @AuditInstanceRecord(
                    resourceType = "job_plan",
                    instanceIds = "#planId",
                    instanceNames = "#$?.name"
            ),
            content = "Delete job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public JobPlan deleteJobPlan(Long planId) {
        JobPlan jobPlan = getPlanById(planId);
        // delete plan code here
        return jobPlan;
    }


}
