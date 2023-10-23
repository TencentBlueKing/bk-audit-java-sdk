package com.tencent.bk.audit.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSchemaUtilsTest {

    @Test
    void generateSchema() throws Exception {
        JsonSchema jsonSchema = JsonSchemaUtils.generateJsonSchema(Account.class);

        ObjectMapper mapper = new ObjectMapper();
        String schemaAsString = mapper.writeValueAsString(jsonSchema);
        assertThat(schemaAsString).isEqualTo("{\"type\":\"object\",\"id\":\"urn:jsonschema:com:tencent:bk:audit:utils:json:Account\",\"properties\":{\"id\":{\"type\":\"integer\",\"description\":\"Account ID\"},\"account\":{\"type\":\"string\",\"description\":\"Account\"},\"creator\":{\"type\":\"string\",\"description\":\"Account creator\"},\"os\":{\"type\":\"string\",\"description\":\"Account OS\"},\"alias\":{\"type\":\"string\",\"description\":\"Account alias\"},\"create_time\":{\"type\":\"string\",\"description\":\"Create time\"}}}");
    }

}