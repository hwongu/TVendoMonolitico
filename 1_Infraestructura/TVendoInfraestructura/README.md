# ISW2 – Entorno Docker para DAO y patrones

Esta versión separa cada motor/servicio de datos en un contenedor independiente.

## Servicios y puertos

| Servicio | Puerto host | Usuario administrador | Clave | Base |
|---|---:|---|---|---|
| MySQL principal | 3307 | root | clave123 | isw2_mysql |
| PostgreSQL principal | 5434 | postgres | clave123 | isw2_postgres |
| PostgreSQL Yape | 5435 | postgres | clave123 | yape_db |
| PostgreSQL Plin | 5436 | postgres | clave123 | plin_db |
| Adminer | 8080 | — | — | — |

## Esquema principal

Tanto MySQL como PostgreSQL principal crean:

- `producto`
- `venta`
- `detalle_venta`
- `pago`

Los nombres de tablas y columnas están en español. Los motores se inicializan en UTF-8/utf8mb4 para aceptar correctamente caracteres como:

- `á`, `é`, `í`, `ó`, `ú`
- `ñ`
- `ü`
- signos y texto Unicode

Se incluyen datos de ejemplo como `Cámara`, `José Pérez` y `María Núñez`.

## Sistemas externos simulados

### Yape

Contenedor: `postgres-yape`

Base: `yape_db`

Tabla:

- `pago_yape`

Puerto desde el host:

```text
5435
```

### Plin

Contenedor: `postgres-plin`

Base: `plin_db`

Tabla:

- `pago_plin`

Puerto desde el host:

```text
5436
```

Yape y Plin utilizan estructuras deliberadamente diferentes para que luego puedan exponerse mediante APIs incompatibles y adaptarse mediante el patrón Adapter.

Ejemplo:

- Yape usa `monto NUMERIC(12,2)`.
- Plin usa `monto_centimos INTEGER`.

## Levantar el laboratorio

```bash
docker compose up -d
```

Comprobar estado:

```bash
docker compose ps
```

Ver logs:

```bash
docker compose logs -f
```

## Reinicializar completamente las bases

Los scripts de `/docker-entrypoint-initdb.d` se ejecutan solamente cuando el volumen se crea por primera vez.

Si ya tenía una versión anterior del laboratorio, ejecute:

```bash
docker compose down -v --remove-orphans
```

Esto elimina los volúmenes y vuelve a crear las tablas y datos de ejemplo.

## Conexiones JDBC desde IntelliJ

### MySQL principal

```text
jdbc:mysql://localhost:3307/isw2_mysql?useUnicode=true&characterEncoding=utf8
```

Usuario:

```text
root
```

Clave:

```text
clave123
```

### PostgreSQL principal

```text
jdbc:postgresql://localhost:5434/isw2_postgres
```

Usuario:

```text
postgres
```

Clave:

```text
clave123
```

### Yape

```text
jdbc:postgresql://localhost:5435/yape_db
```

Usuario:

```text
postgres
```

Clave:

```text
clave123
```

### Plin

```text
jdbc:postgresql://localhost:5436/plin_db
```

Usuario:

```text
postgres
```

Clave:

```text
clave123
```

## Conexiones entre contenedores

Si más adelante las aplicaciones Java también se ejecutan dentro de Docker, no se debe usar `localhost`.

Se usarían los nombres de servicio:

- `mysql-main:3306`
- `postgres-main:5432`
- `postgres-yape:5432`
- `postgres-plin:5432`

## Relación con los patrones

- DAO: persistencia principal.
- Factory Method: creación de conexiones según el motor.
- Abstract Factory: familia de DAO MySQL o PostgreSQL.
- Singleton: configuración compartida, no una única conexión JDBC física.
- Adapter: integración de futuras APIs Yape y Plin.
- Facade: coordinación del flujo de venta.
- Strategy: comportamiento variable.
- Command: acciones de Caja.
- Java Collections: recorrido estándar sin implementar Iterator propio.


