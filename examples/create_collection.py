import sys;
import weaviate
from weaviate.classes.config import Property
from weaviate.collections.classes.config import Configure, DataType

if len(sys.argv) < 2:
    print("Usage: python create_collection.py <collection_name>")
    sys.exit(1)

collection_name = sys.argv[1]

with weaviate.connect_to_local() as client:
    if client.collections.exists(collection_name):
        client.collections.delete(collection_name)
    client.collections.create(
            name=collection_name,
            properties=[
                Property(name="string", data_type=DataType.TEXT),
                Property(name="number", data_type=DataType.NUMBER),
                ],
            vectorizer_config=Configure.Vectorizer.text2vec_ollama(model="nomic-embed-text", api_endpoint="http://host.docker.internal:11434")
            )

