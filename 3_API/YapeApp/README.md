# YapeApp REST (Java 25 + HttpServer + JDBC)

Proyecto educativo sin Spring para exponer servicios REST de pagos Yape usando:

- Java 25
- `com.sun.net.httpserver.HttpServer`
- PostgreSQL + JDBC puro

## 1) Requisitos

- JDK 25
- Maven 3.9+
- PostgreSQL activo con:
  - Host: `localhost`
  - Puerto: `5435`
  - Base: `yape_db`
  - Usuario: `postgres`
  - Contraseña: `clave123`

## 2) Ejecutar el servicio

### IntelliJ IDEA
1. Abrir el proyecto Maven.
2. Configurar JDK 25.
3. Ejecutar `pe.edu.ulima.isw2.yape.app.YapeServiceApplication`.

### Maven
```bash
mvn clean compile
mvn exec:java
```

Salida esperada:
```text
Yape Service iniciado
http://localhost:8091/api/v1/yape/pagos
```

## 3) Base URL

`http://localhost:8091/api/v1/yape/pagos`

## 4) Endpoints disponibles

- `POST /api/v1/yape/pagos`
- `GET /api/v1/yape/pagos`
- `GET /api/v1/yape/pagos/{codigoOperacion}`

---

## 5) Pruebas REST (PowerShell con curl.exe)

> Nota: usar `curl.exe` (no alias de PowerShell).

### 5.1 Crear pago (OK - HTTP 201)

```powershell
curl.exe -i -X POST "http://localhost:8091/api/v1/yape/pagos" -H "Content-Type: application/json" -d '{"telefono":"999111222","nombreCliente":"José Pérez","monto":150.50,"descripcion":"Pago de venta V-001"}'
```

Respuesta esperada (ejemplo):
```json
{
  "codigoOperacion": "YAPE-A1B2C3D4",
  "telefono": "999111222",
  "nombreCliente": "José Pérez",
  "monto": 150.50,
  "moneda": "PEN",
  "estado": "APROBADO",
  "descripcion": "Pago de venta V-001",
  "fechaCreacion": "2026-09-30T13:10:00.123"
}
```

### 5.2 Crear pago con monto negativo (HTTP 400)

```powershell
curl.exe -i -X POST "http://localhost:8091/api/v1/yape/pagos" -H "Content-Type: application/json" -d '{"telefono":"999111222","nombreCliente":"José Pérez","monto":-10.00,"descripcion":"Prueba"}'
```

Mensaje esperado:
`"El monto debe ser mayor a cero"`

### 5.3 Crear pago con monto cero (HTTP 400)

```powershell
curl.exe -i -X POST "http://localhost:8091/api/v1/yape/pagos" -H "Content-Type: application/json" -d '{"telefono":"999111222","nombreCliente":"José Pérez","monto":0,"descripcion":"Prueba"}'
```

### 5.4 Crear pago con teléfono inválido (HTTP 400)

```powershell
curl.exe -i -X POST "http://localhost:8091/api/v1/yape/pagos" -H "Content-Type: application/json" -d '{"telefono":"99911","nombreCliente":"José Pérez","monto":20.00,"descripcion":"Prueba"}'
```

Mensaje esperado:
`"El teléfono debe contener exactamente 9 dígitos"`

### 5.5 Listar pagos (HTTP 200)

```powershell
curl.exe -i "http://localhost:8091/api/v1/yape/pagos"
```

Devuelve un arreglo JSON (vacío o con registros).

### 5.6 Buscar por código existente (HTTP 200)

Primero crea un pago y copia `codigoOperacion`, luego:

```powershell
curl.exe -i "http://localhost:8091/api/v1/yape/pagos/YAPE-A1B2C3D4"
```

### 5.7 Buscar por código inexistente (HTTP 404)

```powershell
curl.exe -i "http://localhost:8091/api/v1/yape/pagos/YAPE-NOEXISTE"
```

Mensaje esperado:
`"No existe una operación Yape con código: YAPE-NOEXISTE"`

### 5.8 Método no permitido (HTTP 405)

```powershell
curl.exe -i -X PUT "http://localhost:8091/api/v1/yape/pagos"
```

---

## 6) Estructura de error esperada

Cuando ocurre error, la API responde con:

```json
{
  "estadoHttp": 400,
  "error": "Solicitud inválida",
  "mensaje": "El monto debe ser mayor a cero",
  "ruta": "/api/v1/yape/pagos",
  "fecha": "2026-09-30T13:12:00.123"
}
```

## 7) Códigos HTTP usados

- `201`: creación exitosa
- `200`: consulta exitosa
- `400`: validación de negocio / request inválido
- `404`: operación no encontrada
- `405`: método no permitido
- `500`: error interno inesperado
