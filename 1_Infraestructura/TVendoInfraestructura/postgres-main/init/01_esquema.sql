SET client_encoding = 'UTF8';

CREATE TABLE IF NOT EXISTS producto (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255),
    precio NUMERIC(12,2) NOT NULL CHECK (precio > 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS venta (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
    costo_envio NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (costo_envio >= 0),
    total NUMERIC(12,2) NOT NULL CHECK (total >= 0),
    estado VARCHAR(30) NOT NULL,
    observacion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL REFERENCES venta(id),
    producto_id BIGINT NOT NULL REFERENCES producto(id),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(12,2) NOT NULL CHECK (precio_unitario > 0),
    subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE IF NOT EXISTS pago (
    id BIGSERIAL PRIMARY KEY,
    venta_id BIGINT NOT NULL REFERENCES venta(id),
    tipo VARCHAR(30) NOT NULL,
    monto NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    estado VARCHAR(30) NOT NULL,
    codigo_externo VARCHAR(80),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(255)
);

INSERT INTO producto (codigo, nombre, descripcion, precio, stock)
VALUES
    ('P-001', 'Laptop', 'Laptop para ingeniería de software', 3500.00, 10),
    ('P-002', 'Mouse', 'Mouse inalámbrico', 80.00, 30),
    ('P-003', 'Teclado', 'Teclado mecánico con distribución en español', 150.00, 20),
    ('P-004', 'Cámara', 'Cámara web con micrófono', 220.00, 15)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO venta (codigo, subtotal, costo_envio, total, estado, observacion)
VALUES
    ('V-001', 3580.00, 0.00, 3580.00, 'REGISTRADA', 'Venta de demostración para José Pérez')
ON CONFLICT (codigo) DO NOTHING;

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
