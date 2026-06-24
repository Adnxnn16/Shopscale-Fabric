FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY cart-service/target/*-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


