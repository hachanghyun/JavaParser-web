package com.example.java_parser.controller;

import com.example.java_parser.model.ParsedClass;
import com.example.java_parser.service.JavaSourceAnalyzerService;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
import com.example.java_parser.service.RagStorageService;

@RestController
@RequestMapping("/api/source")
@CrossOrigin(origins = "http://localhost:5173") // CORS 허용 추가
public class SourceController {

    private final JavaSourceAnalyzerService analyzer;
    private final RagStorageService ragStorage;

    public SourceController(JavaSourceAnalyzerService analyzer, RagStorageService ragStorage) {
        this.analyzer = analyzer;
        this.ragStorage = ragStorage;
    }

    @GetMapping("/search")
    public List<ParsedClass> searchSource(@RequestParam String keyword) throws Exception {
        Path projectRoot = Path.of("/Users/hachanghyun/IdeaProjects/pluginproject");
        return analyzer.search(projectRoot, keyword);
    }

    @GetMapping("/search/all")
    public List<ParsedClass> searchAllSource() throws Exception {
        Path rootPath = Path.of("/Users/hachanghyun/IdeaProjects/pluginproject");
        return analyzer.searchAll(rootPath);
    }

    @GetMapping("/all")
    public List<ParsedClass> getAll(@RequestParam String projectId) {
        return ragStorage.getClasses(projectId);
    }
}

