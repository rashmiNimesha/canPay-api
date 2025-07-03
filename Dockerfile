# Stage 1: Build the application using Maven and OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy all project files to the build context

COPY . . 

# Build the project, skipping tests
RUN mvn clean package -DskipTests 

# Stage 2: Create a lightweight image to run the application
FROM maven:3.9.6-eclipse-temurin-21-alpine

WORKDIR /canpay

# Copy the built JAR from the build stage
COPY --from=build /build/target/*.jar canpay.jar

# Expose port for the application
EXPOSE 8081

# Run the application with specified JVM options
ENTRYPOINT ["java", "-Xms256m", "-Xmx768m", "-jar", "canpay.jar"] 
