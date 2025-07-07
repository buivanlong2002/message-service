# ------------ Stage 1: Build Jar ------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml và tải dependencies trước (tối ưu cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code sau
COPY src ./src

# Build ứng dụng, bỏ qua test
RUN mvn clean package -DskipTests


# ------------ Stage 2: Run App ------------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose đúng cổng app chạy
EXPOSE 8885

# Khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
