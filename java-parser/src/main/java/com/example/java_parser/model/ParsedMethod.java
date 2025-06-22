package com.example.java_parser.model;

import java.util.List;

public class ParsedMethod {
    public String name;
    public String returnType;
    public String description;  // 정적 추론
    public String llmSummary;   // LLM 요약
    public List<Parameter> parameters;

    public static class Parameter {
        public String name;
        public String type;
    }
}
