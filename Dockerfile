# Use OpenJDK 17 (Temurin is stable and efficient)
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /canpay

# Copy built JAR (assumes build step already ran)
COPY target/*.jar canpay.jar

# Tune JVM memory: 512M max heap, 256M min
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run Spring Boot JAR
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
