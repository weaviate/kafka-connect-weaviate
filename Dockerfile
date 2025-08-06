FROM confluentinc/cp-kafka-connect-base:7.8.0

RUN confluent-hub install --no-prompt Weaviate/kafka-connect-weaviate:0.1.2
