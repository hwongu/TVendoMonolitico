SET client_encoding = 'UTF8';

CREATE TABLE IF NOT EXISTS pago_plin (
    id BIGSERIAL PRIMARY KEY,
    codigo_operacion VARCHAR(60) NOT NULL UNIQUE,
    numero_destino VARCHAR(20) NOT NULL,
    titular VARCHAR(120),
    monto_centimos INTEGER NOT NULL CHECK (monto_centimos > 0),
    moneda VARCHAR(10) NOT NULL DEFAULT 'PEN',
    estado VARCHAR(30) NOT NULL,
    detalle VARCHAR(255),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO pago_plin (
    codigo_operacion,
    numero_destino,
    titular,
    monto_centimos,
    estado,
    detalle
)
VALUES (
    'PLIN-DEMO-001',
    '988333444',
    'María Núñez',
    15050,
    'APROBADO',
    'Operación Plin de demostración'
)
ON CONFLICT (codigo_operacion) DO NOTHING;
