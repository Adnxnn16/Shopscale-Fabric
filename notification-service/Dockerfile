FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY notification-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


