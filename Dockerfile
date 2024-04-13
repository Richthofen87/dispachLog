FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/dispatchLog-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE app.jar
COPY --from=ghcr.io/ufoscout/docker-compose-wait:latest /wait /wait
RUN chmod +x /wait
ENTRYPOINT /wait && java -jar /app.jar