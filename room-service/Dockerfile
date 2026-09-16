FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9003
ENTRYPOINT ["jar","-jar", "app.jar"]