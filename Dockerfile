# 베이스 이미지로 OpenJDK 17 버전을 사용합니다.
FROM openjdk:17-jdk-slim

# JAR 파일이 위치할 경로를 변수로 지정합니다.
ARG JAR_FILE=build/libs/*.jar

# 변수로 지정된 경로의 JAR 파일을 app.jar로 복사합니다.
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java","-jar","/app.jar"]