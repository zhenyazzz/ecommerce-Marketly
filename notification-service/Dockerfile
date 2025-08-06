FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create runtime image
FROM openjdk:21-jre-slim

WORKDIR /app

# Copy the built jar
COPY --from=0 /app/target/*.jar app.jar

# Create non-root user
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser
USER javauser

# Expose port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8084/api/v1/notifications/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 