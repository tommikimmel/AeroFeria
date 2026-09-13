# ==============================================================================
# 1. Build Stage (Compilación con Maven y JDK 21)
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilación de código fuente
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==============================================================================
# 2. Runtime Stage (Contenedor Liviano JRE 21)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario sin privilegios para mayor seguridad
RUN addgroup -S aeroferia && adduser -S aeroferia -G aeroferia
RUN mkdir -p /app/uploads && chown -R aeroferia:aeroferia /app

# Copiar el artefacto generado
COPY --from=builder /build/target/*.jar app.jar
RUN chown aeroferia:aeroferia app.jar

USER aeroferia

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
