from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from langchain_community.vectorstores import FAISS
from langchain_ollama import OllamaEmbeddings
import os

app = FastAPI()

# ✅ 벡터 임베딩 초기화
embedding = OllamaEmbeddings(model="llama3")

# ✅ FAISS 인덱스 로드 시 예외 방지
vectorstore = None
VECTOR_INDEX_PATH = "vector_index"

if os.path.exists(VECTOR_INDEX_PATH):
    try:
        vectorstore = FAISS.load_local(VECTOR_INDEX_PATH, embeddings=embedding)
        print("✅ FAISS 인덱스 로딩 완료")
    except Exception as e:
        print("❌ FAISS 로딩 실패:", str(e))
else:
    print("❌ 벡터 인덱스 디렉토리가 존재하지 않습니다.")

# ✅ 질문 구조
class Query(BaseModel):
    question: str

@app.post("/ask")
def ask(query: Query):
    print("📥 받은 질문:", query.question)  # 확인 로그
    if not vectorstore:
        raise HTTPException(status_code=500, detail="벡터 스토어가 초기화되지 않음")

    docs = vectorstore.similarity_search(query.question, k=3)

    if not docs:
        return {"answer": "관련된 문서를 찾을 수 없습니다."}

    return {"answer": docs[0].page_content}
