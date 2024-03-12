FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
ARG ENV_FILE=.env

COPY ${JAR_FILE} application.jar
COPY ${ENV_FILE} ${ENV_FILE}

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]