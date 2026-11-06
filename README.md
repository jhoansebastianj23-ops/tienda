# TiendaApp

API REST de una tienda en línea con **JWT** y **roles** (ADMIN, CLIENTE, VENDEDOR).
Spring Boot + JPA + MySQL. La seguridad está hecha a mano (filtro + interceptor), sin Spring Security completo.

- Puerto: `9090`
- Ruta base: `http://localhost:9090/api/v1/tienda`

## 1. Requisitos

- Java 17 o superior
- MySQL corriendo en `localhost:3306`
- Postman (para la colección)

## 2. Base de datos

1. Abre MySQL Workbench (o la consola de MySQL).
2. Ejecuta el script `tienda.sql` completo. Crea `tienda_db`, las tablas, los 3 roles y el usuario ADMIN.
3. Revisa en `src/main/resources/application.yaml` que el usuario y la contraseña de MySQL sean los tuyos (por defecto `root` / `1234`).

## 3. Variables de entorno

La app lee el secreto y la expiración del JWT desde variables de entorno:

| Variable | Qué es | Ejemplo |
| --- | --- | --- |
| `ENV_JWT_SECRET_KEY` | Secreto para firmar el token (base64, 32 bytes) | lo generas abajo |
| `ENV_JWT_TOKEN_EXPIRATION` | Vida del token en milisegundos (10 min) | `600000` |

**Generar el secreto**

Linux / Mac / Git Bash:

```
openssl rand -base64 32
```

Windows PowerShell (si no tienes openssl):

```
$b = New-Object byte[] 32; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
```

**Definir las variables (solo para esa terminal)**

PowerShell:

```
$env:ENV_JWT_SECRET_KEY="PEGA_AQUI_TU_SECRETO"
$env:ENV_JWT_TOKEN_EXPIRATION="600000"
```

CMD:

```
set ENV_JWT_SECRET_KEY=PEGA_AQUI_TU_SECRETO
set ENV_JWT_TOKEN_EXPIRATION=600000
```

Linux / Mac:

```
export ENV_JWT_SECRET_KEY="PEGA_AQUI_TU_SECRETO"
export ENV_JWT_TOKEN_EXPIRATION="600000"
```

Si corres desde el IDE (IntelliJ, Eclipse, VS Code), agrégalas en la configuración de ejecución de `TiendaApplication` (Environment variables).

## 4. Correr el proyecto

En la misma terminal donde definiste las variables:

```
./mvnw spring-boot:run        (Linux / Mac)
mvnw.cmd spring-boot:run      (Windows)
```

Debe quedar escuchando en el puerto 9090.

> El test `TiendaApplicationTests` arranca la app completa, así que también necesita MySQL encendido y las variables definidas.

## 5. Credenciales del ADMIN inicial

| Campo | Valor |
| --- | --- |
| username | `admin` |
| email | `admin@tienda.com` |
| password | `Admin123!` |

## 6. Colección de Postman

1. En Postman: **Import** y elige `TiendaApp.postman_collection.json`.
2. Abre la colección, pestaña **Variables**, y pega tu secreto en `jwtSecret` (solo se usa para generar el token expirado de la evidencia).
3. Ejecuta las carpetas en orden: **1. Auth**, **2. Users**, **3. Categories**, **4. Products**, **5. Orders**, **6. Evidencias** y al final **7. Eliminar**.
4. Los tokens se guardan solos en variables (`adminToken`, `clienteToken`, `vendedorToken`, `vendedor2Token`).

## 7. Evidencias

Todas están en la carpeta **6. Evidencias** de la colección:

| Caso | Qué hacer | Resultado |
| --- | --- | --- |
| Login correcto | `POST /auth/login` con admin | 200 y un `jwt` |
| Sin token | `GET /users/me` sin header Authorization | 401 |
| Token expirado | `GET /users/me` con un token vencido | 401 |
| Rol no permitido | `GET /users` con el token de CLIENTE | 403 |
| Vendedor editando producto ajeno | `PUT /products/{id}` con el token de `vendedor2` sobre un producto de `vendedor1` | 403 |
| Pedido sin stock | `POST /orders` con `quantity` enorme | 400 |

**Token expirado (otra forma, sin Postman script):** cierra la app, pon `ENV_JWT_TOKEN_EXPIRATION=5000`, arráncala, haz login, espera 6 segundos y llama `GET /users/me` con ese token. Devuelve 401. Después vuelve a `600000`.

## 8. Endpoints (rutas relativas a `/api/v1/tienda`)

| Método | Ruta | Quién |
| --- | --- | --- |
| POST | `/auth/register` | Público (siempre CLIENTE) |
| POST | `/auth/login` | Público |
| POST | `/auth/refreshtoken` | Público (token en el header) |
| GET | `/users/me` | ADMIN, CLIENTE, VENDEDOR |
| GET / POST | `/users` | ADMIN |
| PATCH | `/users/{id}/role` | ADMIN |
| PATCH | `/users/{id}/status` | ADMIN |
| GET | `/categories` | ADMIN, CLIENTE, VENDEDOR |
| POST | `/categories` | ADMIN |
| DELETE | `/categories/{id}` | ADMIN |
| GET | `/products`, `/products/{id}` | Público |
| GET | `/products/mine` | VENDEDOR |
| POST | `/products` | VENDEDOR |
| PUT / DELETE | `/products/{id}` | VENDEDOR (solo suyos), ADMIN |
| POST | `/orders` | CLIENTE |
| GET | `/orders/mine` | CLIENTE |
| GET | `/orders/sales` | VENDEDOR |
| GET | `/orders` | ADMIN |
| PATCH | `/orders/{id}/status` | VENDEDOR (solo sus productos), ADMIN |
| PATCH | `/orders/{id}/cancel` | CLIENTE (solo PENDIENTE) |
