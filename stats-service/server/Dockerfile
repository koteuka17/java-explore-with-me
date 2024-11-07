FROM eclipse-temurin:21.0.2_13-jdk-jammy
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]