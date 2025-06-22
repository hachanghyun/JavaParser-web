FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# JAR 파일 명시적으로 지정
CMD ["java", "-jar", "build/libs/java-parser-0.0.1-SNAPSHOT.jar"]
