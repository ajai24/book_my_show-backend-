# Build stage
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/book-my-show.jar ./app.jar

# Expose port (will be overridden by Render)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
