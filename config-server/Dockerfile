FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY config-server/target/*-SNAPSHOT.jar app.jar
EXPOSE 8888
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


