# syntax=docker/dockerfile:1.7

FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]s