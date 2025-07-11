# 오프라인 RAG 기반 Java 분석 챗봇 시스템
이 프로젝트는 Java 프로젝트의 클래스/메서드 정보를 분석하여 벡터화하고, 로컬 LLM(LLaMA2)을 통해 
자연어로 질의응답할 수 있는 RAG(Retrieval-Augmented Generation) 기반 챗봇.  
SI 프로젝트 환경에서도 사용할 수 있도록 외부 인터넷 연결 없이 완전 오프라인으로 작동하도록 설계

# 기술 스택

| 구성 요소         | 기술명                                  |
|------------------|------------------------------------------|
| 프론트엔드       | React (Vite)                             |
| 백엔드 (RAG 서버) | Python, FastAPI, LangChain               |
| 백엔드 (분석기)   | Java, Spring Boot, JavaParser            |
| LLM              | LLaMA2 (로컬, Ollama 사용)               |
| 임베딩           | HuggingFace Embeddings (MiniLM)          |
| 벡터 DB          | FAISS (로컬 인덱스 저장)                |

# 디렉토리 구조
    project-root/
    ├── rag-server/        # FastAPI 기반 RAG 서버 (Python)
    ├── java-parser/       # Java 코드 분석기 (Spring Boot)
    ├── react-ui/          # React 기반 사용자 UI
    ├── models/            # 로컬 LLaMA 모델 저장 경로
    ├── vector_index/      # FAISS 인덱스 저장소
    └── README.md


# 실행 방법
    docker-compose down && docker-compose up --build -d

## 1. Java 분석기 실행

## 2. RAG 서버 실행 (FastAPI)
    uvicorn main:app --reload

## 3. React UI 실행
    npm install
    npm run dev
