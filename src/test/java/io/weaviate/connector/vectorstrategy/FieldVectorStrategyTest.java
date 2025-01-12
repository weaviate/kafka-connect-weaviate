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
package io.weaviate.connector.vectorstrategy;

import io.weaviate.connector.WeaviateSinkConfig;
import io.weaviate.connector.converter.DataConverter;
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

class FieldVectorStrategyTest {

    @Test
    void getFieldVector() throws IOException {
        DataConverter converter = new DataConverter();
        JsonConverter jsonConverter = new JsonConverter();
        jsonConverter.configure(new HashMap<>() {{
            put(JsonConverterConfig.TYPE_CONFIG, "value");
            put(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, false);
        }});

        String jsonContent = IOUtil.toString(converter.getClass().getResourceAsStream("/jsonData.json"));
        SchemaAndValue schemaAndValue = jsonConverter.toConnectData("test", jsonContent.getBytes(Charset.defaultCharset()));

        WeaviateSinkConfig config = new WeaviateSinkConfig(WeaviateSinkConfig.CONFIG_DEF, new HashMap<>() {{
            put(WeaviateSinkConfig.VECTOR_FIELD_CONFIG, "vector");
        }});

        Map<String, Object> properties = converter.convertToWeaviateProperties(schemaAndValue.schema(), schemaAndValue.value());

        FieldVectorStrategy fieldIdStrategy = new FieldVectorStrategy();
        fieldIdStrategy.configure(config);

        Float[] documentId = fieldIdStrategy.getDocumentVector(null, properties);

        assertEquals(1.0f, documentId[0]);
        assertEquals(2.0f, documentId[1]);
    }
}