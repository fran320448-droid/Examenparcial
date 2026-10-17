-- =====================================================
-- bd_techservices - Script de creacion (phpMyAdmin)
-- Importar este archivo completo desde la pestana
-- "Importar" de phpMyAdmin. Solo valores de prueba.
-- =====================================================

DROP DATABASE IF EXISTS bd_techservices;
CREATE DATABASE bd_techservices CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_techservices;

-- Tabla de usuarios autorizados de la API
CREATE TABLE usuarios_api (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(60) NOT NULL,
    api_key VARCHAR(80) NOT NULL UNIQUE,
    token_autenticacion VARCHAR(80) NOT NULL UNIQUE,
    tokens_disponibles INT NOT NULL DEFAULT 0,
    estado VARCHAR(10) NOT NULL DEFAULT 'activo'
) ENGINE=InnoDB;

-- Tabla de clientes consultables
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    dni VARCHAR(8) NOT NULL UNIQUE,
    correo_electronico VARCHAR(120) NOT NULL,
    telefono VARCHAR(9) NOT NULL
) ENGINE=InnoDB;

-- Usuarios de prueba (solo valores de desarrollo)
INSERT INTO usuarios_api (nombre_usuario, api_key, token_autenticacion, tokens_disponibles, estado) VALUES
('usuario1', 'MI_API_KEY_2026',            'TOKEN_SEGURO_123456',          10, 'activo'),
('usuario2', 'API_KEY_USUARIO2_2026',      'TOKEN_USUARIO2_987654321',     10, 'activo'),
('usuario3', 'API_KEY_USUARIO3_2026',      'TOKEN_USUARIO3_555555555',      0, 'activo'),
('usuario4', 'API_KEY_USUARIO4_2026',      'TOKEN_USUARIO4_111111111',      5, 'inactivo');

-- Clientes de prueba (datos ficticios)
INSERT INTO clientes (nombres, apellidos, dni, correo_electronico, telefono) VALUES
('Carlos',  'Ramirez',     '12345678', 'carlos.ramirez@example.com',  '999888777'),
('Maria',   'Torres',      '87654321', 'maria.torres@example.com',    '988776655'),
('Jorge',   'Quispe',      '11223344', 'jorge.quispe@example.com',    '977665544'),
('Lucia',   'Fernandez',   '44332211', 'lucia.fernandez@example.com', '966554433'),
('Diego',   'Salazar',     '55443322', 'diego.salazar@example.com',   '955443322');
