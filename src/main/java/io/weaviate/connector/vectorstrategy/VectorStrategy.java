package io.weaviate.connector.vectorstrategy;

import io.weaviate.connector.WeaviateSinkConfig;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Map;

public interface VectorStrategy {
    public void configure(WeaviateSinkConfig config);

    public Float[] getDocumentVector(SinkRecord record, Map<String, Object> valueProperties);
}
