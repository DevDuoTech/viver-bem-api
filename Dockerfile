FROM openjdk:17-alpine

ARG JAR_FILE=target/*.jar
ARG ENV_FILE=.env
ARG APPLICATION_YAML_FILE=src/main/resources/application.yml
ARG APPLICATION_DEV_YAML_FILE=src/main/resources/application-dev.yml
ARG APPLICATION_PROD_YAML_FILE=src/main/resources/application-prod.yml

COPY ${JAR_FILE} application.jar
COPY ${ENV_FILE} ${ENV_FILE}
COPY ${APPLICATION_YAML_FILE} application.yml
COPY ${APPLICATION_DEV_YAML_FILE} application-dev.yml
COPY ${APPLICATION_PROD_YAML_FILE} application-prod.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]