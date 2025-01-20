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
package io.weaviate.connector.converter;

import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataConverter {

    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToWeaviateProperties(Schema schema, Object value) {
        Object object = convertToJava(schema, value);
        if (!(object instanceof Map)) {
            throw new DataException(String.format("Cannot convert " + schema.name() + " to Java object, %s is not a Map", value));
        }
        return (Map<String, Object>) object;
    }

    private Object convertToJava(Schema schema, Object value) {
        if (value == null) {
            if (schema == null)
                return null;
            if (schema.defaultValue() != null)
                return convertToJava(schema, schema.defaultValue());
            if (schema.isOptional())
                return null;
            throw new DataException("Conversion error: null value for field that is required and has no default value");
        }

        try {
            final Schema.Type schemaType;
            if (schema == null) {
                schemaType = ConnectSchema.schemaType(value.getClass());
                if (schemaType == null)
                    throw new DataException("Java class " + value.getClass() + " does not have corresponding schema type.");
            } else {
                schemaType = schema.type();
            }
            switch (schemaType) {
                case INT8:
                    ByteBuffer bb = ByteBuffer.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, (byte) value});
                    return bb.getLong();
                case INT16:
                    return ((Short) value).longValue();
                case INT32:
                    return ((Integer) value).longValue();
                case INT64:
                    return (Long) value;
                case FLOAT32:
                    return ((Float) value).doubleValue();
                case FLOAT64:
                    return (Double) value;
                case BOOLEAN:
                    return (Boolean) value;
                case STRING:
                    CharSequence charSeq = (CharSequence) value;
                    return charSeq.toString();
                case BYTES:
                    if (value instanceof byte[])
                        return ((byte[]) value);
                    else if (value instanceof ByteBuffer)
                        return ((ByteBuffer) value).array();
                    else
                        throw new DataException("Invalid type for bytes type: " + value.getClass());
                case ARRAY: {
                    Collection<?> collection = (Collection<?>) value;
                    ArrayList<Object> list = new ArrayList<>(collection.size());
                    for (Object elem : collection) {
                        Schema valueSchema = schema == null ? null : schema.valueSchema();
                        Object fieldValue = convertToJava(valueSchema, elem);
                        list.add(fieldValue);
                    }
                    return list;
                }
                case MAP: {
                    Map<?, ?> map = (Map<?, ?>) value;
                    HashMap<String, Object> object = new HashMap<>(map.size());
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        Schema keySchema = schema == null ? null : schema.keySchema();
                        Schema valueSchema = schema == null ? null : schema.valueSchema();
                        Object mapKey = convertToJava(keySchema, entry.getKey());
                        Object mapValue = convertToJava(valueSchema, entry.getValue());

                        object.put(String.valueOf(mapKey), mapValue);
                    }
                    return object;
                }
                case STRUCT: {
                    Struct struct = (Struct) value;
                    if (!struct.schema().equals(schema))
                        throw new DataException("Mismatching schema.");

                    HashMap<String, Object> object = new HashMap<>();
                    for (Field field : schema.fields()) {
                        object.put(field.name(), convertToJava(field.schema(), struct.get(field)));
                    }
                    return object;
                }
            }

            throw new DataException("Couldn't convert " + value + " to Java object.");
        } catch (ClassCastException e) {
            String schemaTypeStr = (schema != null) ? schema.type().toString() : "unknown schema";
            throw new DataException("Invalid type for " + schemaTypeStr + ": " + value.getClass());
        }
    }

}
