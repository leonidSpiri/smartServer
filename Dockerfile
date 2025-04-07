# Стадия сборки
FROM gradle:8.5-jdk17 AS builder
WORKDIR /home/gradle/project

# Копируем все исходники и скрипты
COPY --chown=gradle:gradle . .

# Собираем приложение
RUN gradle clean build --no-daemon

# Стадия рантайма
FROM openjdk:17-jdk-slim
WORKDIR /app

# Копируем готовый JAR из предыдущего контейнера
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
