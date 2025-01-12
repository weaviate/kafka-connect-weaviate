package io.weaviate.connector.vectorstrategy;

import io.weaviate.connector.WeaviateSinkConfig;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Map;

public class NoVectorStrategy implements VectorStrategy {
    @Override
    public void configure(WeaviateSinkConfig config) {

    }

    @Override
    public Float[] getDocumentVector(SinkRecord record, Map<String, Object> valueProperties) {
        return null;
    }
}
