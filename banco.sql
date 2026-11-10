-- Base de datos: banco_db
DROP DATABASE IF EXISTS banco_db;
CREATE DATABASE banco_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE banco_db;

CREATE TABLE clientes (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  cedula VARCHAR(10) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- Un cliente puede tener varias cuentas (1 a N)
CREATE TABLE cuentas (
  id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
  numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
  tipo VARCHAR(20) NOT NULL,
  id_cliente INT NOT NULL,
  FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
) ENGINE=InnoDB;

-- Cada deposito o retiro es un movimiento de una cuenta
CREATE TABLE movimientos (
  id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
  id_cuenta INT NOT NULL,
  tipo ENUM('DEPOSITO','RETIRO') NOT NULL,
  monto DECIMAL(12,2) NOT NULL CHECK (monto > 0),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_cuenta) REFERENCES cuentas(id_cuenta)
) ENGINE=InnoDB;

-- Datos de prueba
INSERT INTO clientes (nombre, cedula) VALUES
('Juan Perez', '1801234567'),
('Maria Lopez', '1807654321'),
('Carlos Ruiz', '1802468135');

INSERT INTO cuentas (numero_cuenta, tipo, id_cliente) VALUES
('0001-AHO-001', 'Ahorros', 1),
('0001-COR-002', 'Corriente', 1),
('0002-AHO-003', 'Ahorros', 2),
('0003-AHO-004', 'Ahorros', 3),
('0003-COR-005', 'Corriente', 3);

INSERT INTO movimientos (id_cuenta, tipo, monto, fecha) VALUES
(1, 'DEPOSITO', 500.00, '2026-11-01 09:00:00'),
(1, 'RETIRO',   120.50, '2026-11-02 10:30:00'),
(1, 'DEPOSITO', 200.00, '2026-11-03 15:45:00'),
(2, 'DEPOSITO', 1000.00,'2026-11-01 11:00:00'),
(2, 'RETIRO',   300.00, '2026-11-04 16:20:00'),
(3, 'DEPOSITO', 750.00, '2026-11-02 08:15:00'),
(4, 'DEPOSITO', 300.00, '2026-11-03 12:00:00'),
(4, 'RETIRO',    50.00, '2026-11-05 14:10:00'),
(5, 'DEPOSITO', 2000.00,'2026-11-01 13:30:00');
