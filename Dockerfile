FROM gradle:jdk17-alpine as builder
USER root
WORKDIR /builder
ADD . /builder
RUN gradle build --stacktrace

FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]


#docker buildx create --use
#docker buildx build --push --platform linux/arm64 -t lspiridonov/smartserver-smart:latest .