package io.weaviate.connector.idstrategy;

import io.weaviate.connector.WeaviateSinkConfig;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Map;

public interface IDStrategy {
    public String getDocumentId(SinkRecord record, Map<String, Object> valueProperties);

    public default void configure(WeaviateSinkConfig config) {
    }
}
