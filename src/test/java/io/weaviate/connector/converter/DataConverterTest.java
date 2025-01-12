package io.weaviate.connector.converter;

import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataConverterTest {

    @Test
    void structNoSchemaConvertToWeaviateProperties() throws IOException {
        DataConverter converter = new DataConverter();
        JsonConverter jsonConverter = new JsonConverter();
        jsonConverter.configure(new HashMap<>() {{
            put(JsonConverterConfig.TYPE_CONFIG, "value");
            put(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, false);
        }});

        String jsonContent = IOUtil.toString(converter.getClass().getResourceAsStream("/jsonData.json"));
        SchemaAndValue schemaAndValue = jsonConverter.toConnectData("test", jsonContent.getBytes(Charset.defaultCharset()));

        Map<String, Object> properties = converter.convertToWeaviateProperties(schemaAndValue.schema(), schemaAndValue.value());

        assertEquals("hello world", properties.get("text"));
        assertEquals(123L, properties.get("int"));
        assertEquals(1.23, properties.get("float"));
        assertEquals(true, properties.get("boolean"));
    }


    @Test
    void structWithSchemaConvertToWeaviateProperties() throws IOException {
        DataConverter converter = new DataConverter();
        JsonConverter jsonConverter = new JsonConverter();
        jsonConverter.configure(new HashMap<>() {{
            put(JsonConverterConfig.TYPE_CONFIG, "value");
            put(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, true);
        }});

        String jsonContent = IOUtil.toString(converter.getClass().getResourceAsStream("/jsonDataSchema.json"));
        SchemaAndValue schemaAndValue = jsonConverter.toConnectData("test", jsonContent.getBytes(Charset.defaultCharset()));

        Map<String, Object> properties = converter.convertToWeaviateProperties(schemaAndValue.schema(), schemaAndValue.value());

        assertEquals("hello world", properties.get("text"));
        assertEquals(123L, properties.get("int"));
        assertEquals(1.23, properties.get("float"));
        assertEquals(true, properties.get("boolean"));
    }
}