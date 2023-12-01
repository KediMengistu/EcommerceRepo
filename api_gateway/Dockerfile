FROM eclipse-temurin:17.0.4.1_1-jre AS build
WORKDIR extracted
ADD target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract


FROM eclipse-temurin:17.0.4.1_1-jre
WORKDIR application
COPY --from=builder extracted/dependecies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./
# home/app/target/api_gateway-0.0.1-SNAPSHOT.jar api_gateway-0.0.1-SNAPSHOT.jar

EXPOSE 5000

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]