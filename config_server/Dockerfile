FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
WORKDIR build
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine@sha256:c26a727c4883eb73d32351be8bacb3e70f390c2c94f078dc493495ed93c60c2f AS layers
WORKDIR layer
COPY --from=builder /build/target/config_server-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
RUN addgroup --system appuser && adduser -S -s /usr/sbin/nologin -G appuser appuser
COPY --from=layers /layer/dependencies/ ./
COPY --from=layers /layer/spring-boot-loader/ ./
COPY --from=layers /layer/snapshot-dependencies/ ./
COPY --from=layers /layer/application/ ./
RUN chown -R appuser:appuser /opt/app
USER appuser
EXPOSE 8888
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
#FROM eclipse-temurin:21-jdk-alpine as build1
#
#RUN apk add --no-cache bash procps curl tar
#
## common for all images
#ENV MAVEN_HOME /usr/share/maven
#
#COPY --from=maven:3.9.5-eclipse-temurin-11 ${MAVEN_HOME} ${MAVEN_HOME}
#COPY --from=maven:3.9.5-eclipse-temurin-11 /usr/local/bin/mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
#COPY --from=maven:3.9.5-eclipse-temurin-11 /usr/share/maven/ref/settings-docker.xml /usr/share/maven/ref/settings-docker.xml
#
#RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn
#
#ARG MAVEN_VERSION=3.9.5
#ARG USER_HOME_DIR="/root"
#ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
#
#ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
#CMD ["mvn"]
#
#FROM build1 AS build2
#WORKDIR home/config_server
#COPY ./* ./
#COPY pom.xml /config_server
#
#RUN mvn -f ./pom.xml clean package
#
#FROM eclipse-temurin:21-jdk-alpine
#WORKDIR home
#COPY --from=build2 home/config_server/target/config_server-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8888
#ENTRYPOINT ["java","-jar","app.jar"]