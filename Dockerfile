# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache bash curl
# Add semgrep if possible (though usually run in a separate container/worker)
# For this reference implementation, we assume CLI available or mocked.

COPY --from=build /app/target/*.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
