package com.example.java_parser.controller;

import com.example.java_parser.dto.ProjectRequest;
import com.example.java_parser.model.ParsedClass;
import com.example.java_parser.service.JavaSourceAnalyzerService;
import com.example.java_parser.service.RagStorageService;
import com.example.java_parser.service.RagTransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final JavaSourceAnalyzerService analyzer;
    private final RagStorageService ragStorage;
    private final RagTransferService ragTransferService;

    public ProjectController(JavaSourceAnalyzerService analyzer,
                             RagStorageService ragStorage,
                             RagTransferService ragTransferService) {
        this.analyzer = analyzer;
        this.ragStorage = ragStorage;
        this.ragTransferService = ragTransferService;
    }

//    @PostMapping
//    public ResponseEntity<?> registerProject(@RequestBody ProjectRequest req) throws IOException {
//        Path rootPath = Path.of(req.path);
//        List<ParsedClass> classes = analyzer.searchAll(rootPath);
//
//        // 저장소에 저장
//        String projectId = UUID.randomUUID().toString();
//        ragStorage.save(projectId, req.name, classes);
//
//        return ResponseEntity.ok(Map.of("projectId", projectId, "classCount", classes.size()));
//    }

    @GetMapping("/chat")
    public String chat(@RequestParam String projectId, @RequestParam String question) {
        List<ParsedClass> data = ragStorage.getClasses(projectId);
        // 간단한 RAG 처리 예시
        return data.stream()
                .flatMap(pc -> pc.methods.stream())
                .filter(pm -> pm.name.contains(question) || (pm.description != null && pm.description.contains(question)))
                .map(pm -> "Method: " + pm.name + ", Description: " + pm.description)
                .findFirst()
                .orElse("관련된 메서드를 찾을 수 없습니다.");
    }

    @PostMapping
    public ResponseEntity<?> registerProject(@RequestBody ProjectRequest req) throws IOException {
        Path rootPath = Path.of(req.path);
        List<ParsedClass> classes = analyzer.searchAll(rootPath);

        String projectId = UUID.randomUUID().toString();
        ragStorage.save(projectId, req.name, classes);

        // ✅ RAG 서버에 전송
        ragTransferService.sendParsedDataToRag(projectId, classes);

        return ResponseEntity.ok(Map.of("projectId", projectId, "classCount", classes.size()));
    }

}