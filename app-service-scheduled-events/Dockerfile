FROM eclipse-temurin:21-jre-alpine

RUN apk add curl

COPY build/libs/app-service-scheduled-events.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
