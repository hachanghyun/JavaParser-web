package com.example.java_parser.service;

import com.example.java_parser.model.ParsedClass;
import com.example.java_parser.model.ParsedMethod;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class JavaSourceAnalyzerService {
    private static final Logger logger = LoggerFactory.getLogger(JavaSourceAnalyzerService.class);


    public List<ParsedClass> search(Path rootPath, String keyword) throws IOException {
        logger.info("search 시작: rootPath={}, keyword={}", rootPath, keyword);
        List<ParsedClass> fullList = analyzeInternal(rootPath);
        String lowerKeyword = keyword.toLowerCase();

        List<ParsedClass> filtered = fullList.stream()
                .map(pc -> {
                    List<ParsedMethod> filteredMethods = pc.methods.stream()
                            .filter(pm ->
                                    pm.name.toLowerCase().contains(lowerKeyword) ||
                                            pm.returnType.toLowerCase().contains(lowerKeyword) ||
                                            (pm.description != null && pm.description.toLowerCase().contains(lowerKeyword)) ||
                                            pm.parameters.stream().anyMatch(p ->
                                                    p.name.toLowerCase().contains(lowerKeyword) ||
                                                            p.type.toLowerCase().contains(lowerKeyword)
                                            )
                            )
                            .toList();

                    if (!filteredMethods.isEmpty()) {
                        ParsedClass newClass = new ParsedClass();
                        newClass.className = pc.className;
                        newClass.annotations = pc.annotations;
                        newClass.methods = filteredMethods;
                        return newClass;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        logger.info("search 종료: {}개 클래스 반환", filtered.size());
        return filtered;
    }

  private List<ParsedClass> analyzeInternal(Path rootPath) throws IOException {
        logger.info("analyzeInternal 시작: {}", rootPath);
        List<ParsedClass> result = new ArrayList<>();
        AtomicInteger fileCount = new AtomicInteger(0);
        AtomicInteger classCount = new AtomicInteger(0);

        Files.walk(rootPath)
                .filter(Files::isRegularFile) // ✅ 파일만 통과
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.toString().contains("/build/")) // ✅ 불필요 디렉토리 제외
                .filter(p -> !p.toString().contains("/.idea/"))
                .filter(p -> !p.toString().contains("/out/"))
                .peek(p -> {
                    fileCount.incrementAndGet();
                    System.out.println("📄 분석 대상: " + p);
                })
                .forEach(file -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(file.toFile());
                        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                            classCount.incrementAndGet();
                            ParsedClass pc = new ParsedClass();
                            pc.className = clazz.getFullyQualifiedName().orElse(clazz.getNameAsString());
                            pc.annotations = clazz.getAnnotations().stream()
                                    .map(a -> "@" + a.getNameAsString())
                                    .toList();
                            List<ParsedMethod> methods = new ArrayList<>();
                            clazz.getMethods().forEach(method -> {
                                ParsedMethod pm = new ParsedMethod();
                                pm.name = method.getNameAsString();
                                pm.returnType = method.getType().toString();
                                method.getBody().ifPresent(body -> {
                                    String bodyStr = body.toString();
                                    StringBuilder inferred = new StringBuilder();
                                    if (bodyStr.contains(".get")) inferred.append("이벤트나 객체에서 값을 추출합니다. ");
                                    if (bodyStr.contains(".contains(")) inferred.append("문자열 포함 여부를 확인합니다. ");
                                    if (bodyStr.contains("logger.")) inferred.append("로그를 출력합니다. ");
                                    if (bodyStr.contains("if (")) inferred.append("조건 분기 처리를 수행합니다. ");
                                    if (bodyStr.contains("return ")) inferred.append("값을 반환합니다. ");
                                    String safeDescription = inferred.toString()
                                            .replace("\"", "'")
                                            .replace("\n", " ")
                                            .replace("\r", " ");
                                    pm.description = safeDescription.trim();
                                });
                                List<ParsedMethod.Parameter> params = new ArrayList<>();
                                method.getParameters().forEach(p -> {
                                    ParsedMethod.Parameter param = new ParsedMethod.Parameter();
                                    param.name = p.getNameAsString();
                                    param.type = p.getType().toString();
                                    params.add(param);
                                });
                                pm.parameters = params;
                                methods.add(pm);
                            });
                            pc.methods = methods;
                            result.add(pc);
                        });
                    } catch (Exception e) {
                        logger.error("파싱 실패: {} → {}", file, e.getMessage());
                        System.err.println("❌ 파싱 실패: " + file + " → " + e.getMessage());
                    }
                });

        System.out.println("총 자바 파일 수: " + fileCount.get());
        System.out.println("총 클래스 수: " + classCount.get());
        logger.info("analyzeInternal 종료: {}개 클래스 분석됨", result.size());
        return result;
    }

    public List<ParsedClass> searchAll(Path rootPath) throws IOException {
        return analyzeInternal(rootPath); // 필터 없이 전체 반환
    }
}
