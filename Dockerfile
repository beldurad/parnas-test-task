FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

COPY docker.env .env

ENTRYPOINT ["sh", "-c", "export $(cat .env | xargs) && java -jar app.jar"]