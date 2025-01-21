# Kafka Connect Weaviate Connector

kafka-connect-weaviate is a [Kafka Sink Connector](http://kafka.apache.org/documentation.html#connect)
for loading data to any Weaviate cluster.

> [!IMPORTANT]
> This is currently a Work In Progress connector, use it at your own risk.

## Features

* Support streaming INSERT and UPSERT from a list of topics to multiple Weaviate collections
* Support all structured format in Kafka (Avro, JSON, Protobuf)
* Support Bring Your Own Vector if the embedding is generated outside of Weaviate
* Support multiple tasks for higher throughput
* Support at-least-once semantic
* Tested with both Weaviate Cloud and self-managed Weaviate instance

## Upsert operation

Upsert is done by specifying the relevant UUID for each document. 
The UUID can be configured by specifying the `document.id.strategy` configuration.
By default, the `io.weaviate.connector.idstrategy.NoIdStrategy` is used, 

The availables `document.id.strategy` are:

- `io.weaviate.connector.idstrategy.NoIdStrategy` - **default** - generates a new UUID for each record, thus always inserting a new record in Weaviate for each Kafka record.
- `io.weaviate.connector.idstrategy.KafkaIdStrategy` - generates a UUID based on the key of the Kafka message
- `io.weaviate.connector.idstrategy.FieldIdStrategy` - generates a UUID based on a field of the Kafka record payload, the field name can be specified by configuring `document.id.field.name` 

## Bring Your Own Vectors

By default, the embedding of each record will be compute by Weaviate by levearing the collection vectorizers.
If the embedding is already available or computed outside of Weaviate, you can configure the `vector.strategy` to change this behavior.

The availables `vector.strategy` are:

- `io.weaviate.connector.vectorstrategy.NoVectorStrategy` - **default** - will rely on collection vectorizier to generate embedding in Weaviate
- `io.weaviate.connector.vectorstrategy.FieldVectorStrategy` - Embedding available in a field of the Kafka record, the field name can be specified by configuring `vector.field.name`

## Example of configuration

The definition of all parameters is available on [CONFIGURATION.md](./CONFIGURATION.md).

**Simple configuration inserting a new object for each record in Kafka**
```json
{
  "connector.class": "io.weaviate.connector.WeaviateSinkConnector",
  "topics": "test",
  "weaviate.connection.url": "http://weaviate:8080",
  "weaviate.grpc.url": "weaviate:50051",
  "collection.mapping": "weaviate_${topic}",
  "value.converter": "org.apache.kafka.connect.json.JsonConverter",
  "value.converter.schemas.enable": false
}
```


**Upserting object for each record in Kafka based on an ID field**
```json
{
  "connector.class": "io.weaviate.connector.WeaviateSinkConnector",
  "topics": "test",
  "weaviate.connection.url": "http://weaviate:8080",
  "weaviate.grpc.url": "weaviate:50051",
  "collection.mapping": "weaviate_${topic}",
  "document.id.strategy": "io.weaviate.connector.idstrategy.FieldIdStrategy",
  "document.id.field.name": "id",
  "value.converter": "org.apache.kafka.connect.json.JsonConverter",
  "value.converter.schemas.enable": false
}
```

**Connecting to Weaviate Cloud**
```json
{
  "connector.class": "io.weaviate.connector.WeaviateSinkConnector",
  "topics": "test",
  "weaviate.connection.url": "$WEAVIATE_REST_URL",
  "weaviate.grpc.url": "$WEAVIATE_GRPC_URL",
  "weaviate.grpc.secured": true,
  "weaviate.auth.scheme": "API_KEY",
  "weaviate.api.key": "$WEAVIATE_API_KEY",
  "collection.mapping": "Weaviate_test",
  "document.id.strategy": "io.weaviate.connector.idstrategy.FieldIdStrategy",
  "document.id.field.name": "id",
  "value.converter": "org.apache.kafka.connect.json.JsonConverter",
  "value.converter.schemas.enable": false
}
```


## How to build

```bash
mvn clean package kafka-connect:kafka-connect -Drevision=0.1.0
# Package should be generated on target/components/packages/
```

## Quickstart

```bash
docker-compose up -d
sleep 5
python examples/create_collection.py weaviate_test
curl -i -X PUT localhost:8083/connectors/weaviate/config -H 'Content-Type:application/json' --data @examples/weaviate-upsert-sink.json
echo '{"string": "Hello World", "number": 1.23, "id": "test" }' |  docker-compose exec -T kafka kafka-console-producer --bootstrap-server localhost:9092 --topic test
```
