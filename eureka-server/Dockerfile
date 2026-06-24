FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY eureka-server/target/*-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT [ "java", "-jar", "/opt/app/app.jar" ]


