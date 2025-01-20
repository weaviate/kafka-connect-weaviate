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
import org.apache.kafka.connect.sink.SinkRecord;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class FieldIdStrategy implements IDStrategy {
    private String fieldName;

    public FieldIdStrategy() {
    }

    @Override
    public void configure(WeaviateSinkConfig config) {
        fieldName = config.getDocumentIdFieldName();
    }

    @Override
    public String getDocumentId(SinkRecord record, Map<String, Object> valueProperties) {
        try {
            String id = String.valueOf(valueProperties.get(fieldName));
            valueProperties.remove(fieldName);
            return UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8)).toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get document id from message", e);
        }
    }
}
