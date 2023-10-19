# syntax=docker/dockerfile:1

# use jdk 8
FROM openjdk:8-jdk-alpine
WORKDIR /app


COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]