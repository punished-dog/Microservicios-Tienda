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
- Spring HATEOAS (usuarios y productos)
- Datafaker (usuarios y productos)
- JUnit 5 + Mockito
- Docker

## Datos de prueba con Faker

Los servicios `usuarios` y `productos` tienen un endpoint que genera datos aleatorios para no tener que crearlos uno por uno:

```
POST /api/usuarios/generar/5
POST /api/productos/generar/5
```

El número al final es la cantidad de registros a crear.

## Ejecución local (IntelliJ)

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

## Ejecución local con Docker

Con Docker Desktop abierto:

```bash
docker-compose up --build
```

Esto compila los 12 servicios, levanta un MySQL con las 10 bases (usando `init.sql`) y registra todo en Eureka. Se revisa igual en http://localhost:8761.

Para bajar todo:

```bash
docker-compose down
```

## Ejecución remota (Render)

El sistema también está desplegado en Render, con la base de datos en MySQL online (Aiven).

- Eureka: https://eureka-server-sh1l.onrender.com
- Gateway: https://gateway-xxex.onrender.com

Todas las peticiones remotas se hacen contra el Gateway, igual que en local pero con esta URL en vez de `localhost:9090`, por ejemplo:

```
https://gateway-xxex.onrender.com/api/usuarios
```

Para volver a desplegar desde cero: el `render.yaml` en la raíz del proyecto configura los servicios. En Render se crea un Blueprint apuntando a este repositorio, y se completan las variables de entorno (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `EUREKA_URL`) con los datos de la base MySQL online.

El plan gratuito de Render apaga los servicios tras un rato sin uso, así que la primera petición después de eso puede demorar unos segundos en responder.

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

## Postman

La colección `Tienda-Microservicios.postman.json` trae las variables `gateway` y `eureka` apuntando a local. Para probar contra Render, se reemplaza el valor de `gateway` por el de la variable `gateway_render` que también viene incluida.

## Pruebas

Cada servicio tiene sus pruebas en `src/test`. Se corren con:

```bash
mvn test
```

Usan H2 (base en memoria), así que no necesitan MySQL prendido.
