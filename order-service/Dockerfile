FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY order-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


