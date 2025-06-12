# Media Recommender - Configuración Docker

Este proyecto ha sido dockerizado para facilitar su despliegue y desarrollo. A continuación se explica cómo usar Docker con la aplicación.

## Prerrequisitos

- Docker Desktop instalado
- Docker Compose instalado (viene incluido con Docker Desktop)

## Estructura de archivos Docker

```
proyecto/
├── Dockerfile                      # Configuración del contenedor de la app
├── docker-compose.yml             # Orquestación de servicios
├── .dockerignore                  # Archivos a ignorar en el build
├── init.sql                       # Script de inicialización de MySQL
└── application-docker.properties  # Configuración para Docker
```

## Comandos básicos

### 1. Construir y ejecutar todo el stack

```bash
# Construir las imágenes y ejecutar los contenedores
docker-compose up --build

# Ejecutar en segundo plano
docker-compose up -d --build
```

### 2. Ver logs

```bash
# Logs de todos los servicios
docker-compose logs

# Logs de un servicio específico
docker-compose logs media-recommender-app
docker-compose logs mysql-db

# Seguir logs en tiempo real
docker-compose logs -f media-recommender-app
```

### 3. Gestionar los contenedores

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (¡cuidado, se pierden los datos!)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart media-recommender-app
```

### 4. Ejecutar comandos dentro de los contenedores

```bash
# Acceder al contenedor de la aplicación
docker-compose exec media-recommender-app bash

# Acceder a MySQL
docker-compose exec mysql-db mysql -u appuser -p media_recommender
```

## Configuración de servicios

### MySQL Database
- **Puerto externo**: 3307 (para evitar conflictos con MySQL local)
- **Puerto interno**: 3306
- **Base de datos**: `media_recommender`
- **Usuario**: `appuser`
- **Contraseña**: `apppassword`
- **Root password**: `rootpassword`

### Spring Boot Application
- **Puerto**: 8081
- **Perfil**: production (usando application-docker.properties)
- **URL**: http://localhost:8081

## Variables de entorno

El archivo `docker-compose.yml` define las siguientes variables de entorno:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/media_recommender?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  SPRING_DATASOURCE_USERNAME: appuser
  SPRING_DATASOURCE_PASSWORD: apppassword
  APP_JWT_SECRET: JWTSuperSecretKey2024MediaRecommender
  APP_JWT_EXPIRATION_MS: 86400000
```

## Desarrollo con Docker

### Desarrollo local con recarga automática

Para desarrollo, puedes montar el código fuente como volumen:

```yaml
# Agregar en docker-compose.yml bajo media-recommender-app:
volumes:
  - ./src:/app/src
  - app_logs:/app/logs
```

### Debugging

Para habilitar debugging remoto, modifica el Dockerfile:

```dockerfile
# Cambiar la línea CMD por:
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "target/media-recommender-0.0.1-SNAPSHOT.jar"]
```

Y exponer el puerto en docker-compose.yml:

```yaml
ports:
  - "8081:8081"
  - "5005:5005"  # Puerto de debugging
```

## Resolución de problemas

### La aplicación no se conecta a MySQL

1. Verifica que MySQL esté saludable:
   ```bash
   docker-compose ps
   ```

2. Revisa los logs de MySQL:
   ```bash
   docker-compose logs mysql-db
   ```

3. Verifica la conectividad desde la app:
   ```bash
   docker-compose exec media-recommender-app ping mysql-db
   ```

### Resetear la base de datos

```bash
# Detener servicios y eliminar volúmenes
docker-compose down -v

# Volver a construir y ejecutar
docker-compose up --build
```

### Problemas de permisos

En sistemas Unix/Linux, asegúrate de que el script mvnw tenga permisos:

```bash
chmod +x mvnw
```

### Cambiar configuración de MySQL

Modifica las variables de entorno en `docker-compose.yml` según tus necesidades:

```yaml
environment:
  MYSQL_ROOT_PASSWORD: tu_password_root
  MYSQL_DATABASE: tu_base_datos
  MYSQL_USER: tu_usuario
  MYSQL_PASSWORD: tu_password
```

## Despliegue en producción

Para producción, considera:

1. Usar un archivo `.env` para las variables sensibles
2. Configurar un reverse proxy (nginx)
3. Usar volúmenes persistentes para los datos
4. Configurar backups automáticos de la base de datos
5. Implementar health checks más robustos

### Ejemplo de archivo .env

```env
MYSQL_ROOT_PASSWORD=secure_root_password
MYSQL_PASSWORD=secure_app_password
JWT_SECRET=your_super_secure_jwt_secret_key_here
```

## Scripts de utilidad

### Backup de la base de datos

```bash
docker-compose exec mysql-db mysqldump -u root -p media_recommender > backup.sql
```

### Restaurar backup

```bash
docker-compose exec -T mysql-db mysql -u root -p media_recommender < backup.sql
```

## Monitoring

Para monitorear los contenedores:

```bash
# Ver uso de recursos
docker stats

# Ver información detallada de los contenedores
docker-compose ps -a
```