# Dockerfile

# Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Execução
FROM eclipse-temurin:17-jdk
WORKDIR /app
VOLUME /tmp
COPY --from=builder /app/target/*.jar app.jar

# Variaveis de ambiente aqui
#
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
