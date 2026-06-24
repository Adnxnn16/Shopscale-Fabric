FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY gateway-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


