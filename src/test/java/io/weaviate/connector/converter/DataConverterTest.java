/*
 * Copyright © 2025 Weaviate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        assertEquals("[1.0, 2.0]", properties.get("vector").toString());
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