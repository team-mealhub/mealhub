FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV DB_URL=localhost:5432

ENTRYPOINT ["java", "-jar", "-Ddb.url=${DB_URL}", "-Ddb.username=${DB_USERNAME}", "-Ddb.password=${DB_PASSWORD}", "-Ddb.ddl-auto=${DB_DDL}", "-Djwt.secret-key=${JWT_SECRET}", "-Djwt.access-expiration=${JWT_ACCESS_EXPIRATION}", "app.jar"]

EXPOSE 3218