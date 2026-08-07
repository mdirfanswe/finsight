# --- Build stage ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .

# ✅ Give mvnw executable permissions
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]