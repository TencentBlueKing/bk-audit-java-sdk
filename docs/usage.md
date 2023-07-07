# BkAudit Java SDK

以下为 BkAudit Java SDK 使用说明文档，所有代码均有完整注释，可以在源码中查看。

## 类型说明

- 审计事件 AuditEvent

AuditEvent 定义了审计事件的标准字段。详见 [AuditEvent](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/model/AuditEvent.java)

- 操作 Action

Action 实际上为 iam 模型中的 Action 对象，详见 [iam.model.models.Action](https://github.com/TencentBlueKing/iam-python-sdk/blob/master/iam/model/models.py)

- 资源类型 ResourceType

ResourceType 实际上为 iam 模型中的 ResourceType 对象，详见 [iam.model.models.ResourceType](https://github.com/TencentBlueKing/iam-python-sdk/blob/master/iam/model/models.py)

- 审计上下文 AuditContext

审计上下文中定义了一次操作（可能包含多个 Action）执行过程中的审计上下文，详见 [AuditContext](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/context/AuditContext.java)

- 操作审计上下文 ActionAuditContext

操作审计上下文中定义了一个 Action 执行过程中的上下文（一个 AuditContext 可能包含多个 ActionAuditContext ），详见 [ActionAuditContext](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/context/ActionAuditContext.java)

- 审计事件生成 AuditEventBuilder

AuditEventBuilder 用于从审计上下文 AuditContext 中构造 AuditEvent 对象，可以通过扩展 AuditEventBuilder 自定义审计事件的生成。详见 [AuditEventBuilder](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/AuditEventBuilder.java)

- 审计事件输出 EventExporter

EventExporter 用于输出审计事件，如输出到日志文件，标准输出流等，详见 [EventExporter](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/exporter/EventExporter.java)

- 审计上下文属性 AuditAttribute

AuditAttribute 用于定义审计上下文中的属性，构造审计事件(AuditEvent)的时候可能会用到审计属性。标准化的属性名称定义详见 [AuditAttributeNames](../bk-audit-java-sdk/src/main/java/com/tencent/bk/audit/constants/AuditAttributeNames.java)

## 基本使用

Java 程序通用。

### 初始化 AuditClient (单例)

```
EventExporter eventExporter = new LogFileEventExporter();
AuditExceptionResolver auditExceptionResolver = new DefaultAuditExceptionResolver();
AuditClient auditClient = new AuditClient(eventExporter, auditExceptionResolver);
```

### 审计操作
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

    private void action() {
        // 使用 ActionAuditContext 封装 Action 代码，自动封装审计逻辑
        ActionAuditContext.builder("execute_job_plan")
                .setResourceType("job_plan")
                .setInstanceId("1000")
                .setInstanceName("test_audit_execute_job_plan")
                .setContent("Execute job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                .build()
                .wrapActionRunnable(() -> {
                    // action code
                })
                .run();
    }
}
```

## 与 SpringBoot 集成

### 依赖声明
- build.gradle
```
implementation 'com.tencent.bk.sdk:spring-boot-bk-audit-starter:1.0.0'
```
- pom.xml
```
<dependency>
    <groupId>com.tencent.bk.sdk</groupId>
    <artifactId>spring-boot-bk-audit-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置
| 配置项              | 默认值   | 说明                                  |
| :------------------ | :------- | :------------------------------------ |
| audit.enabled       | true     | 是否开启审计功能                      |
| audit.exporter.type | log_file | 审计事件 Exporter，默认为输出日志文件 |


### 基于 Java 注解的审计操作
#### 注解说明
- @AuditEntry 标识审计操作入口
- @AuditRequestBody 标识审计操作 http 请求 Body
- @ActionAuditRecord 标识操作审计记录
- @AuditAttribute 自定义审计上下文属性
- @AuditInstanceRecord 标识操作实例

#### 使用
```java

```

### 特殊

## 使用进阶

### 自定义 EventExporter

除了 SDK 中自定义的 EventExporter，开发者可以通过实现 EventExporter 接口自定义 EventExporter。

```
public class MyEventExporter implements EventExporter {
    @Override
    public void export(AuditEvent event) {
        
    }

    @Override
    public void export(Collection<AuditEvent> events) {

    }
}
```

在定义好了 EventExporter 后，需要在初始化 Client 时指定

```
EventExporter myEventExporter = new MyEventExporter();
AuditExceptionResolver auditExceptionResolver = new DefaultAuditExceptionResolver();
AuditClient auditClient = new AuditClient(myEventExporter, auditExceptionResolver);
```

### 自定义 AuditEventBuilder

SDK 提供了审计事件生成的默认实现 DefaultAuditEventBuilder。开发者可以通过实现 AuditEventBuilder 接口或者继承 DefaultAuditEventBuilder 定制审计事件生成逻辑。
```java

```

在定义好了 AuditEventBuilder 后，可以在 ActionAuditContext 中指定

``` java

```



## 最佳实践
### 