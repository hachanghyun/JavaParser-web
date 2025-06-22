from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List

from langchain_community.vectorstores import FAISS
from langchain.schema import Document
from langchain.chains import RetrievalQA
from langchain.prompts import PromptTemplate
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_community.llms import Ollama

import os
import shutil

app = FastAPI()

# ✅ CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ✅ 전역 변수
VECTOR_DIR = "vector_index"
embedding = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")
vectorstore = None
qa_chain = None

# ✅ LLM Prompt 커스터마이징
prompt_template = PromptTemplate.from_template(
    """당신은 자바 전문가입니다. 다음은 프로젝트의 클래스 정의와 메서드 설명입니다:

{context}

질문: {question}
답변: 전체 클래스와 모든 메서드 시그니처 및 설명을 가능한 한 빠짐없이 한국어로 나열하세요. 추론은 하지 마세요."""
)

# ✅ 초기 로딩
try:
    if os.path.exists(VECTOR_DIR):
        vectorstore = FAISS.load_local(
            VECTOR_DIR,
            embeddings=embedding,
            allow_dangerous_deserialization=True
        )
        print("✅ 벡터스토어 로드 완료")

        llm = Ollama(model="llama2")
        qa_chain = RetrievalQA.from_chain_type(
            llm=llm,
            retriever=vectorstore.as_retriever(search_kwargs={"k": 20}),
            chain_type_kwargs={"prompt": prompt_template},
            return_source_documents=False
        )
    else:
        print("✅ 벡터 디렉토리 없음. 새로 생성 예정.")
except Exception as e:
    print(f"⚠️ 초기 로딩 실패: {e}")

# ✅ 데이터 구조
class MethodParam(BaseModel):
    name: str
    type: str

class ParsedMethod(BaseModel):
    name: str
    returnType: str
    description: str | None = Field(default="")
    parameters: List[MethodParam]

class ParsedClass(BaseModel):
    className: str
    annotations: List[str]
    methods: List[ParsedMethod]

class RagInitRequest(BaseModel):
    projectId: str
    parsedClasses: List[ParsedClass]

class Query(BaseModel):
    question: str

# ✅ 변환 함수 (중복 제거 포함, 포맷 통일)
def parsed_classes_to_documents(classes: List[ParsedClass]) -> List[Document]:
    documents = []
    for cls in classes:
        annotation_text = " ".join(cls.annotations)
        seen_methods = set()
        method_summaries = []
        for m in cls.methods:
            signature = f"{m.name}({', '.join(p.name + ': ' + p.type for p in m.parameters)}): {m.returnType}"
            if signature not in seen_methods:
                seen_methods.add(signature)
                description = str(m.description or "")  # 🛡️ 방어 로직
                method_summaries.append(f"* {signature} - {description}\n")
        header = f"**{cls.className}**"
        if annotation_text:
            header += f" ({annotation_text})"
        content = header + "\n" + "\n".join(method_summaries)
        documents.append(Document(
            page_content=content.strip(),
            metadata={"class": cls.className}
        ))
    return documents

# ✅ 문서 등록 API
@app.post("/rag/init")
def init_rag(req: RagInitRequest):
    global vectorstore, qa_chain

    docs = parsed_classes_to_documents(req.parsedClasses)
    if not docs:
        raise HTTPException(status_code=400, detail="등록할 문서가 없음")

    if os.path.exists(VECTOR_DIR):
        shutil.rmtree(VECTOR_DIR)

    vectorstore = FAISS.from_documents(docs, embedding)
    print("📌 새 벡터스토어 생성됨")

    vectorstore.save_local(VECTOR_DIR)
    llm = Ollama(model="llama2")
    qa_chain = RetrievalQA.from_chain_type(
        llm=llm,
        retriever=vectorstore.as_retriever(search_kwargs={"k": 20}),
        chain_type_kwargs={"prompt": prompt_template},
        return_source_documents=False
    )

    return {"status": "ok", "added": len(docs)}

# ✅ 질문 응답 API (LLM 사용)
@app.post("/ask")
def ask(query: Query):
    if not qa_chain:
        raise HTTPException(status_code=500, detail="QA 체인이 초기화되지 않음")
    answer = qa_chain.invoke({"query": query.question})
    if isinstance(answer, dict) and "result" in answer:
        answer = answer["result"]

    lines = answer.strip().splitlines()
    deduped = list(dict.fromkeys(lines))
    spaced = []
    for line in deduped:
        spaced.append(line)
        if line.strip().startswith("*"):
            spaced.append("")
    return {"answer": "\n".join(spaced).strip()}
