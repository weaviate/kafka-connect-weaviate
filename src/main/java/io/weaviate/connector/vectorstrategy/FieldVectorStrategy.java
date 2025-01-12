package io.weaviate.connector.vectorstrategy;

import io.weaviate.connector.WeaviateSinkConfig;
import org.apache.kafka.connect.sink.SinkRecord;

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
        Float[] vector = valueProperties.get(fieldName) == null ? null : (Float[]) valueProperties.get(fieldName);
        valueProperties.remove(fieldName);
        return vector;
    }
}
