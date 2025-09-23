FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine

ARG BUILD_VERSION
ARG VCS_REF
LABEL org.opencontainers.image.title="TrackPass API" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.source="https://github.com/<org>/<repo>"

RUN addgroup -S trackpass && adduser -S -G trackpass -D trackpass
WORKDIR /app

COPY --from=build --chown=trackpass:trackpass /workspace/target/*.jar /app/app.jar

ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=dev \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -Duser.timezone=America/Sao_Paulo -Dfile.encoding=UTF-8"

EXPOSE 8080
USER trackpass

ENTRYPOINT ["java","-jar","/app/app.jar"]
