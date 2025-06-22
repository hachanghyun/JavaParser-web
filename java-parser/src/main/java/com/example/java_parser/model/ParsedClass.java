package com.example.java_parser.model;

import java.util.List;

public class ParsedClass {
    public String className;
    public List<String> annotations;
    public List<ParsedMethod> methods;
}
