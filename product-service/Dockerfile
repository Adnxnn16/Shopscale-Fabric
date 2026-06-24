FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY product-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


