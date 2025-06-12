# Usar una imagen base de OpenJDK 21
FROM openjdk:21-jdk-slim

# Información del mantenedor
LABEL maintainer="media-recommender"

# Crear directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml primero para aprovechar el cache de Docker
COPY pom.xml .

# Copiar el wrapper de Maven
COPY mvnw .
COPY .mvn .mvn

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar las dependencias (esto se cachea si no cambia el pom.xml)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto 8081 (según tu application.properties)
EXPOSE 8081

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/media-recommender-0.0.1-SNAPSHOT.jar"]