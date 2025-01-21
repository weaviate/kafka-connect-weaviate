# Connection parameters

### weaviate.connection.url     
**documentation**: "Weaviate connection URL, should be following the format `<scheme>://<host>:<port>`                       
**default**: http://localhost:8080

### weaviate.grpc.url           
**documentation**: Weaviate GRPC connection URL                                                                              
**default**: localhost:50051

### weaviate.grpc.secured       
**documentation**: Weaviate GRPC TLS secured connection, set to True to enable TLS encryption                                
**default**: false

### weaviate.auth.scheme        
**documentation**: Authentication mechanism to use to connect to Weaviate, could be NONE, API_KEY or OIDC_CLIENT_CREDENTIALS  
**default**: NONE  
**valid values**:  
- NONE  
- API_KEY  
- OIDC_CLIENT_CREDENTIALS  

### weaviate.headers
**documentation**: Headers to provide while building Weaviate client (e.g. X-OpenAI-Api-Key)

# Security parameters

### weaviate.api.key            
**documentation**: User API Key if API Key authentication mechanism is used                                                  

### weaviate.oidc.client.secret 
**documentation**: User OIDC client secret if OIDC authentication mechanism is used                                          

### weaviate.oidc.scopes        
**documentation**: OIDC client scope if OIDC authentication mechanism is used                                                

# Collection & schema parameters

### topics
**documentation**: List of topics to consume, separated by commas

### topics.regex
**documentation**: Regular expression giving topics to consume. Under the hood, the regex is compiled to a `java.util.regex.Pattern`. Only one of topics or topics.regex should be specified.

### collection.mapping          
**documentation**: Mapping between Kafka topic and Weaviate collection                                                       
**default**: ${topic}

### document.id.strategy        
**documentation**: Java class returning the document ID for each record                                                      
**default**: io.weaviate.connector.idstrategy.NoIdStrategy  
**valid values**:  
- io.weaviate.connector.idstrategy.NoIdStrategy
- io.weaviate.connector.idstrategy.KafkaIdStrategy
- io.weaviate.connector.idstrategy.FieldIdStrategy

### document.id.field.name      
**documentation**: Field name containing the ID in Kafka                                                                     
**default**: id

### vector.strategy             
**documentation**: Java class returning the document embedding for each record                                               
**default**: io.weaviate.connector.vectorstrategy.NoVectorStrategy  
**valid values**:  
- io.weaviate.connector.vectorstrategy.NoVectorStrategy
- io.weaviate.connector.vectorstrategy.FieldVectorStrategy

### vector.field.name           
**documentation**: Field name containing the embedding (used only for FieldVectorStrategy)                                   

# Retry parameters

### max.connection.retries
**documentation**: Maximum number of retries to perform in case of connection issues
**default**: 3

### max.timeout.retries
**default**: 3
**documentation**: Maximum number of retries to perform in case of timeout

### retry.interval
**documentation**: Interval between each retry
**default**: 2000

# Performance parameters

### batch.size
**documentation**: Number of records per batch
**default**: 100

### pool.size
**documentation**: Number of pool to process batch
**default**: 1

### await.termination.ms
**documentation**: Timeout for batch processing
**default**: 10000

