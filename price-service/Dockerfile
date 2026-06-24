FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY price-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


