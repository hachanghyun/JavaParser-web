package com.example.java_parser.service;

import com.example.java_parser.model.ParsedClass;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RagStorageService {
    private final Map<String, ProjectData> store = new HashMap<>();

    public void save(String projectId, String name, List<ParsedClass> data) {
        store.put(projectId, new ProjectData(name, data));
    }

    public List<ParsedClass> getClasses(String projectId) {
        return store.getOrDefault(projectId, new ProjectData("", List.of())).classes;
    }

    public static class ProjectData {
        public final String name;
        public final List<ParsedClass> classes;

        public ProjectData(String name, List<ParsedClass> classes) {
            this.name = name;
            this.classes = classes;
        }
    }
}