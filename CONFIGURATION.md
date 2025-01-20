# Parameters

### weaviate.connection.url     
**description**: "Weaviate connection URL, should be following the format `<scheme>://<host>:<port>`                       
**default**: http://localhost:8080

### weaviate.grpc.url           
**description**: Weaviate GRPC connection URL                                                                              
**default**: localhost:50051

### weaviate.grpc.secured       
**description**: Weaviate GRPC TLS secured connection, set to True to enable TLS encryption                                
**default**: false

### weaviate.auth.scheme        
**description**: Authentication mechanism to use to connect to Weaviate, could be NONE, API_KEY or OIDC_CLIENT_CREDENTIALS  
**default**: NONE  
**valid values**:  
- NONE  
- API_KEY  
- OIDC_CLIENT_CREDENTIALS  


### weaviate.api.key            
**description**: User API Key if API Key authentication mechanism is used                                                  

### weaviate.oidc.client.secret 
**description**: User OIDC client secret if OIDC authentication mechanism is used                                          

### weaviate.oidc.scopes        
**description**: OIDC client scope if OIDC authentication mechanism is used                                                

### collection.mapping          
**description**: Mapping between Kafka topic and Weaviate collection                                                       
**default**: ${topic}

### document.id.strategy        
**description**: Java class returning the document ID for each record                                                      
**default**: io.weaviate.connector.idstrategy.NoIdStrategy  
**valid values**:  
- io.weaviate.connector.idstrategy.NoIdStrategy
- io.weaviate.connector.idstrategy.KafkaIdStrategy
- io.weaviate.connector.idstrategy.FieldIdStrategy

### document.id.field.name      
**description**: Field name containing the ID in Kafka                                                                     
**default**: id

### vector.strategy             
**description**: Java class returning the document embedding for each record                                               
**default**: io.weaviate.connector.vectorstrategy.NoVectorStrategy  
**valid values**:  
- io.weaviate.connector.vectorstrategy.NoVectorStrategy
- io.weaviate.connector.vectorstrategy.FieldVectorStrategy

### vector.field.name           
**description**: Field name containing the embedding (used only for FieldVectorStrategy)                                   
