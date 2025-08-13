FROM openjdk:21-jdk-slim

WORKDIR /app

# Копируем JAR файл
COPY target/eureka-service-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8761

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"] 