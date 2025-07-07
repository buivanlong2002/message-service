# ------------ Stage 1: Build Jar ------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml và tải dependencies trước (tối ưu cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source (nếu có thêm file ngoài src)
COPY . .

# Build ứng dụng, bỏ qua test
RUN mvn clean package -DskipTests


# ------------ Stage 2: Run App ------------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Đặt biến môi trường JAVA_OPTS để Railway inject thêm nếu cần
ENV JAVA_OPTS=""

# Expose đúng cổng app chạy (Railway sẽ map lại cổng)
EXPOSE 8885

# Dùng exec form để truyền biến JAVA_OPTS nếu có
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
