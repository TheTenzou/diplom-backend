FROM gradle:7.0.0-jdk16 as builder
USER root
WORKDIR /builder
ADD . /builder
RUN gradle build --stacktrace -x test

FROM openjdk:16-alpine
WORKDIR /app
COPY --from=builder /builder/build/libs/tsodgis-0.0.1-SNAPSHOT.jar .
COPY wait-for.sh .
# CMD ["java", "-jar", "tsodgis-0.0.1-SNAPSHOT.jar"]