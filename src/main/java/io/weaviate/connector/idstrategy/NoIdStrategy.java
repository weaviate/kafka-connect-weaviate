package io.weaviate.connector.idstrategy;

import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Map;

public class NoIdStrategy implements IDStrategy {
    @Override
    public String getDocumentId(SinkRecord record, Map<String, Object> valueProperties) {
        return null;
    }
}
