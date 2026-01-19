# Стадия сборки
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml .
COPY server/pom.xml ./server/
COPY gateway/pom.xml ./gateway/

# Скачиваем зависимости
RUN mvn dependency:go-offline -B

# Копируем исходный код gateway
COPY gateway/src ./gateway/src

# Собираем приложение
RUN mvn clean package -pl gateway -am -DskipTests

# Финальная стадия
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем JAR из стадии сборки
COPY --from=builder /build/gateway/target/*.jar app.jar

# Настройки JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]