FROM gradle:8.11.1-jdk21-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN gradle --no-daemon dependencies

COPY src src

RUN gradle --no-daemon clean build -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080 || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]