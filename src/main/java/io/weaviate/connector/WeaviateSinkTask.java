package io.weaviate.connector;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateAuthClient;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.v1.auth.exception.AuthException;
import io.weaviate.client.v1.batch.api.ObjectsBatcher;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.connector.converter.DataConverter;
import io.weaviate.connector.idstrategy.IDStrategy;
import io.weaviate.connector.vectorstrategy.VectorStrategy;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;

public class WeaviateSinkTask extends SinkTask {
    WeaviateClient client;
    private String collectionMappingRule;
    private IDStrategy documentIdStrategy;
    private VectorStrategy vectorStrategy;
    private ObjectsBatcher objectsBatcher;

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        WeaviateSinkConfig config = new WeaviateSinkConfig(WeaviateSinkConfig.CONFIG_DEF, map);
        collectionMappingRule = config.getCollectionMapping();
        buildWeaviateClient(config);
        try {
            documentIdStrategy = (IDStrategy) config.getDocumentIdStrategy().getDeclaredConstructor().newInstance();
            documentIdStrategy.configure(config);
        } catch (Exception e) {
            throw new RuntimeException("Can not instantiate DocumentIDStrategy class", e);
        }

        try {
            vectorStrategy = (VectorStrategy) config.getVectorStrategy().getDeclaredConstructor().newInstance();
            vectorStrategy.configure(config);
        } catch (Exception e) {
            throw new RuntimeException("Can not instantiate VectorStrategy class", e);
        }
    }

    private void buildWeaviateClient(WeaviateSinkConfig config) {
        Config weaviateConfig = getConfig(config);
        if (config.getAuthMechanism() == WeaviateSinkConfig.AuthMechanism.NONE) {
            client = new WeaviateClient(weaviateConfig);
        } else if (config.getAuthMechanism() == WeaviateSinkConfig.AuthMechanism.API_KEY) {
            try {
                client = WeaviateAuthClient.apiKey(weaviateConfig, config.getApiKey());
            } catch (AuthException e) {
                throw new RuntimeException(e);
            }
        } else if (config.getAuthMechanism() == WeaviateSinkConfig.AuthMechanism.OIDC_CLIENT_CREDENTIALS) {
            try {
                client = WeaviateAuthClient.clientCredentials(weaviateConfig, config.getOidcClientSecret(), config.getOidcScopes());
            } catch (AuthException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Unknown authentication mechanism");
        }
    }

    private static Config getConfig(WeaviateSinkConfig config) {
        String scheme = config.getConnectionUrl().split("://")[0];
        String hostAndPort = config.getConnectionUrl().split("://")[1];
        Config weaviateConfig = new Config(scheme, hostAndPort);
        //if (config.getGrpcUrl() != null && !config.getGrpcUrl().isEmpty()) {
        //    weaviateConfig.setGRPCHost(config.getGrpcUrl());
        //    weaviateConfig.setGRPCSecured(config.getGrpcSecured());
        //}
        return weaviateConfig;
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        if (objectsBatcher == null) {
            objectsBatcher = client.batch().objectsAutoBatcher();
        }
        DataConverter dataConverter = new DataConverter();
        for (SinkRecord record : collection) {
            Map<String, Object> properties = dataConverter.convertToWeaviateProperties(record.valueSchema(), record.value());
            objectsBatcher.withObject(WeaviateObject.builder()
                    .className(getCollectionName((record.topic())))
                    .properties(properties)
                    .id(documentIdStrategy.getDocumentId(record, properties))
                    .vector(vectorStrategy.getDocumentVector(record, properties))
                    .build());
        }
        objectsBatcher.flush();
    }

    public String getCollectionName(String topic) {
        return collectionMappingRule.replace("${topic}", topic);
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        super.flush(currentOffsets);
        if (objectsBatcher != null) {
            objectsBatcher.flush();
        }
    }

    @Override
    public void stop() {
        if (objectsBatcher != null) {
            objectsBatcher.close();
        }
    }
}
