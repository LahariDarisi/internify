# Use official OpenJDK 21 image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execute permission to Maven wrapper
RUN chmod +x ./mvnw

# Pre-download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy project source
COPY src src

# Build project
RUN ./mvnw package -DskipTests

# Expose port
EXPOSE 8080

# Run app
CMD ["java", "-jar", "target/internify-0.0.1-SNAPSHOT.jar"]
