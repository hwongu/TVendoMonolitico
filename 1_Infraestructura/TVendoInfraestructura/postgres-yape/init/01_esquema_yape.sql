SET client_encoding = 'UTF8';

CREATE TABLE IF NOT EXISTS pago_yape (
    id BIGSERIAL PRIMARY KEY,
    codigo_operacion VARCHAR(60) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    nombre_cliente VARCHAR(120),
    monto NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    moneda VARCHAR(10) NOT NULL DEFAULT 'PEN',
    estado VARCHAR(30) NOT NULL,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO pago_yape (
    codigo_operacion,
    telefono,
    nombre_cliente,
    monto,
    estado,
    descripcion
)
VALUES (
    'YAPE-DEMO-001',
    '999111222',
    'José Pérez',
    3580.00,
    'APROBADO',
    'Operación Yape de demostración'
)
ON CONFLICT (codigo_operacion) DO NOTHING;
