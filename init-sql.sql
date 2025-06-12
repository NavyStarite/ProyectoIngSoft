-- Script de inicialización para la base de datos
-- Este archivo se ejecuta automáticamente cuando se crea el contenedor de MySQL

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS media_recommender;

-- Usar la base de datos
USE media_recommender;

-- Crear usuario para la aplicación (opcional, ya que se crea en docker-compose)
-- CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'apppassword';
-- GRANT ALL PRIVILEGES ON media_recommender.* TO 'appuser'@'%';
-- FLUSH PRIVILEGES;

-- Configuraciones adicionales para MySQL
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- Opcional: Insertar datos de prueba iniciales
-- INSERT INTO users (username, email, password, created_at) VALUES 
-- ('admin', 'admin@example.com', '$2a$10$...', NOW());

-- Mensaje de confirmación
SELECT 'Base de datos media_recommender inicializada correctamente' AS mensaje;