# BkAudit Java SDK

BkAudit Java SDK 使用说明文档。



## 类型说明

- 审计事件 AuditEvent

AuditEvent 定义了审计事件的标准字段。详见 [AuditEvent](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/model/AuditEvent.java)

- 操作 Action

Action 实际上为 iam 模型中的 Action 对象，详见 [iam.model.models.Action](https://github.com/TencentBlueKing/iam-python-sdk/blob/master/iam/model/models.py)

- 资源类型 ResourceType

ResourceType 实际上为 iam 模型中的 ResourceType 对象，详见 [iam.model.models.ResourceType](https://github.com/TencentBlueKing/iam-python-sdk/blob/master/iam/model/models.py)

- 审计上下文 AuditContext

一次请求（可能包含多个操作）的审计上下文，详见 [AuditContext](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/context/AuditContext.java)

- 操作审计上下文 ActionAuditContext

一个操作(Action)的审计上下文。一个 AuditContext 可能包含多个 ActionAuditContext ，一个 ActionAuditContext 可能包含多个 AuditEvent。详见 [ActionAuditContext](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/context/ActionAuditContext.java)

- 审计事件生成 AuditEventBuilder

AuditEventBuilder 用于从 ActionAuditContext 中构造 AuditEvent。可以通过扩展 AuditEventBuilder 自定义审计事件的生成。详见 [AuditEventBuilder](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/AuditEventBuilder.java)

- 审计事件输出 EventExporter

EventExporter 用于输出审计事件，如输出到日志文件，标准输出流，Push 事件到审计中心等。可以通过扩展 EventExporter 自定义审计事件的输出。详见 [EventExporter](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/exporter/EventExporter.java)

- 审计上下文属性 AuditAttribute

AuditAttribute 用于自定义审计上下文中的属性。构造审计事件(AuditEvent)的时候可能会用到审计上下文属性。标准化的名称定义详见 [AuditAttributeNames](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/constants/AuditAttributeNames.java)



## 基本使用

Java 程序通用，通过编码方式实现审计接入。



### 依赖声明

- build.gradle
```groovy
implementation 'com.tencent.bk.sdk:bk-audit-java-sdk:1.0.0'
```
- pom.xml
```xml
<dependency>
    <groupId>com.tencent.bk.sdk</groupId>
    <artifactId>bk-audit-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```



### 初始化 AuditClient (单例)

AuditClient 为 SDK 的操作客户端，提供了审计管理的 API。AuditClient 是线程安全的。对于一个应用服务来说，只需要初始化一个单例的 AuditClient。

```java
// 初始化审计事件 Exporter
EventExporter eventExporter = new LogFileEventExporter();
// 初始化审计操作异常解析器
AuditExceptionResolver auditExceptionResolver = new DefaultAuditExceptionResolver();
// 初始化 AuditClient
AuditClient auditClient = new AuditClient(eventExporter, auditExceptionResolver);
```



### 审计 SDK API 使用

```java
public class SingleAuditEventExample {
    private final AuditClient auditClient;

    public SingleAuditEventExample(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    public void run() {
        // 构造审计上下文
        AuditContext auditContext = auditClient.auditContextBuilder("execute_job_plan")
                .setRequestId("3a84858499bd71d674bc40d4f73cb41a")
                .setAccessSourceIp("127.0.0.1")
                .setAccessType(AccessTypeEnum.CONSOLE)
                .setAccessUserAgent("Chrome")
                .setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL)
                .setUsername("admin")
                .build();
        // 对操作进行审计
        auditClient.audit(auditContext, this::action);
    }

    /**
     * 操作
     */ 
    private void action() {
        // 使用 ActionAuditContext 封装 Action 代码，自动封装对操作的审计处理
        ActionAuditContext.builder("execute_job_plan")
                .setResourceType("job_plan")
                .setInstanceId("1000")
                .setInstanceName("test_audit_execute_job_plan")
                .setContent("Execute job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                .build()
                .wrapActionRunnable(() -> {
                    // action code
                    ActionAuditContext.current().addAttribute("host_id", "1,2,3,4"); // 获取当前操作审计上下文，增加审计属性
                })
                .run();
    }
}
```



## 与 SpringBoot 集成

- 自动配置，开箱即用
- 支持通过注解声明审计事件，大量减少编码成本；与业务代码尽可能解耦
- 提供了完整的操作审计管理、异常处理，SDK 接入方只需关注业务审计数据的提供
- 灵活定制

### 依赖声明
- build.gradle
```groovy
implementation 'com.tencent.bk.sdk:spring-boot-bk-audit-starter:1.0.0'
```
- pom.xml
```xml
<dependency>
    <groupId>com.tencent.bk.sdk</groupId>
    <artifactId>spring-boot-bk-audit-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置文件

如果使用了 spring-boot-bk-audit-starter 中默认的日志文件方式输出审计事件，那么需要定义日志组件框架(logback/log4j/log4j2 等)的 Logger。以 logback 日志组件为例:

- logback.xml

```xml
    <!-- 审计事件日志 Appender -->
    <appender name="audit-event-appender" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${AUDIT_EVENT_LOG_FILE}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${AUDIT_EVENT_LOG_FILE}-%d{yyyy-MM-dd}.log.%i</fileNamePattern>
            <maxFileSize>1GB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${AUDIT_EVENT_LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 审计事件日志 Logger，name 值固定为"bk_audit" -->
    <logger name="bk_audit" level="INFO" additivity="false">
        <appender-ref ref="audit-event-appender"/>
    </logger>
```



### 配置项

| 配置项              | 默认值   | 说明                                  |
| :------------------ | :------- | :------------------------------------ |
| audit.enabled       | true     | 是否开启审计功能                      |
| audit.exporter.type | log_file | 审计事件 Exporter，默认为输出日志文件 |


### 基于注解声明的审计操作
#### 注解说明
- @AuditEntry 标识审计操作入口
- @AuditRequestBody 标识审计操作 http 请求 Body
- @ActionAuditRecord 标识操作审计记录
- @AuditAttribute 自定义审计上下文属性
- @AuditInstanceRecord 标识操作实例

### 使用说明

说明文档中使用的所有代码在 [Demo](../spring-boot-bk-audit-starter/src/test/java/com/tencent/bk/audit/controller) 中都可以找到

#### 场景 1：一次请求对应一个操作

**@AuditEntry / @ActionAuditRecord 既可以声明在同一个方法上，也可以声明在不同的方法上**

```java
@RestController
@RequestMapping("/test/audit/action")
public class AuditTestController {
    private final JobTemplateService jobTemplateService;

    @Autowired
    public AuditController(JobTemplateService jobTemplateService) {
        this.jobTemplateService = jobTemplateService;
    }

    @AuditEntry(
            actionId = "view_job_template"
    )
    @ActionAuditRecord(
            actionId = "view_job_template",
            instance = @AuditInstanceRecord(
                    resourceType = "job_template",
                    instanceIds = "#templateId",
                    instanceNames = "#$?.name"
            ),
            content = "View job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    @GetMapping("/getJobTemplateById/template/{templateId}")
    public JobTemplate getJobTemplateById(@PathVariable("templateId") Long templateId) {
        return jobTemplateService.getTemplateById(templateId);
    }
}
```
**说明**

1. @AuditEntry: 声明审计入口。actionId 为需要被记录审计的操作ID
2. @ActionAuditRecord： 声明一个审计操作
3. @ActionAuditRecord.@AuditInstanceRecord： 声明审计操作对应的资源实例。instanceIds/instanceNames 支持 Spring SpEL 表达式，可以解析方法中的参数和返回值。$ 为内置的方法返回值的引用，例如#$?.name，表示引用返回的 JobTemplate.name 字段的值。
4. @ActionAuditRecord.content 操作描述。支持通过 {{属性名}} 来引用AuditAttribute（内置或通过@AuditAttribute 声明)。这里的 INSTANCE_NAME 和 INSTANCE_ID 为 com.tencent.bk.audit.constants.AuditAttributeNames 中定义的



```java
@RestController
@RequestMapping("/test/audit/action")
public class AuditTestController {
    private final JobTemplateService jobTemplateService;

    @Autowired
    public AuditController(JobTemplateService jobTemplateService) {
        this.jobTemplateService = jobTemplateService;
    }

    @AuditEntry(
            actionId = "create_job_template"
    )
    @PostMapping("/createJobTemplate")
    public JobTemplate createJobTemplate(@AuditRequestBody
                                         @RequestBody
                                                 CreateJobTemplateRequest request) {
        return jobTemplateService.createJobTemplate(request.getName(), request.getDescription());
    }
}

@Service
public class JobTemplateService {
    private final Random random = new SecureRandom();

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
}
```

**说明**

1. @AuditEntry: 声明在 Controller 层
2. @AuditRequestBody 声明 用户请求 Body 内容，会被记录到审计事件中
3. @ActionAuditRecord：声明在 Service 层



#### 场景 2：一次请求对应多个操作(Action)

[demo](../spring-boot-bk-audit-starter/src/test/java/com/tencent/bk/audit/controller/AuditTestController.java)

```java
@RestController
@RequestMapping("/test/audit/action")
public class AuditTestController {
    private final JobTemplateService jobTemplateService;

    @Autowired
    public AuditTestController(JobTemplateService jobTemplateService) {
        this.jobTemplateService = jobTemplateService;
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
    @DeleteMapping("/deleteJobTemplate/template/{templateId}")
    public void deleteJobTemplate(@PathVariable("templateId") Long templateId) {
        jobTemplateService.deleteJobTemplate(templateId);
    }
}

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
```

**说明**

1. 这个案例演示了一次请求对应多个操作的场景：删除作业模版，同时删除作业模版包含的作业执行方案。
2. 在使用 @AuditEntry 声明请求需要被审计的操作，actionId 为主要操作，subActionIds 为主要操作触发的子操作
3. 在 JobTemplateService.java 和 JobPlanService.java 中分别使用 @ActionAuditRecord 声明审计操作，这些操作会被 SDK 自动加入到审计上下文中，并输出审计事件。

### 使用进阶

#### 自定义 EventExporter

除了 SDK 中自定义的 EventExporter，开发者可以通过实现 EventExporter 接口自定义 EventExporter。

```java
@Component
public class MyEventExporter implements EventExporter {
    @Override
    public void export(AuditEvent event) {
        
    }

    @Override
    public void export(Collection<AuditEvent> events) {

    }
}
```

自定义的 EventExporter 会被 SpringBoot 自动装配到 AuditClient

#### 自定义 AuditEventBuilder

SDK 提供了审计事件生成的默认实现 DefaultAuditEventBuilder。开发者可以通过实现 AuditEventBuilder 接口或者继承 DefaultAuditEventBuilder 定制审计事件生成逻辑。在定义好了 AuditEventBuilder 后，可以在 @ActionAuditContext 注解中指定builder。

- 自定义 ExecuteJobAuditEventBuilder，实现多个资源实例合并到同一个审计事件中

```java
@RestController
@RequestMapping("/test/audit/action")
public class AuditTestController {
    private final JobExecuteService jobExecuteService;

    @Autowired
    public AuditTestController(JobExecuteService jobExecuteService) {
        this.jobExecuteService = jobExecuteService;
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

@Service
public class JobExecuteService {

    /**
     * 快速执行脚本
     *
     * @param scriptId 脚本 ID
     * @param hosts    目标主机 IP
     */
    @ActionAuditRecord(
            actionId = "execute_script",
            builder = ExecuteJobAuditEventBuilder.class,
            content = "Execute script [{{@SCRIPT_NAME}}]({{@SCRIPT_ID}})"
    )
    public void executeScript(long scriptId, List<Host> hosts) {
        ActionAuditContext.current()
                .setInstanceIdList(hosts.stream()
                        .map(host -> String.valueOf(host.getHostId())).collect(Collectors.toList()))
                .setInstanceNameList(hosts.stream().map(Host::getIp).collect(Collectors.toList()))
                .addAttribute("@SCRIPT_NAME", "script_" + scriptId)
                .addAttribute("@SCRIPT_ID", String.valueOf(scriptId));
        // action code here
    }

    /**
     * 自定义的作业执行审计事件生成
     */
    public static class ExecuteJobAuditEventBuilder extends DefaultAuditEventBuilder {
        private final ActionAuditContext actionAuditContext;

        public ExecuteJobAuditEventBuilder() {
            this.actionAuditContext = ActionAuditContext.current();
        }

        @Override
        public List<AuditEvent> build() {
            AuditEvent auditEvent = buildBasicAuditEvent();

            // 由于执行脚本的资源实例是主机，而不是脚本。一次操作可能有上万个主机，为每个主机实例生成一个审计事件会产生大量的审计事件。
            // 所以，这里把主机 ID 用逗号拼接，上报给审计中心，审计中心检索资源实例的时候会特殊处理。
            auditEvent.setResourceTypeId("host");
            // 执行脚本虽然包含多个主机资源实例，但是要求多个资源实例都放到一个实例中（id1,id2,id2..idN）
            auditEvent.setInstanceId(AuditInstanceUtils.extractInstanceIds(actionAuditContext.getInstanceIdList(),
                    instance -> instance));
            auditEvent.setInstanceName(AuditInstanceUtils.extractInstanceIds(actionAuditContext.getInstanceNameList(),
                    instance -> instance));

            // 事件描述
            auditEvent.setContent(resolveAttributes(actionAuditContext.getContent(), actionAuditContext.getAttributes()));

            return Collections.singletonList(auditEvent);
        }
    }
}
```



## 示例代码

**SDK 的使用案例以及测试用例**

[编码方式实现操作审计](../bk-audit-java-sdk/src/test/java/com/tencent/bk/audit/example)

[与 SpringBoot 继承，注解方式声明操作审计](../spring-boot-bk-audit-starter/src/test/java/com/tencent/bk/audit/controller)

[蓝鲸作业平台对接审计中心案例](https://github.com/TencentBlueKing/bk-job/tree/master/src/backend)

## 最佳实践
1. 对于传统的 MVC 三层架构：
   - 查询资源操作，最好在 Controller 层的声明审计操作。因为如果在 Service 层，查询操作会被其他类型的操作依赖（比如修改资源之前可能会先查询资源），就会产生多余的审计事件。
   - 对于增删改操作，建议在 Service 层声明审计操作。因为大部分增删改操作都需要被审计，即使是被依赖调用的情况下