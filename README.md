# 오프라인 RAG 기반 Java 분석 챗봇 시스템

이 프로젝트는 Java 프로젝트의 클래스/메서드 정보를 분석하여 벡터화하고, 로컬 LLM(LLaMA2)을 통해 자연어로 질의응답할 수 있는 RAG(Retrieval-Augmented Generation) 기반 챗봇입니다.  
SI 프로젝트 환경에서도 사용할 수 있도록 **외부 인터넷 연결 없이 완전 오프라인으로 작동**하도록 설계했습니다.

---

### 프로젝트 메인화면
<img width="1920" alt="스크린샷 2025-06-22 오후 2 45 25" src="https://github.com/user-attachments/assets/d1992272-91ce-42b7-a525-532b7dabb0bd" />


### 실제 분석할 자바 프로젝트 등록
<img width="1920" alt="스크린샷 2025-06-22 오후 2 45 54" src="https://github.com/user-attachments/assets/4dff0344-344b-4f12-bd63-e04f6aeac859" />


### 분석된 자바 메소드 목록
<img width="1916" alt="스크린샷 2025-06-22 오후 2 46 03" src="https://github.com/user-attachments/assets/af081463-7e46-4138-9d58-ce5cacb1e092" />


### 로컬LLM을 통해 메소드 상세 내용 검색 기능1
<img width="1920" alt="스크린샷 2025-06-22 오후 2 47 27" src="https://github.com/user-attachments/assets/e133fca7-ab31-4f3c-818d-01e33460fec7" />


### 로컬LLM을 통해 메소드 상세 내용 검색 기능2
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



## 실행 방법

### 1. Java 분석기 실행

### 2. RAG 서버 실행 (FastAPI)

```bash
uvicorn main:app --reload
```

### 3. React UI 실행

```bash
npm install
npm run dev
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
