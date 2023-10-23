package com.tencent.bk.audit.utils.json;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.tencent.bk.audit.exception.AuditException;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonSchema 工具
 */
@Slf4j
public class JsonSchemaUtils {
    private static final JsonSchemaGenerator JSON_SCHEMA_GENERATOR;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        SchemaFactoryWrapper wrapper = new SchemaFactoryWrapper();
        JSON_SCHEMA_GENERATOR = new JsonSchemaGenerator(objectMapper, wrapper);
    }

    /**
     * 生成 POJO 的JsonSchema
     *
     * @param pojoClass POJO
     * @return JsonSchema
     */
    public static JsonSchema generateJsonSchema(Class<?> pojoClass) {
        try {
            return JSON_SCHEMA_GENERATOR.generateSchema(pojoClass);
        } catch (JsonMappingException e) {
            throw new AuditException("Generate json schema caught exception", e);
        }
    }
}
