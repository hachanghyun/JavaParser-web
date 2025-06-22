from langchain.embeddings import OllamaEmbeddings
from langchain.vectorstores import FAISS
from langchain.schema import Document

embedding = OllamaEmbeddings(model="llama3")

docs = [Document(page_content="Spring Boot는 자바 기반의 백엔드 프레임워크입니다.")]
db = FAISS.from_documents(docs, embedding)
db.save_local("vector_index")
