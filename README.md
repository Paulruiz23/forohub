# 💬 ForoHub API

[![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange?logo=mysql)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth0-purple)](https://jwt.io/)
![Estado](https://img.shields.io/badge/estado-completado-success)

---

## 🚀 Descripción

**ForoHub** es una API REST completa para gestionar un foro de discusión, desarrollada con **Spring Boot 3** como parte del Challenge de **Alura Latam**. 

Permite crear tópicos, responder preguntas, marcar soluciones, registrar usuarios y gestionar permisos con autenticación JWT. Implementa una **arquitectura en capas** (Controller → Service → Repository) siguiendo principios SOLID y buenas prácticas de desarrollo.

Incluye **autorización granular por propiedad**, **excepciones personalizadas** y documentación interactiva con **Swagger/OpenAPI**.

---

## 🛠 Funcionalidades

### **Autenticación y Seguridad**
* ✅ Registro de usuarios con encriptación BCrypt
* ✅ Autenticación JWT (token válido por 1 hora)
* ✅ Roles: `USER` y `ADMIN`
* ✅ Autorización granular: solo el autor o ADMIN pueden modificar recursos
* ✅ Autor obtenido automáticamente del JWT (sin falsificación posible)
* ✅ Bloqueo/desbloqueo de usuarios (solo ADMIN)
* ✅ Endpoints protegidos con Spring Security

### **Gestión de Tópicos**
* ✅ CRUD completo (Crear, Listar, Actualizar, Eliminar)
* ✅ Listado paginado y ordenado por fecha
* ✅ Validación de duplicados (título + mensaje)
* ✅ Status automático: `NO_RESPONDIDO` → `NO_SOLUCIONADO` → `SOLUCIONADO`
* ✅ Solo el autor o ADMIN pueden modificar/eliminar tópicos

### **Gestión de Respuestas**
* ✅ CRUD completo de respuestas a tópicos
* ✅ Marcar respuesta como solución (solo autor del tópico o ADMIN)
* ✅ Solo una solución por tópico
* ✅ Actualización automática del status del tópico
* ✅ Solo el autor o ADMIN pueden modificar/eliminar respuestas

### **Manejo de Errores**
* ✅ Excepciones personalizadas específicas para cada dominio
* ✅ Manejo centralizado con `@RestControllerAdvice`
* ✅ Respuestas HTTP consistentes y descriptivas
* ✅ Códigos de estado apropiados (404, 400, 401, 403)

### **Documentación**
* ✅ Swagger UI interactivo con ejemplos claros
* ✅ Parámetros de paginación documentados
* ✅ Prueba de endpoints desde el navegador
* ✅ Autenticación JWT integrada en Swagger

---

## 🏗️ Arquitectura

El proyecto implementa una **arquitectura en capas** siguiendo el patrón MVC y principios SOLID:
```
┌─────────────────────────────────────────┐
│          Controller Layer               │  ← Maneja HTTP requests/responses
│  (TopicoController, UsuarioController)  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Service Layer                 │  ← Lógica de negocio y validaciones
│  (TopicoService, AutorizacionService)   │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Repository Layer                │  ← Acceso a datos
│  (TopicoRepository, UsuarioRepository)  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│              Database                   │  ← MySQL
│            (ForoHub DB)                 │
└─────────────────────────────────────────┘
```

**Responsabilidades:**
- **Controller**: Recibe requests HTTP, valida entrada, delega a Service, retorna responses
- **Service**: Contiene lógica de negocio, validaciones de permisos, orquestación, transacciones
- **Repository**: Acceso a datos, queries JPA

---

## 🔒 Sistema de Autorización

### **Reglas de Permisos**

| Acción | Requisito |
|--------|-----------|
| **Crear tópico/respuesta** | Usuario autenticado (autor del JWT) ✅ |
| **Ver tópicos/respuestas** | Usuario autenticado ✅ |
| **Actualizar tópico** | Ser el **autor** del tópico O **ADMIN** 🔒 |
| **Eliminar tópico** | Ser el **autor** del tópico O **ADMIN** 🔒 |
| **Actualizar respuesta** | Ser el **autor** de la respuesta O **ADMIN** 🔒 |
| **Eliminar respuesta** | Ser el **autor** de la respuesta O **ADMIN** 🔒 |
| **Marcar solución** | Ser el **autor del tópico** O **ADMIN** 🔒 |

### **Características de Seguridad**

✅ **Autor automático**: Se obtiene del JWT, imposible de falsificar  
✅ **Validación por propiedad**: Solo el dueño del recurso puede modificarlo  
✅ **Permisos de ADMIN**: Pueden realizar cualquier acción  
✅ **Soft delete**: Usuarios bloqueados no eliminados permanentemente  
✅ **Encriptación BCrypt**: Contraseñas nunca en texto plano  

---

## 🚨 Manejo de Errores

El proyecto implementa un **sistema robusto de manejo de errores** con excepciones personalizadas para cada dominio.

### **Excepciones Personalizadas**

<details>
<summary>Click para ver todas las excepciones</summary>

| Excepción | Código HTTP | Descripción |
|-----------|-------------|-------------|
| `TopicoNotFoundException` | 404 | Tópico no encontrado |
| `TopicoDuplicadoException` | 400 | Tópico duplicado (mismo título y mensaje) |
| `UsuarioNotFoundException` | 404 | Usuario no encontrado |
| `CursoNotFoundException` | 404 | Curso no encontrado |
| `RespuestaNotFoundException` | 404 | Respuesta no encontrada |
| `PerfilNotFoundException` | 404 | Perfil/rol no encontrado |
| `EmailDuplicadoException` | 400 | Email ya registrado |
| `UsuarioBloqueadoException` | 400 | Usuario ya bloqueado |
| `UsuarioActivoException` | 400 | Usuario ya activo |
| `SolucionDuplicadaException` | 400 | Tópico ya tiene solución |
| `AccesoDenegadoException` | 403 | Sin permisos para realizar la acción |
| `BadCredentialsException` | 401 | Credenciales inválidas |
| `MethodArgumentNotValidException` | 400 | Errores de validación de campos |

</details>

### **Ejemplos de Respuestas de Error**

**Tópico no encontrado (404):**
```json
{
  "campo": null,
  "error": "Tópico con ID 999 no encontrado"
}
```

**Sin permisos (403):**
```json
{
  "campo": "permiso",
  "error": "Solo el autor del tópico o un administrador pueden realizar esta acción"
}
```

**Validación de campos (400):**
```json
[
  {
    "campo": "titulo",
    "error": "El título es obligatorio"
  },
  {
    "campo": "email",
    "error": "El email debe ser válido"
  }
]
```

**Tópico duplicado (400):**
```json
{
  "campo": "topico",
  "error": "Ya existe un tópico con el mismo título y mensaje"
}
```

**Credenciales inválidas (401):**
```json
{
  "campo": null,
  "error": "Credenciales inválidas"
}
```

---

## 📁 Estructura del Proyecto

<details>
<summary>Click para expandir</summary>

```
forohub/
│
├─ src/main/java/com/paulruiz/forohub/
│   │
│   ├─ controller/                        # Capa de Presentación
│   │   ├─ AutenticacionController.java  # Login y generación JWT
│   │   ├─ UsuarioController.java        # Registro y gestión de usuarios
│   │   ├─ TopicoController.java         # CRUD de tópicos
│   │   └─ RespuestaController.java      # CRUD de respuestas
│   │
│   ├─ service/                           # Capa de Lógica de Negocio
│   │   ├─ TopicoService.java            # Lógica de negocio de tópicos
│   │   ├─ RespuestaService.java         # Lógica de negocio de respuestas
│   │   ├─ UsuarioService.java           # Lógica de negocio de usuarios
│   │   └─ AutorizacionService.java      # Validación de permisos
│   │
│   ├─ repository/                        # Capa de Acceso a Datos
│   │   ├─ TopicoRepository.java
│   │   ├─ RespuestaRepository.java
│   │   ├─ UsuarioRepository.java
│   │   ├─ CursoRepository.java
│   │   └─ PerfilRepository.java
│   │
│   ├─ dto/                               # Data Transfer Objects
│   │   ├─ DatosAutenticacion.java
│   │   ├─ DatosJWT.java
│   │   ├─ RegistroUsuarioDTO.java
│   │   ├─ DetalleUsuarioDTO.java
│   │   ├─ TopicoDTO.java               # Sin autorId
│   │   ├─ ActualizarTopicoDTO.java
│   │   ├─ DetalleTopicoDTO.java
│   │   ├─ RespuestaDTO.java            # Sin autorId
│   │   ├─ ActualizarRespuestaDTO.java
│   │   └─ DetalleRespuestaDTO.java
│   │
│   ├─ model/                             # Entidades JPA
│   │   ├─ Usuario.java
│   │   ├─ Topico.java
│   │   ├─ Respuesta.java
│   │   ├─ Curso.java
│   │   ├─ Perfil.java
│   │   └─ StatusTopico.java
│   │
│   ├─ infra/                             # Infraestructura
│   │   ├─ security/
│   │   │   ├─ SecurityConfigurations.java
│   │   │   ├─ SecurityFilter.java
│   │   │   ├─ TokenService.java
│   │   │   └─ AutenticacionService.java
│   │   │
│   │   ├─ errores/                       # Excepciones Personalizadas
│   │   │   ├─ TratadorDeErrores.java    # Handler global
│   │   │   ├─ TopicoNotFoundException.java
│   │   │   ├─ TopicoDuplicadoException.java
│   │   │   ├─ UsuarioNotFoundException.java
│   │   │   ├─ CursoNotFoundException.java
│   │   │   ├─ RespuestaNotFoundException.java
│   │   │   ├─ PerfilNotFoundException.java
│   │   │   ├─ EmailDuplicadoException.java
│   │   │   ├─ UsuarioBloqueadoException.java
│   │   │   ├─ UsuarioActivoException.java
│   │   │   ├─ SolucionDuplicadaException.java
│   │   │   ├─ AccesoDenegadoException.java
│   │   │   └─ EntityNotFoundException.java  (fallback)
│   │   │
│   │   └─ springdoc/
│   │       └─ SpringDocConfigurations.java
│   │
│   └─ ForohubApplication.java
│
├─ src/main/resources/
│   ├─ application.properties
│   └─ db/migration/
│       ├─ V1__create-table-usuarios.sql
│       ├─ V2__create-table-perfiles.sql
│       ├─ V3__create-table-cursos.sql
│       ├─ V4__create-table-topicos.sql
│       ├─ V5__create-table-respuestas.sql
│       ├─ V6__insert-usuario-test.sql
│       └─ V7__add-admin-role.sql
│
├─ pom.xml
├─ .gitignore
└─ README.md
```

</details>

---

## ⚡ Instalación y Uso

### **1. Requisitos Previos**
* ☕ Java 21+
* 🗄️ MySQL 8.0+
* 📦 Maven 3.8+

### **2. Clonar el Repositorio**
```bash
git clone https://github.com/Paulruiz23/forohub.git
cd forohub
```

### **3. Configurar Base de Datos**

Crear la base de datos en MySQL:
```sql
CREATE DATABASE forohub;
```

Actualizar credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/forohub
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

### **4. Compilar y Ejecutar**
```bash
# Instalar dependencias
mvn clean install

# Ejecutar aplicación
mvn spring-boot:run
```

La API estará disponible en: **http://localhost:8080**

### **5. Acceder a Swagger UI**
Documentación interactiva: **http://localhost:8080/swagger-ui.html**

---

## 🖥 Endpoints Principales

<details>
<summary>Click para ver todos los endpoints</summary>

### **🔐 Autenticación (Público)**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/login` | Autenticar usuario y obtener JWT |
| POST | `/usuarios` | Registrar nuevo usuario |

### **👤 Usuarios (Requiere JWT)**
| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/usuarios/{id}` | Detalle de usuario | Any |
| DELETE | `/usuarios/{id}` | Bloquear usuario | ADMIN |
| PUT | `/usuarios/{id}/desbloquear` | Desbloquear usuario | ADMIN |

### **📝 Tópicos (Requiere JWT)**
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| POST | `/topicos` | Crear tópico | Any (autor del JWT) |
| GET | `/topicos` | Listar tópicos (paginado) | Any |
| GET | `/topicos/{id}` | Detalle de tópico | Any |
| GET | `/topicos/{id}/respuestas` | Respuestas de un tópico | Any |
| PUT | `/topicos/{id}` | Actualizar tópico | Autor o ADMIN |
| DELETE | `/topicos/{id}` | Eliminar tópico | Autor o ADMIN |

**Parámetros de paginación:**
- `page` - Número de página (default: 0)
- `size` - Elementos por página (default: 10)
- `sort` - Ordenamiento (default: `fechaCreacion,desc`)

**Ejemplos de sort:**
- `fechaCreacion,desc` - Más recientes primero
- `titulo,asc` - Alfabéticamente
- `status,asc` - Por status

### **💬 Respuestas (Requiere JWT)**
| Método | Endpoint | Descripción | Permisos |
|--------|----------|-------------|----------|
| POST | `/respuestas` | Crear respuesta | Any (autor del JWT) |
| GET | `/respuestas/{id}` | Detalle de respuesta | Any |
| PUT | `/respuestas/{id}` | Actualizar respuesta | Autor o ADMIN |
| DELETE | `/respuestas/{id}` | Eliminar respuesta | Autor o ADMIN |
| PUT | `/respuestas/{id}/marcar-solucion` | Marcar como solución | Autor del tópico o ADMIN |

</details>

---

## 🧪 Ejemplo de Uso

<details>
<summary>Click para ver ejemplos completos</summary>

### **1. Registrar Usuario**
```bash
POST http://localhost:8080/usuarios
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@forohub.com",
  "contrasena": "password123"
}
```

### **2. Login**
```bash
POST http://localhost:8080/login
Content-Type: application/json

{
  "email": "juan@forohub.com",
  "contrasena": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### **3. Crear Tópico**
```bash
POST http://localhost:8080/topicos
Authorization: Bearer eyJhbGci...
Content-Type: application/json

{
  "titulo": "¿Cómo usar Spring Security?",
  "mensaje": "Necesito ayuda con JWT",
  "cursoId": 1
}
```

**⚠️ Nota importante:** El autor se obtiene **automáticamente del token JWT**. No es necesario enviar `autorId`.

### **4. Listar Tópicos (con paginación)**
```bash
# Lista por defecto (10 elementos, ordenados por fecha desc)
GET http://localhost:8080/topicos
Authorization: Bearer eyJhbGci...

# Personalizar paginación
GET http://localhost:8080/topicos?page=0&size=5&sort=titulo,asc
Authorization: Bearer eyJhbGci...
```

### **5. Crear Respuesta**
```bash
POST http://localhost:8080/respuestas
Authorization: Bearer eyJhbGci...
Content-Type: application/json

{
  "mensaje": "Aquí está la solución...",
  "topicoId": 1
}
```

**⚠️ Nota importante:** El autor se obtiene **automáticamente del token JWT**. No es necesario enviar `autorId`.

### **6. Marcar como Solución**
```bash
PUT http://localhost:8080/respuestas/1/marcar-solucion
Authorization: Bearer eyJhbGci...
```

**⚠️ Nota:** Solo el **autor del tópico** (quien hizo la pregunta) o un **ADMIN** pueden marcar una respuesta como solución.

### **7. Intentar Eliminar Tópico de Otro Usuario (Denegado)**
```bash
# Usuario 2 intenta eliminar tópico de Usuario 1
DELETE http://localhost:8080/topicos/5
Authorization: Bearer [TOKEN_USUARIO_2]
```

**Response (403 Forbidden):**
```json
{
  "campo": "permiso",
  "error": "Solo el autor del tópico o un administrador pueden realizar esta acción"
}
```

</details>

---

## 🗄️ Modelo de Base de Datos
```
Usuario (1) ←→ (*) Topico (*) ←→ (1) Curso
Usuario (1) ←→ (*) Perfil (ManyToMany)
Topico (1) ←→ (*) Respuesta (*) ←→ (1) Usuario
```

**Estados de Tópico:**
* `NO_RESPONDIDO` → Sin respuestas
* `NO_SOLUCIONADO` → Con respuestas pero sin solución
* `SOLUCIONADO` → Con respuesta marcada como solución
* `CERRADO` → Tópico cerrado

---

## ✅ Tecnologías Utilizadas

* ☕ **Java 21** - Lenguaje de programación
* 🍃 **Spring Boot 3.5.11** - Framework backend
* 🔐 **Spring Security 6** - Autenticación y autorización
* 🎫 **JWT (Auth0)** - Tokens de autenticación
* 🗄️ **MySQL** - Base de datos relacional
* 🔄 **Flyway** - Migración de base de datos
* 📚 **SpringDoc OpenAPI 2.7.0** - Documentación Swagger
* 🧪 **Insomnia** - Testing de endpoints
* 📦 **Maven** - Gestión de dependencias
* 🎨 **Lombok** - Reducción de código boilerplate

---

## ✅ Buenas Prácticas Aplicadas

### **Arquitectura**
* ✅ Arquitectura en capas (Controller → Service → Repository)
* ✅ Separación de responsabilidades (SOLID - SRP)
* ✅ Inyección de dependencias con Spring
* ✅ DTOs para transferencia de datos
* ✅ Servicio de autorización centralizado

### **Seguridad**
* ✅ Autor del JWT - imposible de falsificar
* ✅ Autorización granular por propiedad
* ✅ Encriptación BCrypt de contraseñas
* ✅ Tokens JWT stateless
* ✅ Roles y permisos con Spring Security
* ✅ Endpoints protegidos por defecto
* ✅ Soft delete en usuarios

### **Manejo de Errores**
* ✅ Excepciones personalizadas por dominio
* ✅ Manejo centralizado con `@RestControllerAdvice`
* ✅ Respuestas HTTP consistentes y descriptivas
* ✅ Códigos de estado apropiados (404, 400, 401, 403)

### **Código**
* ✅ Comentarios JavaDoc exhaustivos
* ✅ Validaciones con Bean Validation
* ✅ Uso de Records para DTOs inmutables
* ✅ Métodos privados para encapsular lógica

### **Base de Datos**
* ✅ Migraciones versionadas con Flyway
* ✅ Relaciones bien definidas con JPA
* ✅ Índices para optimizar consultas

### **Documentación**
* ✅ Swagger UI con ejemplos claros
* ✅ Parámetros documentados con `@Parameter`
* ✅ Descripciones detalladas de cada endpoint
* ✅ Ejemplos de uso en código y README

### **API REST**
* ✅ Uso correcto de códigos HTTP (200, 201, 204, 400, 401, 403, 404)
* ✅ URIs RESTful bien estructuradas
* ✅ Paginación en colecciones grandes
* ✅ Headers Location en recursos creados

---


## 👤 Autor

**Paul Stuart Ruiz Cabrera**

[![GitHub](https://img.shields.io/badge/GitHub-000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Paulruiz23)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/paulruiz4227/)


