FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY eurekaServer ./eurekaServer
RUN mvn -q -f eurekaServer/pom.xml package -DskipTests -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.httpconnectionManager.ttlSecs=60

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/eurekaServer/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
