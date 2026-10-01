# TVendoApi

API REST educativa para exponer funcionalidades de `TVendo` usando `HttpServer` de Java y JSON con Jackson.

## 1) Requisitos previos

- JDK 25 configurado en IntelliJ IDEA.
- Maven 3.9+.
- Proyecto `TVendo` instalado en repositorio local Maven:
  - Ejecutar en `TVendo`: `mvn clean install`
- Servicios externos listos:
  - `YapeApp` en `http://localhost:8091`
  - `PlinApp` en `http://localhost:8092`
- Base de datos según `db.motor` de `tvendo.properties` (`MYSQL` o `POSTGRES`).

## 2) Compilar y ejecutar

Desde la raíz del proyecto `TVendoApi`:

```bash
mvn clean compile
```

Ejecutar clase principal:

- `pe.edu.ulima.isw2.tvendoapi.app.TVendoApiApplication`

Al iniciar debe mostrar:

- `TVendo API iniciada`
- `http://localhost:8090/api`

## 3) Base URL

- `http://localhost:8090/api`

## 4) Configurar motor de base de datos

Archivo de configuración en este proyecto:

- `src/main/resources/tvendo.properties`

Cambio por archivo:

```properties
db.motor=POSTGRES
```

o

```properties
db.motor=MYSQL
```

Override temporal por VM options en IntelliJ:

```bash
-Ddb.motor=POSTGRES
```

`-Ddb.motor=...` tiene prioridad sobre `tvendo.properties`.

## 5) Endpoints disponibles

- `GET /api/productos`
- `GET /api/productos/{codigo}`
- `POST /api/ventas`
- `GET /api/ventas/{codigo}`
- `PUT /api/ventas/{codigo}/anular`
- `GET /api/ventas/{codigo}/comprobante`

## 6) Pruebas rápidas con curl

> Todos los ejemplos usan `Content-Type: application/json`.

### 6.1 Listar productos

```bash
curl -i http://localhost:8090/api/productos
```

Esperado:
- HTTP `200`
- Lista JSON de productos.

### 6.2 Buscar producto por código

```bash
curl -i http://localhost:8090/api/productos/P-001
```

Esperado:
- HTTP `200` si existe.
- HTTP `404` si no existe.

### 6.3 Registrar venta (YAPE)

```bash
curl -i -X POST http://localhost:8090/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "codigoVenta": "V-002",
    "productos": {
      "P-001": 1,
      "P-002": 1
    },
    "tipoEnvio": "ESTANDAR",
    "tipoPago": "YAPE",
    "numeroDestino": "999111222",
    "titular": "José Pérez",
    "descripcionPago": "Pago de venta V-002"
  }'
```

Esperado:
- HTTP `201`
- JSON de la venta registrada (`VentaResponseDTO`).

### 6.4 Registrar venta (PLIN)

```bash
curl -i -X POST http://localhost:8090/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "codigoVenta": "V-003",
    "productos": {
      "P-001": 1
    },
    "tipoEnvio": "EXPRESS",
    "tipoPago": "PLIN",
    "numeroDestino": "988333444",
    "titular": "María Núñez",
    "descripcionPago": "Pago de venta V-003"
  }'
```

Esperado:
- HTTP `201`
- JSON de la venta registrada.

### 6.5 Consultar venta por código

```bash
curl -i http://localhost:8090/api/ventas/V-002
```

Esperado:
- HTTP `200` si existe.
- HTTP `404` si no existe.

### 6.6 Anular venta

```bash
curl -i -X PUT http://localhost:8090/api/ventas/V-002/anular
```

Esperado:
- HTTP `200`
- JSON de la venta actualizada (estado anulado).

### 6.7 Obtener comprobante

```bash
curl -i http://localhost:8090/api/ventas/V-002/comprobante
```

Esperado:
- HTTP `200`
- JSON con:
  - `codigoVenta`
  - `comprobante` (texto generado por `VentaFacade.obtenerComprobante(...)`).

## 7) Validar CORS (Angular localhost:4200)

Preflight `OPTIONS` de ejemplo:

```bash
curl -i -X OPTIONS http://localhost:8090/api/ventas \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type"
```

Esperado en headers:

- `Access-Control-Allow-Origin: http://localhost:4200`
- `Access-Control-Allow-Methods: GET, POST, PUT, OPTIONS`
- `Access-Control-Allow-Headers: Content-Type`

## 8) Errores HTTP esperados

- `400`: solicitud inválida (JSON inválido, tipo envío no soportado, campos obligatorios vacíos).
- `404`: recurso no encontrado (producto/venta inexistente).
- `405`: método no permitido.
- `409`: conflicto de negocio (por ejemplo, pago rechazado o stock insuficiente lanzado como `IllegalStateException`).
- `500`: error interno inesperado.

## 9) Notas de operación

- `TVendoApi` no ejecuta SQL ni JDBC directo.
- `TVendoApi` no llama directamente a `YapeApp`/`PlinApp`.
- La lógica de negocio y patrones (`Command`, `Facade`, `Strategy`, `Adapter`) se resuelven en `TVendo`.
