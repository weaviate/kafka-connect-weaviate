package io.weaviate.connector;

import io.weaviate.connector.idstrategy.IDStrategy;
import io.weaviate.connector.vectorstrategy.VectorStrategy;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.*;

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

    public static final String DOCUMENT_ID_STRATEGY_CONFIG = "document.id.strategy";
    private static final String DOCUMENT_ID_STRATEGY_DOC = "Java class returning the document ID for each record";
    private static final Class<? extends IDStrategy> DOCUMENT_ID_STRATEGY_DEFAULT = io.weaviate.connector.idstrategy.NoIdStrategy.class;

    public static final String VECTOR_STRATEGY_CONFIG = "vector.strategy";
    private static final String VECTOR_STRATEGY_DOC = "Java class returning the document embedding for each record";
    private static final Class<? extends VectorStrategy> VECTOR_STRATEGY_DEFAULT = io.weaviate.connector.vectorstrategy.NoVectorStrategy.class;

    public static final String VECTOR_FIELD_CONFIG = "vector.field.name";
    private static final String VECTOR_FIELD_DOC = "Field name containing the embedding";
    private static final String VECTOR_FIELD_DEFAULT = "vector";

    public static ConfigDef CONFIG_DEF = new ConfigDef()
            .define(CONNECTION_URL_CONFIG, ConfigDef.Type.STRING, CONNECTION_URL_DEFAULT, ConfigDef.Importance.HIGH, CONNECTION_URL_DOC)
            .define(GRPC_URL_CONFIG, ConfigDef.Type.STRING, GRPC_URL_DEFAULT, ConfigDef.Importance.HIGH, GRPC_URL_DOC)
            .define(GRPC_SECURED_CONFIG, ConfigDef.Type.BOOLEAN, GRPC_SECURED_DEFAULT, ConfigDef.Importance.HIGH, GRPC_SECURED_DOC)
            .define(AUTH_MECHANISM_CONFIG, ConfigDef.Type.STRING, AUTH_MECHANISM_DEFAULT, EnumValidator.in(AuthMechanism.values()), ConfigDef.Importance.HIGH, AUTH_MECHANISM_DOC)
            .define(API_KEY_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, API_KEY_DOC)
            .define(OIDC_CLIENT_SECRET_CONFIG, ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, OIDC_CLIENT_SECRET_DOC)
            .define(OIDC_SCOPES_CONFIG, ConfigDef.Type.LIST, OIDC_SCOPES_DEFAULT, ConfigDef.Importance.HIGH, OIDC_SCOPES_DOC)
            .define(COLLECTION_MAPPING_CONFIG, ConfigDef.Type.STRING, COLLECTION_MAPPING_DEFAULT, ConfigDef.Importance.HIGH, COLLECTION_MAPPING_DOC)
            .define(DOCUMENT_ID_STRATEGY_CONFIG, ConfigDef.Type.CLASS, DOCUMENT_ID_STRATEGY_DEFAULT, ConfigDef.Importance.HIGH, DOCUMENT_ID_STRATEGY_DOC)
            .define(VECTOR_STRATEGY_CONFIG, ConfigDef.Type.CLASS, VECTOR_STRATEGY_DEFAULT, ConfigDef.Importance.HIGH, VECTOR_STRATEGY_DOC)
            .define(VECTOR_FIELD_CONFIG, ConfigDef.Type.STRING, VECTOR_FIELD_DEFAULT, ConfigDef.Importance.HIGH, VECTOR_FIELD_DOC);

    public WeaviateSinkConfig(ConfigDef definition, Map<?, ?> originals) {
        super(CONFIG_DEF, originals);
        connectionUrl = getString(CONNECTION_URL_CONFIG);
        authMechanism = AuthMechanism.valueOf(getString(AUTH_MECHANISM_CONFIG));
        apiKey = getString(API_KEY_CONFIG);
        oidcClientSecret = getString(OIDC_CLIENT_SECRET_CONFIG);
        oidcScopes = getList(OIDC_SCOPES_CONFIG);
        collectionMapping = getString(COLLECTION_MAPPING_CONFIG);
        grpcUrl = getString(GRPC_URL_CONFIG);
        grpcSecured = getBoolean(GRPC_SECURED_CONFIG);
        documentIdStrategy = getClass(DOCUMENT_ID_STRATEGY_CONFIG);
        vectorStrategy = getClass(VECTOR_STRATEGY_CONFIG);
        vectorFieldName = getString(VECTOR_FIELD_CONFIG);
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

    public Class<?> getVectorStrategy() {
        return vectorStrategy;
    }

    public String getVectorFieldName() {
        return vectorFieldName;
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
}
