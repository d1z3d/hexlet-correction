FROM gradle:8.7-jdk21 as build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle clean build --no-daemon

FROM openjdk:21 as builder
WORKDIR /app
COPY --from=build /build/libs/typoreporter-0.0.1-SNAPSHOT.jar /app/typoreporter.jar
RUN java -Djarmode=layertools -jar typoreporter.jar extract

FROM openjdk:21
WORKDIR /app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
