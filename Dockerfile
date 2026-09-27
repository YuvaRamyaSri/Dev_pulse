FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/devpulse-1.0.0.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.war"]
