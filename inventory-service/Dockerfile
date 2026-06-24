FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY inventory-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


