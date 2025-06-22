# 오프라인 RAG 기반 Java 분석 챗봇 시스템

이 프로젝트는 Java 프로젝트의 클래스/메서드 정보를 분석하여 벡터화하고, 로컬 LLM(LLaMA2)을 통해 자연어로 질의응답할 수 있는 RAG(Retrieval-Augmented Generation) 기반 챗봇입니다.  
SI 프로젝트 환경에서도 사용할 수 있도록 **외부 인터넷 연결 없이 완전 오프라인으로 작동**하도록 설계했습니다.

---

<img width="1920" alt="스크린샷 2025-06-22 오후 2 45 25" src="https://github.com/user-attachments/assets/d1992272-91ce-42b7-a525-532b7dabb0bd" />
<img width="1920" alt="스크린샷 2025-06-22 오후 2 45 54" src="https://github.com/user-attachments/assets/13af9d68-2822-4b43-86f5-c49c0b92d5d5" />
<img width="1920" alt="스크린샷 2025-06-22 오후 2 46 03" src="https://github.com/user-attachments/assets/5a12bcf3-4859-4a54-8b82-d03a129f4673" />
<img width="1920" alt="스크린샷 2025-06-22 오후 2 47 27" src="https://github.com/user-attachments/assets/e133fca7-ab31-4f3c-818d-01e33460fec7" />
<img width="1920" alt="스크린샷 2025-06-22 오후 2 48 31" src="https://github.com/user-attachments/assets/7054e1b3-6dfc-44d2-9ca3-21e15ce1880b" />

---

## 사용 기술 스택

| 구성 요소         | 기술명                                  |
|------------------|------------------------------------------|
| 프론트엔드       | React (Vite)                             |
| 백엔드 (RAG 서버) | Python, FastAPI, LangChain               |
| 백엔드 (분석기)   | Java, Spring Boot, JavaParser            |
| LLM              | LLaMA2 (로컬, Ollama 사용)               |
| 임베딩           | HuggingFace Embeddings (MiniLM)          |
| 벡터 DB          | FAISS (로컬 인덱스 저장)                |

---

## 디렉토리 구조

```
project-root/
├── rag-server/        # FastAPI 기반 RAG 서버 (Python)
├── java-parser/       # Java 코드 분석기 (Spring Boot)
├── react-ui/          # React 기반 사용자 UI
├── models/            # 로컬 LLaMA 모델 저장 경로
├── vector_index/      # FAISS 인덱스 저장소
└── README.md
```

---

## 아키텍처 다이어그램

flowchart TD
    A[Java 프로젝트<br>파서 (Spring Boot)] --> B[클래스/메서드 JSON 추출]
    B --> C[FastAPI 기반 RAG 서버]
    C --> D[HuggingFace 임베딩 수행]
    D --> E[FAISS 벡터 저장소]
    C --> F[LLM 응답 생성 (Ollama LLaMA2)]
    G[사용자 질문 (React UI)] --> C
    F --> H[자연어 응답 반환]

    subgraph RAG 서버 구성
        C
        D
        E
        F
    end

---

## 실행 방법

### 1. Java 분석기 실행

```bash
cd java-parser
./gradlew bootRun
```

### 2. RAG 서버 실행 (FastAPI)

```bash
cd rag-server
uvicorn main:app --reload
```

### 3. React UI 실행

```bash
cd react-ui
npm install
npm run dev
```

### 4. Java 코드 전송 API

```http
POST http://localhost:8000/rag/init
Content-Type: application/json

{
  "projectId": "example-project",
  "parsedClasses": [ ... ]
}
```

### 5. 질문 API 호출

```http
POST http://localhost:8000/ask
Content-Type: application/json

{
  "question": "UserController에 어떤 메소드가 있나요?"
}
```

---

## 개발 배경

- SI 프로젝트는 보안상 외부 LLM(OpenAI 등) 사용이 어려운 경우가 많습니다.
- Java 프로젝트 구조를 빠르게 파악하기 어려운 신규 투입 인력에게 도움이 되도록 설계했습니다.
- 이 시스템은 로컬 분석기 + 벡터 저장소 + 로컬 LLM으로 구성되어 완전한 오프라인 환경에서도 사용 가능합니다.
- 프로젝트 구조와 메서드 정보를 자연어로 질의응답할 수 있어 문서화 + Q&A 자동화를 실현할 수 있습니다.

---

## 특징 및 장점

- 인터넷 없이도 사용 가능한 완전 오프라인 구성
- 기존 Java 프로젝트 그대로 분석 가능
- 프론트엔드는 React로 구성되어 사용이 간편함
- FastAPI 기반으로 확장성 있는 API 서버 구조
- 신규 투입 인력을 위한 빠른 코드 구조 파악 지원

---
