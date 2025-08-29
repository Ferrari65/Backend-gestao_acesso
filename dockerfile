# Primeiro estágio (build) = compila projeto e gera app.jar.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./

# Otimização
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -DskipTests clean package

# SEGUNDO estágio (runtime) = copia apenas o app.jar e define como rodar.

FROM eclipse-temurin:21-jre-alpine

ARG BUILD_VERSION
ARG VCS_REF
LABEL org.opencontainers.image.title="TrackPass API" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.source="https://github.com/<org>/<repo>"


RUN addgroup -S trackpass && adduser -S -G trackpass -D trackpass
WORKDIR /app

# Copia o JAR da etapa de build e garante que o usuário trackpass é o proprietário
COPY --from=build --chown=trackpass:trackpass /workspace/target/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=dev \
    SERVER_PORT=8080 \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -Duser.timezone=America/Sao_Paulo"

EXPOSE 8080
USER trackpass

ENTRYPOINT ["java","-jar","/app/app.jar"]