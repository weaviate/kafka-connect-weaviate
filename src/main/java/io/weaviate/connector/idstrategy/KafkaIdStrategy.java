package io.weaviate.connector.idstrategy;

import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Map;

public class KafkaIdStrategy implements IDStrategy {
    public KafkaIdStrategy() {
    }

    @Override
    public String getDocumentId(SinkRecord record, Map<String, Object> valueProperties) {
        if (record.key() instanceof String) {
            return record.key().toString();
        }

        return String.valueOf(record.key());
    }
}
