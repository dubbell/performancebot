FROM maven:3.9.0
VOLUME /tmp
ADD target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]