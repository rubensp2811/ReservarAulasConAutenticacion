# 📚 Sistema de Reserva de Aulas

Sistema web para la gestión de reservas de aulas educativas, desarrollado con **Spring Boot** y **MySQL**.

## 🎯 Descripción

Aplicación REST API que permite a profesores y administradores gestionar reservas de aulas en un centro educativo. El sistema controla la disponibilidad de aulas, horarios y valida que no haya solapamientos en las reservas.

## 🛠️ Tecnologías Utilizadas

- **Backend**: Spring Boot 3.5.6
- **Java**: 17
- **Base de datos**: MySQL
- **Seguridad**: Spring Security + JWT (JSON Web Tokens)
- **ORM**: Spring Data JPA / Hibernate
- **Validación**: Jakarta Validation
- **Utilidades**: Lombok, Commons BeanUtils
- **Frontend**: HTML5, CSS3, Bootstrap 5, JavaScript

## 📋 Requisitos Previos

- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

## ⚙️ Configuración

### Base de Datos

Crear una base de datos MySQL llamada `reservas`:

```sql
CREATE DATABASE reservas;
```

### Configuración (application.properties)

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/reservas
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=mi_clave_super_secreta_muy_larga_que_debe_tener_al_menos_32_bytes_2025
```

## 🚀 Instalación y Ejecución

1. **Clonar el repositorio**
   ```bash
   git clone <url-repositorio>
   cd ReservarAulas
   ```

2. **Compilar el proyecto**
   ```bash
   ./mvnw clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Acceder a la aplicación**
   - API: `http://localhost:8080`
   - Frontend: `http://localhost:8080/index.html` o abrir directamente el archivo `index.html`

## 👥 Roles y Permisos

El sistema maneja dos roles de usuario:

### 🔑 ROLE_ADMIN (Administrador)
- **Aulas**: Crear, leer, actualizar y eliminar
- **Horarios**: Crear, leer, actualizar y eliminar
- **Reservas**: Crear, leer, actualizar y eliminar (todas)
- **Usuarios**: Gestión completa

### 👨‍🏫 ROLE_PROFESOR (Profesor)
- **Aulas**: Solo lectura
- **Horarios**: Solo lectura
- **Reservas**: 
  - Crear nuevas reservas
  - Ver todas las reservas
  - Editar y eliminar **solo sus propias reservas**

## 📡 Endpoints de la API

### 🔐 Autenticación (`/auth`)

Endpoints públicos (no requieren autenticación):

| Método | Endpoint | Descripción | Body |
|--------|----------|-------------|------|
| POST | `/auth/login` | Iniciar sesión | `LoginRequest` |
| POST | `/auth/register` | Registrar nuevo usuario | `RegisterRequest` |
| GET | `/auth/perfil` | Obtener perfil del usuario autenticado | - |

**LoginRequest DTO:**
```json
{
  "email": "usuario@example.com",
  "password": "contraseña"
}
```

**RegisterRequest DTO:**
```json
{
  "email": "nuevo@example.com",
  "password": "contraseña123",
  "rol": "ROLE_PROFESOR"
}
```

**Respuesta de Login:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 🚪 Aulas (`/aulas`)

| Método | Endpoint | Roles Permitidos | Descripción | Parámetros |
|--------|----------|------------------|-------------|------------|
| GET | `/aulas` | PROFESOR, ADMIN | Obtener todas las aulas | `?capacidad=25&esOrdenadores=true` (opcional) |
| GET | `/aulas/{id}` | PROFESOR, ADMIN | Obtener aula por ID | - |
| POST | `/aulas` | **ADMIN** | Crear nueva aula | Body: `Aula` |
| PUT | `/aulas/{id}` | **ADMIN** | Actualizar aula | Body: `Aula` |
| DELETE | `/aulas/{id}` | **ADMIN** | Eliminar aula | - |
| GET | `/aulas/{id}/reservas` | PROFESOR, ADMIN | Obtener reservas de un aula | - |

**Estructura Aula:**
```json
{
  "id": 1,
  "nombre": "Aula 101",
  "capacidad": 30,
  "esOrdenadores": true
}
```

---

### 🕐 Horarios (`/horarios`)

| Método | Endpoint | Roles Permitidos | Descripción |
|--------|----------|------------------|-------------|
| GET | `/horarios` | PROFESOR, ADMIN | Obtener todos los horarios |
| GET | `/horarios/{id}` | PROFESOR, ADMIN | Obtener horario por ID |
| POST | `/horarios` | **ADMIN** | Crear nuevo horario |
| PUT | `/horarios` | **ADMIN** | Actualizar horario |
| DELETE | `/horarios/{id}` | **ADMIN** | Eliminar horario |

**Estructura Horario:**
```json
{
  "id": 1,
  "diaSemana": "LUNES",
  "horaInicio": "08:00",
  "horaFin": "10:00"
}
```

**Valores permitidos para diaSemana:**
- `LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`

---

### 📅 Reservas (`/reservas`)

| Método | Endpoint | Roles Permitidos | Descripción | Validación Adicional |
|--------|----------|------------------|-------------|---------------------|
| GET | `/reservas` | PROFESOR, ADMIN | Obtener todas las reservas | - |
| GET | `/reservas/{id}` | PROFESOR, ADMIN | Obtener reserva por ID | - |
| POST | `/reservas` | PROFESOR, ADMIN | Crear nueva reserva | Usuario se asigna automáticamente |
| PUT | `/reservas/{id}` | PROFESOR, ADMIN | Actualizar reserva | Solo creador o ADMIN |
| DELETE | `/reservas/{id}` | PROFESOR, ADMIN | Eliminar reserva | Solo creador o ADMIN |

**Estructura Reserva (Request):**
```json
{
  "fecha": "2025-12-01",
  "motivo": "Clase de programación",
  "asistentes": 25,
  "aulaa": {
    "id": 1
  },
  "horario": {
    "id": 1
  }
}
```

**Estructura Reserva (Response):**
```json
{
  "id": 1,
  "fecha": "2025-12-01",
  "motivo": "Clase de programación",
  "asistentes": 25,
  "fechaCreacion": "2025-11-15T10:30:00",
  "aulaa": {
    "id": 1,
    "nombre": "Aula 101",
    "capacidad": 30,
    "esOrdenadores": true
  },
  "horario": {
    "id": 1,
    "diaSemana": "LUNES",
    "horaInicio": "08:00",
    "horaFin": "10:00"
  },
  "usuariox": {
    "id": 1,
    "email": "profesor@example.com",
    "nombre": "Juan Pérez",
    "rol": "ROLE_PROFESOR"
  }
}
```

---

### 👤 Usuarios (`/usuarios`)

| Método | Endpoint | Roles Permitidos | Descripción |
|--------|----------|------------------|-------------|
| PUT | `/usuarios/{id}` | PROFESOR, ADMIN | Actualizar usuario |
| DELETE | `/usuarios/{id}` | PROFESOR, ADMIN | Eliminar usuario |
| PATCH | `/usuarios/cambiar-pass` | PROFESOR, ADMIN | Cambiar contraseña del usuario autenticado |

**Cambiar contraseña:**
```
PATCH /usuarios/cambiar-pass?nuevaPassword=nuevaContraseña123
```

---

## 🔒 Autenticación con JWT

Todos los endpoints (excepto `/auth/login` y `/auth/register`) requieren autenticación mediante JWT.

### Cómo usar el token:

1. **Iniciar sesión** en `/auth/login` para obtener el token
2. **Incluir el token** en cada petición:

```http
GET /aulas HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

En JavaScript (fetch):
```javascript
fetch('http://localhost:8080/aulas', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

---

## ✅ Validaciones del Sistema

### Validaciones de Reservas:

1. **No solapamiento**: No se permite crear reservas que se solapen en el mismo aula, fecha y horario
2. **No reservas pasadas**: No se pueden crear reservas con fechas anteriores a hoy
3. **Capacidad**: El número de asistentes no puede superar la capacidad del aula
4. **Horario existente**: Debe seleccionarse un horario previamente creado
5. **Permisos**: Solo el creador o un ADMIN pueden editar/eliminar una reserva

### Validaciones de Usuarios:

- Email único en el sistema
- Contraseña mínima de 6 caracteres
- Rol válido: `ROLE_ADMIN` o `ROLE_PROFESOR`

---

## 📦 Entidades del Sistema

### 🏢 Aula
```java
@Entity
public class Aula {
    @Id @GeneratedValue
    private Long id;
    
    @NotNull @NotBlank
    private String nombre;
    
    @NotNull @Positive
    private Integer capacidad;
    
    @NotNull
    private Boolean esOrdenadores;
    
    @OneToMany(mappedBy = "aulaa")
    private List<Reserva> reservas;
}
```

### ⏰ Horario
```java
@Entity
public class Horario {
    @Id @GeneratedValue
    private Long id;
    
    @NotNull @Enumerated(EnumType.STRING)
    private DIA_SEMANA diaSemana;
    
    @NotNull
    private LocalTime horaInicio;
    
    @NotNull
    private LocalTime horaFin;
    
    @OneToMany(mappedBy = "horario")
    private List<Reserva> reservas;
}
```

### 📖 Reserva
```java
@Entity
public class Reserva {
    @Id @GeneratedValue
    private Long id;
    
    @NotNull
    private LocalDate fecha;
    
    private String motivo;
    
    @NotNull @Positive
    private Integer asistentes;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Aula aulaa;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Horario horario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Usuario usuariox;
}
```

### 👨‍💼 Usuario
```java
@Entity
public class Usuario implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    
    @NotNull @NotBlank
    private String nombre;
    
    @NotNull @NotBlank
    private String rol;
    
    @NotNull @NotBlank @Column(unique = true)
    private String email;
    
    @NotNull @NotBlank
    private String password;
    
    @OneToMany(mappedBy = "usuariox")
    private List<Reserva> reservas;
    
    private boolean enabled = true;
}
```

---

## 🗂️ Estructura del Proyecto

```
ReservarAulas/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── es.juanbosco.ruben.reservaraulas/
│   │   │       ├── Beans/
│   │   │       │   └── CopiarClase.java
│   │   │       ├── config/
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controllers/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── ControllerAula.java
│   │   │       │   ├── ControllerHorario.java
│   │   │       │   ├── ControllerReserva.java
│   │   │       │   └── ControllerUsuario.java
│   │   │       ├── dto/
│   │   │       │   ├── LoginRequest.java
│   │   │       │   └── RegisterRequest.java
│   │   │       ├── entities/
│   │   │       │   ├── Aula.java
│   │   │       │   ├── Horario.java
│   │   │       │   ├── Reserva.java
│   │   │       │   └── Usuario.java
│   │   │       ├── enums/
│   │   │       │   └── DIA_SEMANA.java
│   │   │       ├── repositories/
│   │   │       │   ├── AulaRepositorio.java
│   │   │       │   ├── HorarioRepositorio.java
│   │   │       │   ├── ReservaRepositorio.java
│   │   │       │   └── UsuarioRepositorio.java
│   │   │       ├── services/
│   │   │       │   ├── AulaServicio.java
│   │   │       │   ├── CustomUserDetailsService.java
│   │   │       │   ├── HorarioServicio.java
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── ReservaServicio.java
│   │   │       │   └── UsuarioService.java
│   │   │       └── ReservarAulasApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── index.html
├── pom.xml
└── README.md
```

---

## 🌐 Interfaz Web (index.html)

La aplicación incluye una interfaz web completa con las siguientes características:

- **Diseño responsive** con Bootstrap 5
- **Autenticación**: Login y registro de usuarios
- **Gestión de Aulas**: CRUD completo (solo ADMIN)
- **Gestión de Horarios**: CRUD completo (solo ADMIN)
- **Gestión de Reservas**: Crear, ver, editar y eliminar reservas
- **Filtros**: Búsqueda de aulas por capacidad y ordenadores
- **Validaciones**: Control de permisos según el rol del usuario

---

## 🔧 Configuración de Seguridad (CORS)

La aplicación permite peticiones desde cualquier origen (`*`). Para producción, se recomienda configurar orígenes específicos en `SecurityConfig.java`:

```java
configuration.setAllowedOriginPatterns(Arrays.asList("https://tudominio.com"));
```

---

## 📝 Notas Importantes

1. **JWT Secret**: El secret está configurado en `application.properties`. Para producción, usar variables de entorno.
2. **Base de datos**: El sistema usa `ddl-auto=update`, que actualiza el esquema automáticamente.
3. **Tablas de secuencia**: Hibernate crea tablas `*_seq` para controlar los IDs autoincrementales.
4. **Password**: Las contraseñas se almacenan cifradas con BCrypt.
5. **Token expiración**: Los tokens JWT expiran en 24 horas.

---

## 🐛 Solución de Problemas

### Error: "JWT signature does not match"
- El secret JWT ha cambiado. Regenera el token haciendo login nuevamente.

### Error: "La reserva se solapa con otra"
- Ya existe una reserva para esa aula en ese horario y fecha.
- Verifica los horarios disponibles antes de crear la reserva.

### Error: "No se pueden hacer reservas en el pasado"
- La fecha de la reserva debe ser hoy o posterior.

### Error al cargar reservas en el frontend
- Las relaciones `@ManyToOne` deben usar `FetchType.EAGER` en la entidad `Reserva`.

---

## 📄 Licencia

Este proyecto es un sistema educativo desarrollado como ejemplo.

---

## 👨‍💻 Autor

**Rubén** - IES Juan Bosco

---

## 📚 Recursos Adicionales

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [Bootstrap 5](https://getbootstrap.com/)

---

## 🚀 Próximas Mejoras

- [ ] Notificaciones por email
- [ ] Exportar reservas a PDF/Excel
- [ ] Dashboard con estadísticas
- [ ] Sistema de recordatorios
- [ ] Integración con calendario (Google Calendar, Outlook)
- [ ] Búsqueda avanzada de reservas
- [ ] Historial de cambios

