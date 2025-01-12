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
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldVectorStrategy implements VectorStrategy {
    private String fieldName;

    public FieldVectorStrategy() {
    }

    @Override
    public void configure(WeaviateSinkConfig config) {
        fieldName = config.getVectorFieldName();
    }

    @Override
    public Float[] getDocumentVector(SinkRecord record, Map<String, Object> valueProperties) {
        if (valueProperties.get(fieldName) == null) {
            return null;
        }

        Object object = valueProperties.get(fieldName);
        if (object instanceof Float[]) {
            valueProperties.remove(fieldName);
            return (Float[]) object;
        }
        if (object instanceof Iterable) {
            List<Float> floatList = new ArrayList<>();
            for (Object o : (Iterable<?>) object) {
                if (o instanceof Float) {
                    Float f = (Float) o;
                    floatList.add(f);
                } else if (o instanceof Double) {
                    Double d = (Double) o;
                    floatList.add(d.floatValue());
                } else { // trying to cast anyway
                    Float f = (Float) o;
                    floatList.add(f);
                }
            }
            valueProperties.remove(fieldName);
            return floatList.toArray(new Float[0]);
        }
        throw new UnsupportedOperationException("Can't convert " + object + " to Float[]");
    }
}
