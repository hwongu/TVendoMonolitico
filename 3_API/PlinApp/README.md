# PlinApp - Servicio REST educativo (Java 25 + HttpServer + JDBC)

Proyecto didáctico para simular pagos Plin sin frameworks REST (sin Spring/Spring Boot/JPA/Hibernate/Jakarta/Lombok).

## 1) Requisitos

- JDK 25
- Maven 3.9+
- PostgreSQL accesible (configurable en `src/main/resources/application.properties`) en:
    - Host: `localhost`
    - Puerto: `5436`
    - Base: `plin_db`
    - Usuario: `postgres`
    - Password: `clave123`
- Tabla existente `pago_plin` (no se crea automáticamente desde la app)

## 2) Ejecutar el servicio

Desde la raíz del proyecto:

```bash
mvn clean compile
mvn exec:java
```

El servicio inicia en:

- `http://localhost:8092/api/v1/plin/pagos`

Mensaje esperado en consola:

```text
Plin Service iniciado
http://localhost:8092/api/v1/plin/pagos
```

## 3) Endpoints REST disponibles

Base URL:

- `http://localhost:8092/api/v1/plin/pagos`

### 3.1 POST /api/v1/plin/pagos

Registra un pago Plin.

#### Request

```http
POST /api/v1/plin/pagos
Content-Type: application/json
```

```json
{
  "numeroDestino": "988333444",
  "titular": "María Núñez",
  "montoCentimos": 15050,
  "detalle": "Pago de venta V-001"
}
```

#### cURL

```bash
curl -i -X POST "http://localhost:8092/api/v1/plin/pagos" ^
  -H "Content-Type: application/json" ^
  -d "{\"numeroDestino\":\"988333444\",\"titular\":\"María Núñez\",\"montoCentimos\":15050,\"detalle\":\"Pago de venta V-001\"}"
```

#### Respuesta esperada

- HTTP `201 Created`

```json
{
  "codigoOperacion": "PLIN-XXXXXXXX",
  "numeroDestino": "988333444",
  "titular": "María Núñez",
  "montoCentimos": 15050,
  "moneda": "PEN",
  "estado": "APROBADO",
  "detalle": "Pago de venta V-001",
  "fechaCreacion": "2026-09-30T13:00:00"
}
```

> `montoCentimos=15050` representa `S/ 150.50`.

---

### 3.2 GET /api/v1/plin/pagos

Lista todos los pagos.

#### cURL

```bash
curl -i "http://localhost:8092/api/v1/plin/pagos"
```

#### Respuesta esperada

- HTTP `200 OK`
- JSON array de `PagoPlinResponseDTO`

---

### 3.3 GET /api/v1/plin/pagos/{codigoOperacion}

Busca un pago por código de operación.

#### cURL (registro de demostración existente)

```bash
curl -i "http://localhost:8092/api/v1/plin/pagos/PLIN-DEMO-001"
```

#### Respuesta esperada (si existe)

- HTTP `200 OK`
- Objeto `PagoPlinResponseDTO`

#### cURL (no encontrado)

```bash
curl -i "http://localhost:8092/api/v1/plin/pagos/PLIN-NOEXISTE"
```

#### Respuesta esperada (si no existe)

- HTTP `404 Not Found`

```json
{
  "estadoHttp": 404,
  "error": "No encontrado",
  "mensaje": "No existe una operación Plin con código: PLIN-NOEXISTE",
  "ruta": "/api/v1/plin/pagos/PLIN-NOEXISTE",
  "fecha": "2026-09-30T13:00:00"
}
```

## 4) Casos de error importantes

### 4.1 Monto inválido (no inserta registro)

#### Request

```json
{
  "numeroDestino": "988333444",
  "titular": "María Núñez",
  "montoCentimos": -1000,
  "detalle": "Prueba"
}
```

#### cURL

```bash
curl -i -X POST "http://localhost:8092/api/v1/plin/pagos" ^
  -H "Content-Type: application/json" ^
  -d "{\"numeroDestino\":\"988333444\",\"titular\":\"María Núñez\",\"montoCentimos\":-1000,\"detalle\":\"Prueba\"}"
```

#### Respuesta esperada

- HTTP `400 Bad Request`

```json
{
  "estadoHttp": 400,
  "error": "Solicitud inválida",
  "mensaje": "El monto en céntimos debe ser mayor a cero",
  "ruta": "/api/v1/plin/pagos",
  "fecha": "2026-09-30T13:00:00"
}
```

## 5) Validaciones de negocio aplicadas en el Service

- `request` obligatorio
- `numeroDestino` obligatorio y de exactamente 9 dígitos
- `titular` obligatorio
- `montoCentimos` obligatorio
- `montoCentimos > 0` (si no, responde `400`)

## 6) Arquitectura didáctica implementada

Flujo de escritura:

`HTTP/JSON -> Controller -> Request DTO -> Service -> Entity -> DAO -> JDBC -> PostgreSQL`

Flujo de lectura:

`PostgreSQL -> DAO -> Entity -> Mapper -> Response DTO -> Controller -> JSON`
