FROM maven:3-eclipse-temurin-17
VOLUME /tmp
ADD target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]