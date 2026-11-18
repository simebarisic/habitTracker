
# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/habit-tracker-backend.jar app.jar
EXPOSE 8080
# Heap do 75% limita kontejnera (mem_limit u composeu); bez limita bi JVM uzeo
# četvrtinu RAM-a cijelog servera, a više takvih backenda zajedno ga može srušiti.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "app.jar"]
