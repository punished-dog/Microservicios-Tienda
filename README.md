# Tienda de Microservicios

Proyecto de una tienda electrónica hecho con Spring Boot y arquitectura de microservicios. Cada servicio se encarga de una parte del negocio (usuarios, productos, pedidos, etc.), tiene su propia base de datos y se comunica con los demás por REST. Todo entra por un API Gateway y los servicios se registran en Eureka.

## Integrantes
- Andres Vergara
- Joaquin Rodriguez

## Microservicios

| Servicio | Puerto | Base de datos |
|---|---|---|
| eureka-server | 8761 | - |
| gateway | 9090 | - |
| usuarios | 8080 | usuarios_db |
| productos | 8081 | productos_db |
| inventario | 8082 | inventario_db |
| resenas | 8083 | resenas_db |
| carrito | 8084 | carrito_db |
| promociones | 8085 | promociones_db |
| pedidos | 8086 | pedidos_db |
| envios | 8087 | envios_db |
| pagos | 8088 | pagos_db |
| soporte | 8089 | soporte_db |

## Stack

- Java 21
- Spring Boot 3.3.4 / Spring Cloud 2023.0.3
- Eureka + Spring Cloud Gateway
- JPA / Hibernate + MySQL
- Bean Validation
- SLF4J
- Swagger (springdoc)
- JUnit 5 + Mockito
- Docker

## Cómo correrlo en local (IntelliJ)

Hace falta Java 21, Maven y MySQL corriendo en localhost:3306 (usuario `root`, clave `system`).

Primero hay que crear las bases de datos:

```sql
CREATE DATABASE usuarios_db;
CREATE DATABASE productos_db;
CREATE DATABASE inventario_db;
CREATE DATABASE resenas_db;
CREATE DATABASE carrito_db;
CREATE DATABASE promociones_db;
CREATE DATABASE pedidos_db;
CREATE DATABASE envios_db;
CREATE DATABASE pagos_db;
CREATE DATABASE soporte_db;
```

Después se levantan los servicios en este orden:

1. eureka-server (hay que arrancarlo primero)
2. gateway
3. usuarios y productos
4. el resto

Para revisar que todos quedaron registrados: http://localhost:8761

Todo se prueba a través del gateway en el puerto 9090, por ejemplo `http://localhost:9090/api/usuarios`.

## Cómo correrlo con Docker

Con Docker Desktop abierto, primero se compila cada servicio:

```bash
cd usuarios && mvn package -DskipTests && cd ..
```

(y así con cada uno)

Y después se levanta todo junto:

```bash
docker-compose up --build
```

El docker-compose arma un MySQL, crea las 10 bases con el `init.sql`, y levanta Eureka, el gateway y los 10 servicios. Igual que antes, se revisa en http://localhost:8761.

Para bajar todo:

```bash
docker-compose down
```

## Despliegue en Render

El `render.yaml` tiene la configuración para subir los servicios a Render. La base de datos se usa online (por ejemplo con Aiven, MySQL gratis).

Pasos:

1. Crear una base MySQL en https://console.aiven.io y anotar host, puerto, base, usuario y contraseña.
2. Subir el proyecto a GitHub.
3. En Render: New → Blueprint → elegir el repo. Detecta el `render.yaml`.
4. Completar las variables de cada servicio con los datos de la base:
   - `SPRING_DATASOURCE_URL` → `jdbc:mysql://HOST:PUERTO/BASE?ssl-mode=REQUIRED`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `EUREKA_URL` → la url del eureka en Render con `/eureka/` al final
5. Aplicar y esperar a que queden arriba.

Con el plan gratis los servicios se apagan si no se usan por un rato, así que la primera llamada después puede demorar un poco.

## Rutas del Gateway

```
/api/usuarios/**
/api/productos/**
/api/inventario/**
/api/resenas/**
/api/carrito/**
/api/promociones/**
/api/pedidos/**
/api/envios/**
/api/pagos/**
/api/soporte/**
```

## Swagger

Cada servicio tiene su documentación en `/swagger-ui.html`. Por ejemplo:

- usuarios: http://localhost:8080/swagger-ui.html
- productos: http://localhost:8081/swagger-ui.html
- pedidos: http://localhost:8086/swagger-ui.html

(el resto igual, cambiando el puerto)

## Comunicación entre servicios

Algunos servicios llaman a otros para validar datos antes de guardar:

- pedidos consulta usuarios y productos
- pagos consulta pedidos
- envios consulta pedidos y usuarios
- carrito consulta productos
- resenas consulta usuarios y productos
- promociones consulta productos
- soporte consulta usuarios
- inventario consulta productos

Para verlo funcionando hay endpoints como `GET /api/pedidos/1/usuario`, que trae los datos del usuario desde el servicio de usuarios.

## Pruebas

Cada servicio tiene sus pruebas en `src/test`. Se corren con:

```bash
mvn test
```

Usan H2 (base en memoria), así que no necesitan MySQL prendido.
