# Kafka Connect Weaviate Connector

kafka-connect-weaviate is a [Kafka Connector](http://kafka.apache.org/documentation.html#connect) sink connector
for loading data to any Weaviate cluster.

This is currently a Work In Progress connector, use it at your own risk.

## Configuration

| Parameter                   | Description                                                                                               | Default value                                         |
|-----------------------------|-----------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| weaviate.connection.url     | "Weaviate connection URL, should be following the format `<scheme>://<host>:<port>`                       | http://localhost:8080                                 |
| weaviate.grpc.url           | Weaviate GRPC connection URL                                                                              | localhost:50051                                       |
| weaviate.grpc.secured       | Weaviate GRPC TLS secured connection, set to True to enable TLS encryption                                | false                                                 |
| weaviate.auth.scheme        | Authentication mechanism to use to connect to Weaviate, could be NONE, API_KEY or OIDC_CLIENT_CREDENTIALS | NONE                                                  |
| weaviate.api.key            | User API Key if API Key authentication mechanism is used                                                  |                                                       |
| weaviate.oidc.client.secret | User OIDC client secret if OIDC authentication mechanism is used                                          |                                                       |
| weaviate.oidc.scopes        | OIDC client scope if OIDC authentication mechanism is used                                                |                                                       |
| collection.mapping          | Mapping between Kafka topic and Weaviate collection                                                       | ${topic}                                              |
| document.id.strategy        | Java class returning the document ID for each record                                                      | io.weaviate.connector.idstrategy.NoIdStrategy         |
| document.id.field.name      | Field name containing the ID in Kafka                                                                     | id                                                    |
| vector.strategy             | Java class returning the document embedding for each record                                               | io.weaviate.connector.vectorstrategy.NoVectorStrategy |
| vector.field.name           | Field name containing the embedding (used only for FieldVectorStrategy)                                   |                                                       |


## Example of configuration

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
