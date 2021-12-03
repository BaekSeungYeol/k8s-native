FROM harbor.clap.hmc.co.kr/library/docker/gradle:6.7.1-jdk11 AS build

WORKDIR /home/gradle/src

# only dependancy files download for cache
COPY --chown=gradle:gradle build.gradle settings.gradle gradle ./
RUN gradle build --no-daemon > /dev/null 2>&1 || true

COPY --chown=gradle:gradle . ./
RUN gradle build --no-daemon -x test

FROM harbor.clap.hmc.co.kr/library/docker/openjdk:16-ea-23-jdk-alpine3.12 AS builder
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
RUN java -Djarmode=layertools -jar /app/spring-boot-application.jar extract

FROM harbor.clap.hmc.co.kr/library/docker/openjdk:16-ea-23-jdk-alpine3.12
WORKDIR /app
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
RUN chown -R appuser:appuser /app
USER appuser
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT [ "java", "org.springframework.boot.loader.JarLauncher"]