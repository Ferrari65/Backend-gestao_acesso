# ====== BUILD ======
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests clean package

# ====== RUNTIME ======
FROM eclipse-temurin:21-jre-alpine

# Certificados para HTTPS (Supabase SSL)
RUN apk add --no-cache ca-certificates

# Usuário não-root
RUN addgroup -S trackpass && adduser -S -G trackpass -D trackpass
WORKDIR /app

# Copia o JAR gerado do estágio build
# (se quiser ser mais estrito: use ARG JAR_FILE=/workspace/target/*-SNAPSHOT.jar)
COPY --from=build --chown=trackpass:trackpass /workspace/target/*.jar /app/app.jar

# Profile padrão para prod (o Render pode sobrescrever via env)
ENV SPRING_PROFILES_ACTIVE=prod

# Força IPv4 e define timezone/encoding; tudo em UMA linha
ENV JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75\ -Duser.timezone=America/Sao_Paulo\ -Dfile.encoding=UTF-8\ -Djava.net.preferIPv4Stack=true

EXPOSE 8080
USER trackpass

ENTRYPOINT ["java","-jar","/app/app.jar"]
