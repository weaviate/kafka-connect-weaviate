/*
 * Copyright © 2025 Damien Gasparina (damien@gasparina.cloud)
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
package io.weaviate.connector.idstrategy;

import io.weaviate.connector.WeaviateSinkConfig;
import io.weaviate.connector.converter.DataConverter;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.apache.kafka.connect.sink.SinkRecord;
import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaIdStrategyTest {

    @Test
    void getFieldId() throws IOException {
        DataConverter converter = new DataConverter();
        JsonConverter jsonConverter = new JsonConverter();
        jsonConverter.configure(new HashMap<>() {{
            put(JsonConverterConfig.TYPE_CONFIG, "value");
            put(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, false);
        }});

        String jsonContent = IOUtil.toString(converter.getClass().getResourceAsStream("/jsonData.json"));
        SchemaAndValue schemaAndValue = jsonConverter.toConnectData("test", jsonContent.getBytes(Charset.defaultCharset()));

        WeaviateSinkConfig config = new WeaviateSinkConfig(WeaviateSinkConfig.CONFIG_DEF, new HashMap<>() {{
            put(WeaviateSinkConfig.DOCUMENT_ID_FIELD_CONFIG, "text");
        }});

        Map<String, Object> properties = converter.convertToWeaviateProperties(schemaAndValue.schema(), schemaAndValue.value());

        KafkaIdStrategy fieldIdStrategy = new KafkaIdStrategy();
        fieldIdStrategy.configure(config);

        String documentId = fieldIdStrategy.getDocumentId(new SinkRecord("", 0, Schema.STRING_SCHEMA, "hello", null, null, 0L), properties);
        assertEquals("5d41402a-bc4b-3a76-b971-9d911017c592", documentId);
    }
}