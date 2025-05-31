# ----- Stage 1: Build the app - just to support deployment service -----
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ----- Stage 2: Run the app -----
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/weather-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
