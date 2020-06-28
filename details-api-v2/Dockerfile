FROM openjdk:8u141-jdk-slim
EXPOSE 9080

COPY launch.sh /service/launch.sh
RUN chmod 755 /service/launch.sh
COPY target/details-api-0.0.1-SNAPSHOT.jar /service/app.jar
RUN chmod 755 /service/app.jar

ENTRYPOINT ["/service/launch.sh"]
