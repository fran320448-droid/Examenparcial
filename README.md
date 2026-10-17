# TechServices Perú — API REST segura de consulta de clientes

Proyecto para el **Ejercicio 1**: API REST con Spring Boot 3, Java 21, Maven, MySQL (XAMPP/phpMyAdmin) y despliegue en Render.

Qué hace: expone `GET /api/clientes/{id}` protegido con **API Key + token Bearer**. Valida credenciales, estado del usuario y saldo de tokens, y **descuenta exactamente 1 token solo cuando la consulta es exitosa**.

---

## 1. Estructura del proyecto

```
techservices-api/
├── database.sql                       ← base de datos bd_techservices (importar en phpMyAdmin)
├── pom.xml
├── mvnw.cmd                           ← Maven Wrapper (no necesitas instalar Maven)
├── render.yaml                        ← config de despliegue en Render
├── postman/TechServices-API.postman_collection.json
└── src/main/java/pe/techservices/
    ├── TechservicesApiApplication.java
    ├── model/        Cliente.java, UsuarioApi.java
    ├── repository/   ClienteRepository.java, UsuarioApiRepository.java
    ├── service/      ConsultaClienteService.java (descuento atómico de tokens)
    ├── controller/   ClienteController.java
    ├── security/     ApiKeyValidator.java (validación de API Key + Bearer)
    ├── dto/          ClienteResponseDTO, ConsultaClienteResponseDTO, ErrorResponseDTO
    └── exception/    ApiException, GlobalExceptionHandler
```

---

## 2. Requisitos previos (Windows)

| Herramienta | Cómo verificar (CMD) | Si no está |
|---|---|---|
| JDK 21+ | `java -version` | Instalar un JDK de Adoptium (apachefriends no lo trae). Con el ZIP de Adoptium no se necesita admin: descomprimir y con `set PATH=...` queda listo |
| XAMPP con MySQL | Existe la carpeta `C:\xampp\mysql` | Descargar XAMPP desde apachefriends.org |
| Postman | — | Usar Postman Web (web.postman.co) o el instalador |
| Maven | No se requiere instalar: el proyecto incluye Maven Wrapper (`mvnw.cmd`) | — |

> Nota: el proyecto se compiló y probó con JDK 22 instalado, que es compatible con el objetivo Java 21 declarado en `pom.xml` (el compilador usa `--release 21`).

---

## 3. Paso a paso en tu PC

### Paso 1. Iniciar MySQL en XAMPP
- Abre el **Panel de Control de XAMPP** y pulsa **Start** en MySQL.
- Verifica que el puerto **3306** quede en verde.
- Si vas a usar phpMyAdmin, también pulsa **Start** en Apache.

### Paso 2. Crear la base de datos con phpMyAdmin
1. Abre en el navegador: `http://localhost/phpmyadmin`
2. Ve a la pestaña **Importar**.
3. Selecciona el archivo `database.sql` de este proyecto.
4. Pulsa **Continuar**. phpMyAdmin creará:
   - base de datos `bd_techservices`
   - tabla `usuarios_api` con **4 usuarios de prueba**
   - tabla `clientes` con **5 clientes ficticios**

### Paso 3. Variables de entorno (CMD, sin admin)
El proyecto lee credenciales desde variables de entorno. Para tu PC con XAMPP valen los valores por defecto (root sin contraseña) y **no necesitas configurar nada**. Si tu MySQL tiene otra contraseña, configúralo así en cada sesión de CMD:

```
set DB_URL=jdbc:mysql://localhost:3306/bd_techservices?useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=America/Lima
set DB_USERNAME=root
set DB_PASSWORD=tu_contrasena
set SERVER_PORT=8080
```

> `^` es el escape de `&` en CMD. Los valores `MI_API_KEY_2026` y `TOKEN_SEGURO_123456` son **valores de desarrollo** que ya están en `database.sql`.

### Paso 4. Compilar con Maven (usa el wrapper incluido)
```
cd C:\Users\UPSJB\proyectos\techservices-api
mvnw.cmd clean package
```
La primera vez descarga dependencias (tarda unos minutos).

### Paso 5. Ejecutar Spring Boot
```
mvnw.cmd spring-boot:run
```
Cuando veas `Started TechservicesApiApplication`, la API está en `http://localhost:8080`.

### Paso 6. Probar con Postman
1. Importa `postman/TechServices-API.postman_collection.json`.
2. El request **"1 Consulta exitosa"** ya trae los encabezados y apunta a:
   ```
   GET http://localhost:8080/api/clientes/1
   x-api-key: MI_API_KEY_2026
   Authorization: Bearer TOKEN_SEGURO_123456
   ```
3. Respuesta esperada:
   ```json
   {
     "mensaje": "Consulta realizada correctamente",
     "cliente": {
       "id": 1,
       "nombres": "Carlos",
       "apellidos": "Ramirez",
       "dni": "12345678",
       "correoElectronico": "carlos.ramirez@example.com",
       "telefono": "999888777"
     },
     "tokensRestantes": 9
   }
   ```

(falta)### Paso 7. Comprobar el descuento de tokens
- `usuario1` empieza con **10 tokens** (son 10 en `database.sql`).
- Después de una consulta exitosa: la respuesta dice `"tokensRestantes": 9`.
- Verifícalo en phpMyAdmin: `bd_techservices` → tabla `usuarios_api` → usuario1 muestra `tokens_disponibles = 9`, y en la fila se ve el descuento.
- Cada consulta exitosa resta 1. **Fallar con 404 (cliente inexistente) NO descuenta**: prueba el request 6 y vuelve a mirar la tabla, el saldo no cambia.

### Paso 8. Pruebas (cobertura de los 9 casos)
| Caso | Encabezados / condición | HTTP esperado | ¿Descuenta? |
|---|---|---|---|
| Consulta exitosa | key + Bearer válidos | 200 | Sí (−1) |
| API Key incorrecta | key mala | 401 | No |
| Token incorrecto | Bearer malo | 401 | No |
| Sin encabezados | (ninguno) | 401 | No |
| Usuario inactivo (usuario4) | key/token de usuario4 | 403 | No |
| Usuario sin tokens (usuario3) | key/token de usuario3 | 429 | No |
| Key/token de usuarios distintos | key de usuario1 + token de usuario2 | 401 | No |
| Cliente inexistente | key + Bearer válidos, id 999 | 404 | No |

> Los errores no descuentan porque detienen el flujo **antes** de actualizar la cuenta:
> la verificación de credenciales/saldo ocurre antes de buscar al cliente, y el descuadre
> del token solo ocurre tras confirmar que el cliente existe.

---

## 4. Despliegue en Render

1. **GitHub**: crea un repositorio (por ejemplo `techservices-api`) y sube el proyecto:
   ```
   cd C:\Users\UPSJB\proyectos\techservices-api
   git init
   git add .
   git commit -m "Ejercicio 1: API REST segura TechServices Peru"
   git remote add origin https://github.com/TU_USUARIO/techservices-api.git
   git push -u origin main
   ```
2. **Base de datos MySQL en la nube** (importante: XAMPP local **no es accesible** desde Render):
   - Crea una MySQL administrada, por ejemplo en **Aiven** (plan free) o **Clever Cloud**.
   - Estas bases ya vienen habilitando TLS; su "Service URI" es la que usarás como `DB_URL`.
3. **Importar `database.sql` en la nube**:
   - Con CLI del proveedor: `mysql -h TU_HOST -P PUERTO -u TU_USUARIO -p --database NOMBRE_BD < database.sql`
   - O bien, si hay editor de consultas web, copia y pega el contenido de `database.sql`.
4. **Render**: en render.com → **New + → Blueprint** → conecta el repositorio. Render lee `render.yaml` (compila con `./mvnw clean package -DskipTests` y ejecuta con el JAR). El puerto se ajusta solo: Render inyecta `PORT` y el proyecto lo usa (`server.port=${SERVER_PORT:${PORT:8080}}` en `application.properties`).
5. **Variables de entorno en Render** (Environment → Add / o en `render.yaml` marcadas `sync:false` para cargarlas desde tu panel):
   - `DB_URL` = `jdbc:mysql://HOST:PUERTO/NOMBRE_BD?useSSL=true&...` (los valores exactos los da el proveedor; la contraseña va solo en variables, nunca en el código)
   - `DB_USERNAME` y `DB_PASSWORD` = los del proveedor.

> **Este proyecto NO está desplegado todavía en Render**: crear la base en la nube y publicar el repo en GitHub son pasos manuales que tú debes ejecutar con tu cuenta. El código está listo para Blueprint.

---

## 5. Errores comunes

| Problema | Causa | Solución (CMD) |
|---|---|---|
| `Communications link failure` | MySQL apagado | XAMPP → Start en MySQL (o `C:\xampp\mysql_start.bat`) |
| `Access denied for user 'root'` | contraseña de MySQL distinta | `set DB_PASSWORD=...` antes de `mvnw.cmd spring-boot:run` |
| `Unknown database 'bd_techservices'` | no importaste database.sql | Repite el Paso 2 en phpMyAdmin |
| Puerto 8080 ocupado | otro proceso lo usa | `for /f "tokens=5" %a in ('netstat -ano ^| findstr :8080') do taskkill /PID %a /F` |
| `java` no se reconoce | JDK fuera del PATH de esa sesión | `set PATH=C:\Program Files\Java\jdk-22\bin;%PATH%` |
| `mvnw.cmd` falla la primera vez | descarga del wrapper interrumpida | Verifica Internet y vuelve a ejecutar |
| `401` con datos aparentemente correctos | token con comillas o doble espacio en Postman | `Authorization` debe ser exactamente `Bearer TOKEN_SEGURO_123456` (un espacio, sin comillas) |
| `429` en todas tus pruebas | te quedaste sin saldo con usuario1 | En phpMyAdmin, edita `tokens_disponibles` de usuario1 p. ej. a 10 |

---

## 6. Seguridad implementada (resumen para sustentar)

1. `x-api-key` obligatorio → 401 si falta.
2. `Authorization: Bearer ...` obligatorio → 401 si falta o formato incorrecto.
3. La API Key y el token deben coincidir con **el mismo usuario** (consulta `findByApiKeyAndTokenAutenticacion`) → 401 si no coinciden.
4. Usuario `estado = 'activo'` → 403 si está inactivo.
5. `tokens_disponibles >= 1` → 429 si no tiene saldo.
6. Cliente debe existir → 404 si no existe (y aquí **NO descuenta**).
7. El descuento es **atómico y consistente**: `UPDATE ... SET tokens_disponibles = tokens_disponibles - 1 WHERE id = ? AND tokens_disponibles = saldo_esperado` dentro de una transacción. Si otro proceso descuenta primero, la fila no coincide y la petición recibe 429, evitando así consumos duplicados concurrentes.
8. Errores internos → 500 con mensaje genérico "Error interno del servidor" (sin stacktrace ni datos sensibles en la respuesta).
9. Ninguna credencial va en el código: todo por variables de entorno.
