package com.example.java_parser.service;

import com.example.java_parser.model.ParsedClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class RagTransferService {

    private final RestTemplate restTemplate;

    public RagTransferService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendParsedDataToRag(String projectId, List<ParsedClass> parsedClasses) {
        Map<String, Object> body = Map.of(
                "projectId", projectId,
                "parsedClasses", parsedClasses
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8000/rag/init", request, String.class
            );
            System.out.println("✅ RAG 서버 응답: " + response.getBody());
        } catch (Exception e) {
            System.err.println("❌ RAG 서버 전송 실패: " + e.getMessage());
        }
    }
}

