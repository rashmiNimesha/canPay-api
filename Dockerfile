# Stage 1: Build the application using Maven and OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /build
COPY . .

# Build the project and skip tests
RUN mvn clean package -DskipTests

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /canpay

# Copy the built JAR from the builder stage
COPY --from=build /build/target/*.jar canpay.jar

# Set low-memory JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseSerialGC"

# Expose the Spring Boot server port
EXPOSE 8081

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar canpay.jar"]
