package com.tencent.bk.audit.utils.json;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonSchemaUtils {
    private static final JsonSchemaGenerator JSON_SCHEMA_GENERATOR = new JsonSchemaGenerator((ObjectMapper) null);

    public static JsonSchema generateJsonSchema(Class<?> jsonObject) {
        try {
            return JSON_SCHEMA_GENERATOR.generateSchema(jsonObject);
        } catch (JsonMappingException e) {
            log.error("Generate json schema caught exception", e);
            return null;
        }
    }
}
