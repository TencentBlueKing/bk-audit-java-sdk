package com.tencent.bk.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.DataBindingMethodResolver;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SpEL 注入安全测试。
 * <p>
 * 对应安全扫描报告：
 * - Deserialization (P0): 反序列化/SpEL 注入
 * - Command Injection (P1): SpEL 注入/任意方法调用
 * - Template Injection (P0): AOP 审计注解参数 SpEL 表达式注入
 * <p>
 * 本测试用例演示：
 * 1. 使用 StandardEvaluationContext（修复前）时，恶意 SpEL 可以执行 RCE
 * 2. 使用 SimpleEvaluationContext（修复后）时，恶意 SpEL 会被安全拦截，但允许对象方法调用
 */
public class SpelInjectionSecurityTest {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 模拟业务 DTO，用于测试方法调用场景
     */
    public static class MockResource {
        private final Long id;
        private final String name;
        private final List<String> tags;

        public MockResource(Long id, String name, List<String> tags) {
            this.id = id;
            this.name = name;
            this.tags = tags;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<String> getTags() {
            return tags;
        }

        /** 非 get 开头的业务方法：将名称转为大写 */
        public String toUpperName() {
            return name != null ? name.toUpperCase() : null;
        }

        /** 非 get 开头的业务方法：拼接展示文本 */
        public String formatDisplay(String prefix) {
            return prefix + "[" + name + "](" + id + ")";
        }

        /** 非 get 开头的业务方法：判断是否包含某个 tag */
        public boolean containsTag(String tag) {
            return tags != null && tags.contains(tag);
        }

        /** 非 get 开头的业务方法：返回 tag 数量 */
        public int countTags() {
            return tags != null ? tags.size() : 0;
        }
    }

    // =====================================================
    // 第一组：使用 StandardEvaluationContext（修复前的危险行为）
    // 证明 RCE 漏洞确实存在
    // =====================================================

    @Test
    @DisplayName("[修复前-漏洞复现] StandardEvaluationContext 允许通过 T() 调用 Runtime.exec 实现 RCE")
    void testStandardContext_RCE_via_Runtime() {
        // 模拟攻击者注入的恶意 SpEL 表达式：通过 T() 引用 Runtime 类并调用 exec
        String maliciousSpel = "T(java.lang.Runtime).getRuntime().exec('calc')";

        StandardEvaluationContext unsafeContext = new StandardEvaluationContext();
        unsafeContext.setVariable("param", "normalValue");

        // 使用 StandardEvaluationContext 时，恶意表达式可以成功执行（返回 Process 对象）
        Object result = parser.parseExpression(maliciousSpel).getValue(unsafeContext);
        assertNotNull(result, "StandardEvaluationContext 允许 T(Runtime).exec() 执行，返回了 Process 对象");
        assertTrue(result instanceof Process, "返回值是 Process 实例，说明 RCE 成功");

        // 清理：销毁启动的进程
        ((Process) result).destroy();
    }

    @Test
    @DisplayName("[修复前-漏洞复现] StandardEvaluationContext 允许读取系统环境变量")
    void testStandardContext_ReadEnvVariable() {
        // 攻击者可以读取敏感环境变量
        String maliciousSpel = "T(java.lang.System).getenv('PATH')";

        StandardEvaluationContext unsafeContext = new StandardEvaluationContext();

        Object result = parser.parseExpression(maliciousSpel).getValue(unsafeContext);
        assertNotNull(result, "StandardEvaluationContext 允许读取系统环境变量");
        assertTrue(result.toString().length() > 0, "成功读取到 PATH 环境变量内容");
    }

    @Test
    @DisplayName("[修复前-漏洞复现] StandardEvaluationContext 允许通过 new 创建对象并执行命令")
    void testStandardContext_RCE_via_ProcessBuilder() {
        // 使用 ProcessBuilder 构造命令执行
        String maliciousSpel = "new java.lang.ProcessBuilder({'cmd', '/c', 'echo', 'hacked'}).start()";

        StandardEvaluationContext unsafeContext = new StandardEvaluationContext();

        Object result = parser.parseExpression(maliciousSpel).getValue(unsafeContext);
        assertNotNull(result, "StandardEvaluationContext 允许 new ProcessBuilder().start() 执行");
        assertTrue(result instanceof Process, "返回值是 Process 实例，说明通过构造器执行命令成功");

        // 清理
        ((Process) result).destroy();
    }

    // =====================================================
    // 第二组：使用 SimpleEvaluationContext（修复后的安全行为）
    // 证明修复有效，恶意 SpEL 全部被拒绝
    // =====================================================

    /**
     * 构建与 ActionAuditAspect 修复后一致的 SimpleEvaluationContext (forReadOnlyDataBinding + DataBindingMethodResolver)。
     * 允许变量引用、属性读和实例方法调用，但禁用 T()、new、静态方法和反射链。
     */
    private SimpleEvaluationContext buildSafeContext() {
        SimpleEvaluationContext context = SimpleEvaluationContext
                .forReadOnlyDataBinding()
                .withMethodResolvers(DataBindingMethodResolver.forInstanceMethodInvocation())
                .build();
        context.setVariable("param", "normalValue");
        context.setVariable("$", "returnValue");
        context.setVariable("resource", new MockResource(
                1001L, "test_template",
                Arrays.asList("production", "critical", "v2")));
        context.setVariable("resources", Arrays.asList(
                new MockResource(1L, "res_a", Arrays.asList("tagA")),
                new MockResource(2L, "res_b", Arrays.asList("tagB")),
                new MockResource(3L, "res_c", Arrays.asList("tagA", "tagC"))));
        return context;
    }

    @Test
    @DisplayName("[修复后-安全验证] SimpleEvaluationContext 拒绝 T() 类型引用，阻止 RCE")
    void testSimpleContext_BlocksTypeReference() {
        String maliciousSpel = "T(java.lang.Runtime).getRuntime().exec('calc')";
        EvaluationContext safeContext = buildSafeContext();

        // SimpleEvaluationContext 不支持 T() 类型引用，会抛出 SpelEvaluationException
        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression(maliciousSpel).getValue(safeContext);
        }, "SimpleEvaluationContext 应拒绝 T() 类型引用");
    }

    @Test
    @DisplayName("[修复后-安全验证] SimpleEvaluationContext 拒绝读取系统环境变量")
    void testSimpleContext_BlocksSystemEnvAccess() {
        String maliciousSpel = "T(java.lang.System).getenv('PATH')";
        EvaluationContext safeContext = buildSafeContext();

        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression(maliciousSpel).getValue(safeContext);
        }, "SimpleEvaluationContext 应拒绝 T(System).getenv() 调用");
    }

    @Test
    @DisplayName("[修复后-安全验证] SimpleEvaluationContext 拒绝 new 构造器调用")
    void testSimpleContext_BlocksConstructor() {
        String maliciousSpel = "new java.lang.ProcessBuilder({'cmd', '/c', 'echo', 'hacked'}).start()";
        EvaluationContext safeContext = buildSafeContext();

        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression(maliciousSpel).getValue(safeContext);
        }, "SimpleEvaluationContext 应拒绝 new 构造器调用");
    }

    @Test
    @DisplayName("[修复后-安全验证] SimpleEvaluationContext 拒绝 getClass() 反射链调用")
    void testSimpleContext_BlocksReflection() {
        // 通过 getClass().forName() 反射获取 Runtime 的攻击路径
        String maliciousSpel = "#param.getClass().forName('java.lang.Runtime')" +
                ".getMethod('exec', T(String)).invoke(" +
                "#param.getClass().forName('java.lang.Runtime').getMethod('getRuntime').invoke(null), 'calc')";
        EvaluationContext safeContext = buildSafeContext();

        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression(maliciousSpel).getValue(safeContext);
        }, "SimpleEvaluationContext 应拒绝反射链攻击");
    }

    // =====================================================
    // 第三组：验证正常业务 SpEL 表达式在修复后仍然正常工作
    // =====================================================

    @Test
    @DisplayName("[修复后-功能验证] SimpleEvaluationContext 允许正常的变量引用 #param")
    void testSimpleContext_AllowsVariableReference() {
        String normalSpel = "#param";
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression(normalSpel).getValue(safeContext);
        assertEquals("normalValue", result, "正常的变量引用 #param 应正常工作");
    }

    @Test
    @DisplayName("[修复后-功能验证] SimpleEvaluationContext 允许正常的返回值变量引用 #$")
    void testSimpleContext_AllowsReturnValueReference() {
        String normalSpel = "#$";
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression(normalSpel).getValue(safeContext);
        assertEquals("returnValue", result, "返回值变量引用 #$ 应正常工作");
    }

    // =====================================================
    // 第四组：forReadOnlyDataBinding 场景下的方法调用验证
    // 验证允许对象方法调用（get/非get），同时高危操作仍被阻止
    // =====================================================

    // --- 4.1 getter 方法调用 ---

    @Test
    @DisplayName("[方法调用-getter] 允许调用 getId()")
    void testSimpleContext_AllowsGetId() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.getId()").getValue(safeContext);
        assertEquals(1001L, result);
    }

    @Test
    @DisplayName("[方法调用-getter] 允许调用 getName()")
    void testSimpleContext_AllowsGetName() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.getName()").getValue(safeContext);
        assertEquals("test_template", result);
    }

    @Test
    @DisplayName("[方法调用-getter] 允许调用 getTags() 并获取集合")
    void testSimpleContext_AllowsGetTags() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.getTags()").getValue(safeContext);
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(3, ((List<?>) result).size());
    }

    @Test
    @DisplayName("[方法调用-getter] 允许通过属性语法访问（等价于 getter）")
    void testSimpleContext_AllowsPropertyAccess() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        // 属性语法 #resource.name 等价于 #resource.getName()
        Object result = parser.parseExpression("#resource.name").getValue(safeContext);
        assertEquals("test_template", result);
    }

    // --- 4.2 非 get 开头的业务方法调用 ---

    @Test
    @DisplayName("[方法调用-非getter] 允许调用 toUpperName() —— 无参非get方法")
    void testSimpleContext_AllowsToUpperName() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.toUpperName()").getValue(safeContext);
        assertEquals("TEST_TEMPLATE", result);
    }

    @Test
    @DisplayName("[方法调用-非getter] 允许调用 formatDisplay(prefix) —— 带参非get方法")
    void testSimpleContext_AllowsFormatDisplay() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.formatDisplay('View ')").getValue(safeContext);
        assertEquals("View [test_template](1001)", result);
    }

    @Test
    @DisplayName("[方法调用-非getter] 允许调用 containsTag(tag) —— 返回boolean的非get方法")
    void testSimpleContext_AllowsContainsTag() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.containsTag('production')").getValue(safeContext);
        assertEquals(true, result);

        Object result2 = parser.parseExpression("#resource.containsTag('nonexistent')").getValue(safeContext);
        assertEquals(false, result2);
    }

    @Test
    @DisplayName("[方法调用-非getter] 允许调用 countTags() —— 返回int的非get方法")
    void testSimpleContext_AllowsCountTags() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.countTags()").getValue(safeContext);
        assertEquals(3, result);
    }

    // --- 4.3 集合上的方法调用与投影 ---

    @Test
    @DisplayName("[方法调用-集合] 允许调用 List.size()")
    void testSimpleContext_AllowsListSize() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resources.size()").getValue(safeContext);
        assertEquals(3, result);
    }

    @Test
    @DisplayName("[方法调用-集合] 允许对集合元素调用非get方法（链式：get+toUpperName）")
    void testSimpleContext_AllowsChainedMethodOnListElement() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        // 访问第一个元素并调用非get方法
        Object result = parser.parseExpression("#resources[0].toUpperName()").getValue(safeContext);
        assertEquals("RES_A", result);
    }

    @Test
    @DisplayName("[方法调用-集合] 允许 String.toString() 等 Object 基础方法")
    void testSimpleContext_AllowsToString() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        Object result = parser.parseExpression("#resource.getId().toString()").getValue(safeContext);
        assertEquals("1001", result);
    }

    // --- 4.4 forReadOnlyDataBinding 下高危操作仍被阻止 ---

    @Test
    @DisplayName("[方法调用-安全] forReadOnlyDataBinding 仍然拒绝 T() 类型引用")
    void testReadWriteContext_StillBlocksTypeReference() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression("T(java.lang.Runtime).getRuntime().exec('calc')").getValue(safeContext);
        });
    }

    @Test
    @DisplayName("[方法调用-安全] forReadOnlyDataBinding 仍然拒绝 new 构造器")
    void testReadWriteContext_StillBlocksConstructor() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression("new java.lang.ProcessBuilder({'cmd'}).start()").getValue(safeContext);
        });
    }

    @Test
    @DisplayName("[方法调用-安全] forReadOnlyDataBinding 仍然拒绝 getClass() 反射链")
    void testReadWriteContext_StillBlocksGetClassReflection() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        // 尝试通过业务对象的 getClass() 进入反射链
        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression("#resource.getClass().forName('java.lang.Runtime')").getValue(safeContext);
        });
    }

    @Test
    @DisplayName("[方法调用-安全] forReadOnlyDataBinding 仍然拒绝通过 String.getClass() 反射")
    void testReadWriteContext_StillBlocksStringGetClassReflection() {
        SimpleEvaluationContext safeContext = buildSafeContext();

        assertThrows(SpelEvaluationException.class, () -> {
            parser.parseExpression("#param.getClass().forName('java.lang.Runtime')").getValue(safeContext);
        });
    }
}
