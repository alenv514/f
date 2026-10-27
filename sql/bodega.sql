DROP DATABASE IF EXISTS bodega_db;

CREATE DATABASE bodega_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bodega_db;

CREATE TABLE productos (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    cantidad INT UNSIGNED NOT NULL DEFAULT 0,
    estante VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_productos_codigo (codigo),
    KEY idx_productos_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO productos (codigo, nombre, cantidad, estante) VALUES
('P-001', 'Martillo de acero', 25, 'Estante A1'),
('P-002', 'Destornillador plano', 40, 'Estante A1'),
('P-003', 'Taladro eléctrico', 8, 'Estante B2'),
('P-004', 'Caja de clavos 2 pulgadas', 120, 'Estante C3'),
('P-005', 'Cinta métrica 5 metros', 33, 'Estante B1'),
('P-006', 'Guantes de seguridad', 60, 'Estante D4');

SELECT id, codigo, nombre, cantidad, estante FROM productos;
