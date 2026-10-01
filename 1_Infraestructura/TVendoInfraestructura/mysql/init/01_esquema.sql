CREATE DATABASE IF NOT EXISTS isw2_mysql
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE isw2_mysql;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_producto_precio CHECK (precio > 0),
    CONSTRAINT chk_producto_stock CHECK (stock >= 0)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS venta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12,2) NOT NULL,
    costo_envio DECIMAL(12,2) NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    observacion VARCHAR(255),
    CONSTRAINT chk_venta_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_venta_costo_envio CHECK (costo_envio >= 0),
    CONSTRAINT chk_venta_total CHECK (total >= 0)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS detalle_venta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (venta_id) REFERENCES venta(id),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_precio CHECK (precio_unitario > 0),
    CONSTRAINT chk_detalle_subtotal CHECK (subtotal >= 0)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    codigo_externo VARCHAR(80),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(255),
    CONSTRAINT fk_pago_venta
        FOREIGN KEY (venta_id) REFERENCES venta(id),
    CONSTRAINT chk_pago_monto CHECK (monto > 0)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

INSERT IGNORE INTO producto (codigo, nombre, descripcion, precio, stock)
VALUES
    ('P-001', 'Laptop', 'Laptop para ingeniería de software', 3500.00, 10),
    ('P-002', 'Mouse', 'Mouse inalámbrico', 80.00, 30),
    ('P-003', 'Teclado', 'Teclado mecánico con distribución en español', 150.00, 20),
    ('P-004', 'Cámara', 'Cámara web con micrófono', 220.00, 15);

INSERT IGNORE INTO venta (codigo, subtotal, costo_envio, total, estado, observacion)
VALUES
    ('V-001', 3580.00, 0.00, 3580.00, 'REGISTRADA', 'Venta de demostración para José Pérez');

INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal)
SELECT v.id, p.id, 1, 3500.00, 3500.00
FROM venta v
JOIN producto p ON p.codigo = 'P-001'
WHERE v.codigo = 'V-001'
  AND NOT EXISTS (
      SELECT 1 FROM detalle_venta d
      WHERE d.venta_id = v.id AND d.producto_id = p.id
  );

INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal)
SELECT v.id, p.id, 1, 80.00, 80.00
FROM venta v
JOIN producto p ON p.codigo = 'P-002'
WHERE v.codigo = 'V-001'
  AND NOT EXISTS (
      SELECT 1 FROM detalle_venta d
      WHERE d.venta_id = v.id AND d.producto_id = p.id
  );

INSERT INTO pago (venta_id, tipo, monto, estado, codigo_externo, observacion)
SELECT v.id, 'YAPE', 3580.00, 'APROBADO', 'YAPE-DEMO-001', 'Pago válido para demostración'
FROM venta v
WHERE v.codigo = 'V-001'
  AND NOT EXISTS (
      SELECT 1 FROM pago p
      WHERE p.venta_id = v.id AND p.codigo_externo = 'YAPE-DEMO-001'
  );
