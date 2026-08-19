# Business-Service (Resource Server - OAuth 2.0 / JWT)

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)
![Spring Security](https://img.shields.io/badge/Spring%20Security-OAuth2%20Resource%20Server-red.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)

Microservicio de negocio (**Resource Server**) desarrollado en **Java 21** y **Spring Boot 3**. Protege sus endpoints de negocio mediante el estándar **OAuth 2.0 / JWT**, validando la autenticidad y firma criptográfica de los tokens emitidos por el **Auth-Service** sin gestionar contraseñas localmente.

---

## 📋 Tabla de Contenidos
- [Características](#-características)
- [Arquitectura y Tecnologías](#-arquitectura-y-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración](#-configuración)
- [Instalación y Ejecución Local](#-instalación-y-ejecución-local)
- [Ejecución con Docker](#-ejecución-con-docker)
- [API Reference & Documentación de Endpoints](#-api-reference--documentación-de-endpoints)
- [Despliegue en AWS EC2](#-despliegue-en-aws-ec2)
- [Solución de Problemas](#-solución-de-problemas)

---

## 🚀 Características
- **Seguridad Descentralizada:** Actúa como Resource Server validando tokens JWT en cada solicitud.
- **Filtro de Seguridad Personalizado (`SecurityFilterChain`):**
  - Endpoint libre `/api/v1/public` para comprobación de disponibilidad sin token.
  - Endpoints protegidos `/api/v1/status` y `/api/v1/saludo` que exigen cabecera `Authorization: Bearer <TOKEN>`.
- **Extracción de Identidad:** Inyección dinámica del objeto `Jwt` mediante la anotación `@AuthenticationPrincipal`.
- **Contenerización Multietapa:** Dockerfile optimizado basado en Alpine Linux.

---

## 🛠️ Arquitectura y Tecnologías
- **Lenguaje:** Java 21 (JDK 21)
- **Framework:** Spring Boot 3.x
- **Seguridad:** Spring Security (`spring-boot-starter-oauth2-resource-server`)
- **Decodificador JWT:** Nimbus JWT Decoder (`NimbusJwtDecoder`)
- **Build Tool:** Maven 3.9+
- **Contenedores:** Docker (Alpine JRE 21)

---

## 📂 Estructura del Proyecto

```text
business-service/
├── Dockerfile
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/cloud/actividad/
    │   │       ├── ActividadApplication.java
    │   │       ├── controller/
    │   │       │   └── StatusController.java
    │   │       └── security/
    │   │           └── SecurityConfig.java
    │   └── resources/
    │       └── application.yaml
    └── test/
```

---

## ⚙️ Configuración

El servicio utiliza el archivo `src/main/resources/application.yaml` para configurar el puerto interno y la clave secreta de decodificación:

```yaml
server:
  port: 8081

spring:
  application:
    name: actividad

jwt:
  secret: EstaEsUnaClaveSuperSeguraDe32Caracteres123!
```

> 🔑 **Clave Secreta Compartida:** La variable `jwt.secret` debe coincidir exactamente con la definida en el `Auth-Service`. Con esta clave, NimbusJwtDecoder valida la firma criptográfica HMAC-SHA256 sin comunicarse directamente con el servidor de autenticación en cada petición.

---

## 💻 Requisitos Previos
- **Java JDK 21** o superior instalado.
- **Maven 3.9+** (o el wrapper de Maven `./mvnw`).
- **Docker** e **Engine Docker Daemon** activos.
- Un token JWT válido previamente generado por el **Auth-Service**.

---

## 🔧 Instalación y Ejecución Local

### 1. Clonar el Repositorio
```bash
git clone https://github.com/TU_USUARIO/business-service.git
cd business-service
```

### 2. Compilar el Proyecto
```bash
mvn clean package -DskipTests
```

### 3. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```
El microservicio iniciará en el puerto `8081` (`http://localhost:8081`).

---

## 🐳 Ejecución con Docker

### 1. Construir la Imagen Docker
```bash
docker build -t business-service:1.0.0 .
```

### 2. Ejecutar el Contenedor
```bash
docker run -d -p 8081:8081 --name business-service business-service:1.0.0
```

### 3. Verificar Estado del Contenedor
```bash
docker ps
```
Deberías ver ambos contenedores corriendo simultáneamente:
```text
CONTAINER ID   IMAGE                    PORTS                    NAMES
a1b2c3d4e5f6   auth-service:1.0.0       0.0.0.0:8080->8080/tcp   auth-service
f6e5d4c3b2a1   business-service:1.0.0   0.0.0.0:8081->8081/tcp   business-service
```

---

## 📡 API Reference & Documentación de Endpoints

### 1. 🟢 Endpoint Público (Sin Autenticación)
- **Endpoint:** `GET /api/v1/public`
- **Headers:** Ninguno requerido.
- **Respuesta (`200 OK`):**
  ```json
  {
    "mensaje": "endpoint sin validacion"
  }
  ```

---

### 2. 🔒 Endpoint Protegido: Obtener Estado del Servicio
- **Endpoint:** `GET /api/v1/status`
- **Headers:** 
  - `Authorization: Bearer <JWT_TOKEN>`
- **Respuesta Exitosa (`200 OK`):**
  ```json
  {
    "status": "UP",
    "mensaje": "Microservicio activo y funcionando",
    "usuarioAutenticado": "admin"
  }
  ```
- **Respuesta sin Token / Token Inválido (`401 Unauthorized`):**
  ```json
  {
    "status": 401,
    "error": "Unauthorized"
  }
  ```

---

### 3. 🔒 Endpoint Protegido: Saludar Usuario
- **Endpoint:** `POST /api/v1/saludo`
- **Headers:** 
  - `Content-Type: application/json`
  - `Authorization: Bearer <JWT_TOKEN>`
- **Request Body Ejemplo:**
  ```json
  {
    "nombre": "Carlos"
  }
  ```
- **Respuesta Exitosa (`200 OK`):**
  ```json
  {
    "mensaje": "Hola Carlos",
    "usuarioAutenticado": "admin"
  }
  ```

---

## 🧪 Pruebas Rápidas con `curl`

#### Probar Endpoint Libre
```bash
curl -i -X GET http://localhost:8081/api/v1/public
```

#### Probar Endpoint Protegido (Rechazo sin Token)
```bash
curl -i -X GET http://localhost:8081/api/v1/status
```

#### Probar Endpoint Protegido (Acceso Autorizado)
```bash
TOKEN="pega_aqui_tu_jwt"

curl -X GET http://localhost:8081/api/v1/status   -H "Authorization: Bearer $TOKEN"
```

#### Probar Endpoint POST Saludo
```bash
curl -X POST http://localhost:8081/api/v1/saludo   -H "Content-Type: application/json"   -H "Authorization: Bearer $TOKEN"   -d '{"nombre": "Maria"}'
```

---

## ☁️ Despliegue en AWS EC2

1. Conectarse a la instancia EC2 vía SSH:
   ```bash
   ssh -i "tu-clave.pem" ubuntu@<IP_PUBLICA_EC2>
   ```
2. Clonar el repositorio y construir la imagen:
   ```bash
   git clone https://github.com/TU_USUARIO/business-service.git
   cd business-service
   docker build -t business-service:1.0.0 .
   ```
3. Ejecutar el contenedor asignando el puerto 8081:
   ```bash
   docker run -d -p 8081:8081 --name business-service business-service:1.0.0
   ```
4. Configurar el **Security Group** de AWS para autorizar tráfico entrante en el puerto **TCP 8081**.

---

## ❓ Solución de Problemas

| Problema | Causa Posible | Solución |
| :--- | :--- | :--- |
| **Error `401 Unauthorized` usando token válido** | La clave `jwt.secret` no es idéntica a la del `Auth-Service`. | Verificar que `jwt.secret` en ambos repositorios sea exactamente el mismo string. |
| **Error de importación en `Keys`** | Falta la librería JJWT en el `pom.xml`. | Utilizar la implementación nativa con `SecretKeySpec` en `SecurityConfig.java` o agregar las dependencias de `jjwt` al `pom.xml`. |
| **Error `403 Forbidden`** | La petición no incluye la cabecera `Authorization: Bearer <TOKEN>`. | Asegurarse de seleccionar **Bearer Token** en Postman e incluir el prefijo `Bearer` en el header. |
