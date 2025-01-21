/*
 * Copyright © 2025 Damien Gasparina (damien@gasparina.cloud)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.weaviate.connector;

import io.weaviate.connector.idstrategy.IDStrategy;
import io.weaviate.connector.idstrategy.KafkaIdStrategy;
import io.weaviate.connector.vectorstrategy.VectorStrategy;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.*;

import static io.weaviate.client.v1.async.batch.api.ObjectsBatcher.AutoBatchConfig.BATCH_SIZE;
import static io.weaviate.client.v1.async.batch.api.ObjectsBatcher.BatchRetriesConfig.*;
import static io.weaviate.client.v1.batch.api.ObjectsBatcher.AutoBatchConfig.AWAIT_TERMINATION_MS;
import static io.weaviate.client.v1.batch.api.ObjectsBatcher.AutoBatchConfig.POOL_SIZE;

public final class WeaviateSinkConfig extends AbstractConfig {

    private final String connectionUrl;
    private final AuthMechanism authMechanism;
    private final String apiKey;
    private final String oidcClientSecret;
    private final String collectionMapping;
    private final List<String> oidcScopes;
    private final Boolean grpcSecured;
    private final String grpcUrl;
    private final Class<?> documentIdStrategy;
    private final Class<?> vectorStrategy;
    private final String vectorFieldName;
    private final String documentIdFieldName;
    private final List<String> rawHeaders;
    private final ConsistencyLevel consistencyLevel;
    private final Integer awaitTerminationMs;
    private final Integer maxConnectionRetries;
    private final Integer maxTimeoutRetries;
    private final Integer retryInterval;
    private final Integer batchSize;
    private final Integer poolSize;
    private final Boolean deleteEnabled;

    public enum AuthMechanism {
        NONE,
        API_KEY,
        OIDC_CLIENT_CREDENTIALS,
    }

    public static final String CONNECTION_URL_CONFIG = "weaviate.connection.url";
    private static final String CONNECTION_URL_DOC = "Weaviate connection URL";
    private static final String CONNECTION_URL_DEFAULT = "http://localhost:8080";

    public static final String GRPC_URL_CONFIG = "weaviate.grpc.url";
    private static final String GRPC_URL_DOC = "Weaviate GRPC connection URL";
    private static final String GRPC_URL_DEFAULT = "localhost:50051";

    public static final String GRPC_SECURED_CONFIG = "weaviate.grpc.secured";
    private static final String GRPC_SECURED_DOC = "Weaviate GRPC TLS secured connection";
    private static final Boolean GRPC_SECURED_DEFAULT = false;

    public static final String AUTH_MECHANISM_CONFIG = "weaviate.auth.scheme";
    private static final String AUTH_MECHANISM_DOC = "Authentication mechanism to use to connect to Weaviate";
    private static final String AUTH_MECHANISM_DEFAULT = AuthMechanism.NONE.name();

    public static final String API_KEY_CONFIG = "weaviate.api.key";
    private static final String API_KEY_DOC = "User API key";

    public static final String OIDC_CLIENT_SECRET_CONFIG = "weaviate.oidc.client.secret";
    private static final String OIDC_CLIENT_SECRET_DOC = "User OIDC client secret";

    public static final String OIDC_SCOPES_CONFIG = "weaviate.oidc.scopes";
    private static final String OIDC_SCOPES_DOC = "User OIDC client scopes";
    private static final String OIDC_SCOPES_DEFAULT = "openid";

    public static final String COLLECTION_MAPPING_CONFIG = "collection.mapping";
    private static final String COLLECTION_MAPPING_DOC = "Mapping between Kafka topic and Weaviate collection";
    private static final String COLLECTION_MAPPING_DEFAULT = "${topic}";

    public static final String HEADERS_CONFIG = "weaviate.headers";
    private static final String HEADERS_DOC = "Headers to provide while building Weaviate client (e.g. X-OpenAI-Api-Key)";
    private static final String HEADERS_DEFAULT = "";

    public static final String DOCUMENT_ID_STRATEGY_CONFIG = "document.id.strategy";
    private static final String DOCUMENT_ID_STRATEGY_DOC = "Java class returning the document ID for each record";
    private static final Class<? extends IDStrategy> DOCUMENT_ID_STRATEGY_DEFAULT = io.weaviate.connector.idstrategy.NoIdStrategy.class;

    public static final String DOCUMENT_ID_FIELD_CONFIG = "document.id.field.name";
    private static final String DOCUMENT_ID_FIELD_DOC = "Field name containing the ID in Kafka";
    private static final String DOCUMENT_ID_FIELD_DEFAULT = "id";

    public static final String VECTOR_STRATEGY_CONFIG = "vector.strategy";
    private static final String VECTOR_STRATEGY_DOC = "Java class returning the document embedding for each record";
    private static final Class<? extends VectorStrategy> VECTOR_STRATEGY_DEFAULT = io.weaviate.connector.vectorstrategy.NoVectorStrategy.class;

    public static final String VECTOR_FIELD_CONFIG = "vector.field.name";
    private static final String VECTOR_FIELD_DOC = "Field name containing the embedding";
    private static final String VECTOR_FIELD_DEFAULT = "vector";

    public static final String CONSISTENCY_LEVEL_CONFIG = "consistency.level";
    private static final String CONSISTENCY_LEVEL_DOC = "Consistency level to use while inserting objects";
    private static final String CONSISTENCY_LEVEL_DEFAULT = ConsistencyLevel.QUORUM.name();

    public static final String MAX_TIMEOUT_RETRIES_CONFIG = "max.timeout.retries";
    private static final String MAX_TIMEOUT_RETRIES_DOC = "Maximum number of retries to perform in case of timeout";
    private static final int MAX_TIMEOUT_RETRIES_DEFAULT = MAX_TIMEOUT_RETRIES;

    public static final String MAX_CONNECTION_RETRIES_CONFIG = "max.connection.retries";
    private static final String MAX_CONNECTION_RETRIES_DOC = "Maximum number of retries to perform in case of connection issues";
    private static final int MAX_CONNECTION_RETRIES_DEFAULT = MAX_CONNECTION_RETRIES;

    public static final String RETRY_INTERVAL_CONFIG = "retry.interval";
    private static final String RETRY_INTERVAL_DOC = "Interval between each retry";
    private static final int RETRY_INTERVAL_DEFAULT = RETRIES_INTERVAL;

    public static final String BATCH_SIZE_CONFIG = "batch.size";
    private static final String BATCH_SIZE_DOC = "Number of records per batch";
    private static final int BATCH_SIZE_DEFAULT = BATCH_SIZE;

    public static final String POOL_SIZE_CONFIG = "pool.size";
    private static final String POOL_SIZE_DOC = "Number of pool to process batch";
    private static final int POOL_SIZE_DEFAULT = POOL_SIZE;

    public static final String AWAIT_TERMINATION_MS_CONFIG = "await.termination.ms";
    private static final String AWAIT_TERMINATION_MS_DOC = "Timeout for batch processing";
    private static final int AWAIT_TERMINATION_MS_DEFAULT = AWAIT_TERMINATION_MS;

    public static final String DELETE_ENABLED_CONFIG = "delete.enabled";
    private static final String DELETE_ENABLED_DOC = "Whether to treat null record values as deletes";
    private static final boolean DELETE_ENABLED_DEFAULT = false;

    public static ConfigDef CONFIG_DEF = new ConfigDef()
            .define(CONNECTION_URL_CONFIG, ConfigDef.Type.STRING, CONNECTION_URL_DEFAULT, ConfigDef.Importance.HIGH, CONNECTION_URL_DOC)
            .define(GRPC_URL_CONFIG, ConfigDef.Type.STRING, GRPC_URL_DEFAULT, ConfigDef.Importance.HIGH, GRPC_URL_DOC)
            .define(GRPC_SECURED_CONFIG, ConfigDef.Type.BOOLEAN, GRPC_SECURED_DEFAULT, ConfigDef.Importance.HIGH, GRPC_SECURED_DOC)
            .define(AUTH_MECHANISM_CONFIG, ConfigDef.Type.STRING, AUTH_MECHANISM_DEFAULT, EnumValidator.in(AuthMechanism.values()), ConfigDef.Importance.HIGH, AUTH_MECHANISM_DOC)
            .define(API_KEY_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, API_KEY_DOC)
            .define(OIDC_CLIENT_SECRET_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, OIDC_CLIENT_SECRET_DOC)
            .define(OIDC_SCOPES_CONFIG, ConfigDef.Type.LIST, OIDC_SCOPES_DEFAULT, ConfigDef.Importance.HIGH, OIDC_SCOPES_DOC)
            .define(COLLECTION_MAPPING_CONFIG, ConfigDef.Type.STRING, COLLECTION_MAPPING_DEFAULT, ConfigDef.Importance.HIGH, COLLECTION_MAPPING_DOC)
            .define(HEADERS_CONFIG, ConfigDef.Type.LIST, HEADERS_DEFAULT, new HeaderValidator(), ConfigDef.Importance.MEDIUM, HEADERS_DOC)
            .define(CONSISTENCY_LEVEL_CONFIG, ConfigDef.Type.STRING, CONSISTENCY_LEVEL_DEFAULT, EnumValidator.in(ConsistencyLevel.values()), ConfigDef.Importance.LOW, CONSISTENCY_LEVEL_DOC)
            .define(DOCUMENT_ID_STRATEGY_CONFIG, ConfigDef.Type.CLASS, DOCUMENT_ID_STRATEGY_DEFAULT, ConfigDef.Importance.MEDIUM, DOCUMENT_ID_STRATEGY_DOC)
            .define(DOCUMENT_ID_FIELD_CONFIG, ConfigDef.Type.STRING, DOCUMENT_ID_FIELD_DEFAULT, ConfigDef.Importance.MEDIUM, DOCUMENT_ID_FIELD_DOC)
            .define(VECTOR_STRATEGY_CONFIG, ConfigDef.Type.CLASS, VECTOR_STRATEGY_DEFAULT, ConfigDef.Importance.MEDIUM, VECTOR_STRATEGY_DOC)
            .define(VECTOR_FIELD_CONFIG, ConfigDef.Type.STRING, VECTOR_FIELD_DEFAULT, ConfigDef.Importance.MEDIUM, VECTOR_FIELD_DOC)
            .define(MAX_CONNECTION_RETRIES_CONFIG, ConfigDef.Type.INT, MAX_CONNECTION_RETRIES_DEFAULT, ConfigDef.Importance.LOW, MAX_CONNECTION_RETRIES_DOC)
            .define(MAX_TIMEOUT_RETRIES_CONFIG, ConfigDef.Type.INT, MAX_TIMEOUT_RETRIES_DEFAULT, ConfigDef.Importance.LOW, MAX_TIMEOUT_RETRIES_DOC)
            .define(RETRY_INTERVAL_CONFIG, ConfigDef.Type.INT, RETRY_INTERVAL_DEFAULT, ConfigDef.Importance.LOW, RETRY_INTERVAL_DOC)
            .define(BATCH_SIZE_CONFIG, ConfigDef.Type.INT, BATCH_SIZE_DEFAULT, ConfigDef.Importance.LOW, BATCH_SIZE_DOC)
            .define(POOL_SIZE_CONFIG, ConfigDef.Type.INT, POOL_SIZE_DEFAULT, ConfigDef.Importance.LOW, POOL_SIZE_DOC)
            .define(AWAIT_TERMINATION_MS_CONFIG, ConfigDef.Type.INT, AWAIT_TERMINATION_MS_DEFAULT, ConfigDef.Importance.LOW, AWAIT_TERMINATION_MS_DOC)
            .define(DELETE_ENABLED_CONFIG, ConfigDef.Type.BOOLEAN, DELETE_ENABLED_DEFAULT, ConfigDef.Importance.LOW, DELETE_ENABLED_DOC);

    public WeaviateSinkConfig(ConfigDef definition, Map<?, ?> originals) {
        super(CONFIG_DEF, originals);
        connectionUrl = getString(CONNECTION_URL_CONFIG);
        authMechanism = AuthMechanism.valueOf(getString(AUTH_MECHANISM_CONFIG));
        apiKey = getString(API_KEY_CONFIG);
        oidcClientSecret = getString(OIDC_CLIENT_SECRET_CONFIG);
        oidcScopes = getList(OIDC_SCOPES_CONFIG);
        collectionMapping = getString(COLLECTION_MAPPING_CONFIG);
        rawHeaders = getList(HEADERS_CONFIG);
        grpcUrl = getString(GRPC_URL_CONFIG);
        grpcSecured = getBoolean(GRPC_SECURED_CONFIG);
        documentIdStrategy = getClass(DOCUMENT_ID_STRATEGY_CONFIG);
        documentIdFieldName = getString(DOCUMENT_ID_FIELD_CONFIG);
        vectorStrategy = getClass(VECTOR_STRATEGY_CONFIG);
        vectorFieldName = getString(VECTOR_FIELD_CONFIG);
        consistencyLevel = ConsistencyLevel.valueOf(getString(CONSISTENCY_LEVEL_CONFIG));
        maxConnectionRetries = getInt(MAX_CONNECTION_RETRIES_CONFIG);
        maxTimeoutRetries = getInt(MAX_TIMEOUT_RETRIES_CONFIG);
        retryInterval = getInt(RETRY_INTERVAL_CONFIG);
        batchSize = getInt(BATCH_SIZE_CONFIG);
        poolSize = getInt(POOL_SIZE_CONFIG);
        awaitTerminationMs = getInt(AWAIT_TERMINATION_MS_CONFIG);
        deleteEnabled = getBoolean(DELETE_ENABLED_CONFIG);
        if (deleteEnabled && (!documentIdStrategy.equals(KafkaIdStrategy.class))) {
            throw new IllegalArgumentException("If delete.enabled is true, document.id.strategy should be set to KafkaIdStrategy");
        }
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public AuthMechanism getAuthMechanism() {
        return authMechanism;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getOidcClientSecret() {
        return oidcClientSecret;
    }

    public String getCollectionMapping() {
        return collectionMapping;
    }

    public List<String> getOidcScopes() {
        return oidcScopes;
    }

    public Boolean getGrpcSecured() {
        return grpcSecured;
    }

    public String getGrpcUrl() {
        return grpcUrl;
    }

    public Class<?> getDocumentIdStrategy() {
        return documentIdStrategy;
    }

    public String getDocumentIdFieldName() {
        return documentIdFieldName;
    }

    public Class<?> getVectorStrategy() {
        return vectorStrategy;
    }

    public String getVectorFieldName() {
        return vectorFieldName;
    }

    public List<String> getRawHeaders() {
        return rawHeaders;
    }

    public ConsistencyLevel getConsistencyLevel() {
        return consistencyLevel;
    }

    public Integer getAwaitTerminationMs() {
        return awaitTerminationMs;
    }

    public Integer getMaxConnectionRetries() {
        return maxConnectionRetries;
    }

    public Integer getMaxTimeoutRetries() {
        return maxTimeoutRetries;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public Boolean getDeleteEnabled() {
        return deleteEnabled;
    }

    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        for (String header : rawHeaders) {
            if (!header.contains("=")) {
                throw new IllegalArgumentException("Invalid header: " + header);
            }
            headers.put(header.split("=")[0], header.split("=")[1]);
        }
        return headers;
    }

    private static class HeaderValidator implements ConfigDef.Validator {
        @SuppressWarnings("unchecked")
        @Override
        public void ensureValid(String name, Object value) {
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Invalid header value: " + value);
            }
            List<String> headers = (List<String>) value;
            for (String header : headers) {
                if (!header.contains("=")) {
                    throw new IllegalArgumentException("Invalid header value: " + header);
                }
            }
        }
    }

    private static class EnumValidator implements ConfigDef.Validator {
        private final List<String> canonicalValues;
        private final Set<String> validValues;

        private EnumValidator(List<String> canonicalValues, Set<String> validValues) {
            this.canonicalValues = canonicalValues;
            this.validValues = validValues;
        }

        public static <E> EnumValidator in(E[] enumerators) {
            final List<String> canonicalValues = new ArrayList<>(enumerators.length);
            final Set<String> validValues = new HashSet<>(enumerators.length * 2);
            for (E e : enumerators) {
                canonicalValues.add(e.toString().toLowerCase());
                validValues.add(e.toString().toUpperCase());
                validValues.add(e.toString().toLowerCase());
            }
            return new EnumValidator(canonicalValues, validValues);
        }

        @Override
        public void ensureValid(String key, Object value) {
            if (!validValues.contains(value)) {
                throw new ConfigException(key, value, "Invalid enumerator");
            }
        }

        @Override
        public String toString() {
            return canonicalValues.toString();
        }
    }

    public enum ConsistencyLevel {
        ALL,
        ONE,
        QUORUM,
    }

}
