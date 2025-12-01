# 📚 CHULETA COMPLETA: SPRING BOOT CON JWT Y SEGURIDAD

## 📋 ÍNDICE
1. [Estructura del Proyecto](#estructura-del-proyecto)
2. [Configuración Inicial (pom.xml)](#configuración-inicial)
3. [Entidades (Entities)](#entidades)
4. [Repositorios (Repositories)](#repositorios)
5. [DTOs (Data Transfer Objects)](#dtos)
6. [Servicios (Services)](#servicios)
7. [Controladores (Controllers)](#controladores)
8. [Seguridad (Security Config)](#seguridad)
9. [Anotaciones Importantes](#anotaciones-importantes)
10. [Relaciones entre Entidades](#relaciones-entre-entidades)
11. [Flujo de una Petición](#flujo-de-una-petición)

---

## 1️⃣ ESTRUCTURA DEL PROYECTO

```
src/main/java/
├── config/                    # Configuración de seguridad y filtros
│   ├── SecurityConfig.java
│   └── JwtAuthenticationFilter.java
├── controllers/               # Endpoints REST (API)
│   ├── AuthController.java
│   ├── ControllerAula.java
│   ├── ControllerHorario.java
│   ├── ControllerReserva.java
│   └── ControllerUsuario.java
├── dto/                       # Objetos de transferencia de datos
│   ├── LoginRequest.java
│   └── RegisterRequest.java
├── entities/                  # Modelos de la base de datos
│   ├── Aula.java
│   ├── Horario.java
│   ├── Reserva.java
│   └── Usuario.java
├── enums/                     # Enumeraciones
│   └── DIA_SEMANA.java
├── repositories/              # Acceso a la base de datos
│   ├── AulaRepositorio.java
│   ├── HorarioRepositorio.java
│   ├── ReservaRepositorio.java
│   └── UsuarioRepositorio.java
├── services/                  # Lógica de negocio
│   ├── AulaServicio.java
│   ├── HorarioServicio.java
│   ├── ReservaServicio.java
│   ├── UsuarioService.java
│   ├── JwtService.java
│   └── CustomUserDetailsService.java
└── Application.java           # Clase principal
```

---

## 2️⃣ CONFIGURACIÓN INICIAL (pom.xml)

### Dependencias necesarias:

```xml
<!-- Spring Boot Starter Web: Para crear APIs REST -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Boot Starter Data JPA: Para trabajar con bases de datos -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Boot Starter Security: Para autenticación y autorización -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- MySQL Connector: Para conectar con MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok: Para reducir código boilerplate (getters, setters, constructores) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- JWT (JSON Web Token): Para generar y validar tokens -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Validation: Para validar datos (@NotNull, @NotBlank, etc.) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### application.properties:

```properties
# Configuración de la base de datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_bd
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate: Crea/actualiza las tablas automáticamente
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Secret Key (clave secreta para firmar tokens)
jwt.secret=tu_clave_secreta_muy_larga_y_segura_aqui
jwt.expiration=86400000
```

---

## 3️⃣ ENTIDADES (Entities)

Las entidades representan las tablas de la base de datos.

### 🔹 Ejemplo: Usuario.java

```java
@Entity                           // Marca esta clase como entidad JPA (tabla en BD)
@Table(name = "usuarios")         // Nombre de la tabla en la BD
@Data                             // Lombok: genera getters, setters, toString, equals, hashCode
@AllArgsConstructor               // Lombok: constructor con todos los parámetros
@NoArgsConstructor                // Lombok: constructor sin parámetros
@Builder                          // Lombok: patrón Builder para crear objetos
public class Usuario implements UserDetails {
    
    @Id                           // Marca este campo como PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.SEQUENCE)  // Autoincrementa el ID
    private Long id;
    
    @NotNull(message = "El nombre no puede ser nulo")    // Validación: no puede ser null
    @NotBlank(message = "El nombre es obligatorio")      // Validación: no puede estar vacío
    private String nombre;
    
    @Column(unique = true, nullable = false)             // Columna única y no nula
    private String email;
    
    private String password;
    
    private String rol;  // "ROLE_ADMIN" o "ROLE_PROFESOR"
    
    // Relación 1:N - Un usuario tiene muchas reservas
    @OneToMany(mappedBy = "usuariox", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnoreProperties("usuariox")  // Evita bucles infinitos en JSON
    private List<Reserva> reservas;
    
    private boolean enabled = true;
    
    // Métodos de UserDetails (necesarios para Spring Security)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : rol.split(",")) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
    
    @Override
    public String getUsername() {
        return email;  // Usamos email como username
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return enabled; }
}
```

### 🔹 Ejemplo: Reserva.java (con relaciones)

```java
@Entity
@Table(name = "reservas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @JsonFormat(pattern = "yyyy-MM-dd")  // Formato de fecha en JSON
    @NotNull(message = "La fecha no puede ser nula")
    private LocalDate fecha;
    
    private String motivo;
    
    @NotNull
    @Positive
    private Integer asistentes;
    
    @CreationTimestamp  // Asigna automáticamente la fecha de creación
    private LocalDateTime fechaCreacion;
    
    // Relación N:1 - Muchas reservas pertenecen a un aula
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"reservas"})  // Evita bucle infinito
    @JoinColumn(name = "aula_id", nullable = false)  // Nombre de la columna FK
    @NotNull
    private Aula aulaa;
    
    // Relación N:1 - Muchas reservas pertenecen a un horario
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"reservas"})
    @JoinColumn(name = "horario_id", nullable = false)
    @NotNull
    private Horario horario;
    
    // Relación N:1 - Muchas reservas pertenecen a un usuario
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"reservas", "password"})  // No enviar password en JSON
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuariox;
}
```

---

## 4️⃣ REPOSITORIOS (Repositories)

Los repositorios acceden a la base de datos. **NO necesitas implementar métodos CRUD básicos**, Spring Data JPA los genera automáticamente.

### 🔹 Ejemplo: ReservaRepositorio.java

```java
@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {
    
    // Spring genera automáticamente:
    // - save(reserva)          -> INSERT o UPDATE
    // - findById(id)           -> SELECT por ID
    // - findAll()              -> SELECT *
    // - deleteById(id)         -> DELETE
    // - existsById(id)         -> Verifica si existe
    
    // Métodos personalizados:
    // Spring genera la consulta SQL automáticamente según el nombre del método
    List<Reserva> findByFecha(LocalDate fecha);
    
    List<Reserva> findByUsuariox_Id(Long usuarioId);
    
    // Consulta personalizada con @Query
    @Query("SELECT r FROM Reserva r WHERE r.aulaa.id = :aulaId AND r.horario.id = :horarioId AND r.fecha = :fecha")
    List<Reserva> findByAulaAndHorarioAndFecha(
        @Param("aulaId") Long aulaId,
        @Param("horarioId") Long horarioId,
        @Param("fecha") LocalDate fecha
    );
}
```

**¿Cómo funciona?**
- `extends JpaRepository<Reserva, Long>`: Extiende de JpaRepository pasando la entidad (Reserva) y el tipo de ID (Long)
- Spring genera automáticamente los métodos CRUD
- Los métodos personalizados se generan según el nombre (findBy, deleteBy, etc.)

---

## 5️⃣ DTOs (Data Transfer Objects)

Los DTOs son objetos simples para transferir datos entre el cliente y el servidor. Se usan para:
- No exponer las entidades directamente
- Validar datos de entrada
- Enviar solo los datos necesarios

### 🔹 Ejemplo: LoginRequest.java

```java
public record LoginRequest(
    @NotBlank(message = "El email es obligatorio")
    String email,
    
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
```

### 🔹 Ejemplo: RegisterRequest.java

```java
public record RegisterRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    String email,
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,
    
    @NotBlank(message = "El rol es obligatorio")
    String rol
) {}
```

---

## 6️⃣ SERVICIOS (Services)

Los servicios contienen la lógica de negocio. Llaman a los repositorios para acceder a la base de datos.

### 🔹 Ejemplo: ReservaServicio.java

```java
@Service  // Marca esta clase como servicio de Spring
@Transactional  // Todas las operaciones son transaccionales
public class ReservaServicio {
    
    @Autowired  // Inyección de dependencias
    private ReservaRepositorio reservaRepositorio;
    
    @Autowired
    private AulaRepositorio aulaRepositorio;
    
    @Autowired
    private HorarioRepositorio horarioRepositorio;
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    // Obtener todas las reservas
    public List<Reserva> obtenerTodas() {
        return reservaRepositorio.findAll();
    }
    
    // Crear o actualizar una reserva
    public Reserva guardar(Reserva reserva) {
        // Validar que no exista otra reserva en el mismo aula, horario y fecha
        List<Reserva> reservasExistentes = reservaRepositorio.findByAulaAndHorarioAndFecha(
            reserva.getAulaa().getId(),
            reserva.getHorario().getId(),
            reserva.getFecha()
        );
        
        // Si existe una reserva con diferente ID, hay conflicto
        for (Reserva r : reservasExistentes) {
            if (!r.getId().equals(reserva.getId())) {
                throw new IllegalArgumentException("La reserva se solapa con otra existente");
            }
        }
        
        // Cargar las entidades relacionadas desde la BD
        Aula aula = aulaRepositorio.findById(reserva.getAulaa().getId())
            .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada"));
        
        Horario horario = horarioRepositorio.findById(reserva.getHorario().getId())
            .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
        
        Usuario usuario = usuarioRepositorio.findById(reserva.getUsuariox().getId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Asignar las entidades cargadas
        reserva.setAulaa(aula);
        reserva.setHorario(horario);
        reserva.setUsuariox(usuario);
        
        // Guardar en la BD
        return reservaRepositorio.save(reserva);
    }
    
    // Eliminar una reserva
    public void eliminar(Long id) {
        if (!reservaRepositorio.existsById(id)) {
            throw new IllegalArgumentException("Reserva no encontrada");
        }
        reservaRepositorio.deleteById(id);
    }
}
```

---

## 7️⃣ CONTROLADORES (Controllers)

Los controladores manejan las peticiones HTTP y devuelven respuestas.

### 🔹 Ejemplo: ControllerReserva.java

```java
@RestController  // Marca esta clase como controlador REST
@RequestMapping("/reservas")  // Ruta base: /reservas
@CrossOrigin(origins = "*")  // Permite peticiones desde cualquier origen (CORS)
public class ControllerReserva {
    
    @Autowired
    private ReservaServicio reservaServicio;
    
    // GET /reservas - Obtener todas las reservas
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")  // Solo ADMIN y PROFESOR
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = reservaServicio.obtenerTodas();
        return ResponseEntity.ok(reservas);
    }
    
    // POST /reservas - Crear una nueva reserva
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<?> createReserva(@Valid @RequestBody Reserva reserva) {
        try {
            Reserva nuevaReserva = reservaServicio.guardar(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT /reservas/{id} - Actualizar una reserva
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservaServicio.esCreadorReserva(#id, authentication.name)")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @Valid @RequestBody Reserva reserva) {
        try {
            reserva.setId(id);
            Reserva actualizada = reservaServicio.guardar(reserva);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DELETE /reservas/{id} - Eliminar una reserva
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reservaServicio.esCreadorReserva(#id, authentication.name)")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        try {
            reservaServicio.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
```

**Códigos HTTP comunes:**
- `200 OK`: Operación exitosa
- `201 CREATED`: Recurso creado
- `204 NO CONTENT`: Operación exitosa sin contenido
- `400 BAD REQUEST`: Datos inválidos
- `401 UNAUTHORIZED`: No autenticado
- `403 FORBIDDEN`: No autorizado (sin permisos)
- `404 NOT FOUND`: Recurso no encontrado
- `500 INTERNAL SERVER ERROR`: Error del servidor

---

## 8️⃣ SEGURIDAD (Security Config)

### 🔹 SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize en los controladores
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Desactiva CSRF (no necesario para APIs REST)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Configura CORS
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()  // Permite acceso sin autenticación a /auth/**
                .requestMatchers(HttpMethod.GET, "/aulas/**").hasAnyRole("ADMIN", "PROFESOR")
                .requestMatchers(HttpMethod.POST, "/aulas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/aulas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/aulas/**").hasRole("ADMIN")
                .anyRequest().authenticated()  // Todas las demás rutas requieren autenticación
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Sin sesiones (JWT)
            )
            .httpBasic(Customizer.withDefaults())  // Habilita autenticación básica (usuario/contraseña)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Añade filtro JWT
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Codifica contraseñas con BCrypt
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");  // Permite todos los orígenes
        configuration.addAllowedMethod("*");  // Permite todos los métodos (GET, POST, PUT, DELETE)
        configuration.addAllowedHeader("*");  // Permite todos los headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 🔹 JwtAuthenticationFilter.java

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Extraer el token del header Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        
        // 2. Verificar que el header comienza con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Quitar "Bearer "
            
            try {
                // 3. Extraer el email del token
                email = jwtService.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Error al validar JWT: " + e.getMessage());
            }
        }
        
        // 4. Si el token es válido y no hay autenticación previa
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. Cargar el usuario desde la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            // 6. Validar el token
            if (jwtService.validateToken(token, userDetails)) {
                // 7. Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()  // Roles del usuario
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 8. Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 9. Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}
```

### 🔹 JwtService.java

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    
    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;
    
    // Generar token JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Añadir roles al token
        String roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        claims.put("roles", roles);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())  // Email del usuario
            .setIssuer("gestion-centro-api")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 24 horas
            .signWith(getSignKey(), SignatureAlgorithm.HS256)  // Firma con clave secreta
            .compact();
    }
    
    // Extraer email del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Validar token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    // Verificar si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // Extraer fecha de expiración
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Extraer cualquier claim del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    // Extraer todos los claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    // Obtener la clave de firma
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## 9️⃣ ANOTACIONES IMPORTANTES

### 📌 Anotaciones de Spring

| Anotación | Uso | Explicación |
|-----------|-----|-------------|
| `@Entity` | Clase | Marca la clase como entidad JPA (tabla en BD) |
| `@Table(name = "...")` | Clase | Especifica el nombre de la tabla en BD |
| `@Id` | Campo | Marca el campo como PRIMARY KEY |
| `@GeneratedValue` | Campo | Genera automáticamente el valor (autoincremento) |
| `@Column` | Campo | Configura la columna (unique, nullable, etc.) |
| `@OneToMany` | Campo | Relación 1:N (un objeto tiene muchos) |
| `@ManyToOne` | Campo | Relación N:1 (muchos objetos pertenecen a uno) |
| `@JoinColumn` | Campo | Especifica la columna FK en la relación |
| `@Repository` | Clase | Marca la clase como repositorio (acceso a BD) |
| `@Service` | Clase | Marca la clase como servicio (lógica de negocio) |
| `@RestController` | Clase | Marca la clase como controlador REST |
| `@RequestMapping` | Clase/Método | Define la ruta base del controlador |
| `@GetMapping` | Método | Define endpoint GET |
| `@PostMapping` | Método | Define endpoint POST |
| `@PutMapping` | Método | Define endpoint PUT |
| `@DeleteMapping` | Método | Define endpoint DELETE |
| `@PathVariable` | Parámetro | Obtiene variable de la URL (/usuarios/{id}) |
| `@RequestBody` | Parámetro | Obtiene el cuerpo de la petición (JSON) |
| `@RequestParam` | Parámetro | Obtiene parámetro de query (?nombre=valor) |
| `@Autowired` | Campo | Inyección de dependencias automática |
| `@PreAuthorize` | Método | Define permisos de acceso (roles) |
| `@Transactional` | Método/Clase | Marca como transaccional (rollback automático si hay error) |
| `@Valid` | Parámetro | Valida el objeto según anotaciones de validación |

### 📌 Anotaciones de Lombok

| Anotación | Uso | Explicación |
|-----------|-----|-------------|
| `@Data` | Clase | Genera getters, setters, toString, equals, hashCode |
| `@AllArgsConstructor` | Clase | Constructor con todos los parámetros |
| `@NoArgsConstructor` | Clase | Constructor sin parámetros |
| `@Builder` | Clase | Patrón Builder para crear objetos |
| `@Getter` | Campo/Clase | Genera getter |
| `@Setter` | Campo/Clase | Genera setter |
| `@ToString` | Clase | Genera método toString |

### 📌 Anotaciones de Validación

| Anotación | Uso | Explicación |
|-----------|-----|-------------|
| `@NotNull` | Campo | El campo no puede ser null |
| `@NotBlank` | Campo (String) | El campo no puede estar vacío o solo espacios |
| `@NotEmpty` | Campo (String/Collection) | El campo no puede estar vacío |
| `@Size(min, max)` | Campo (String/Collection) | Define tamaño mínimo y máximo |
| `@Min(valor)` | Campo (Number) | Valor mínimo |
| `@Max(valor)` | Campo (Number) | Valor máximo |
| `@Positive` | Campo (Number) | Debe ser positivo |
| `@Email` | Campo (String) | Valida formato de email |
| `@Pattern(regexp)` | Campo (String) | Valida con expresión regular |

### 📌 Anotaciones de Jackson (JSON)

| Anotación | Uso | Explicación |
|-----------|-----|-------------|
| `@JsonIgnoreProperties` | Campo/Clase | Ignora propiedades al serializar/deserializar JSON |
| `@JsonIgnore` | Campo | Ignora el campo al serializar/deserializar |
| `@JsonFormat` | Campo | Define formato de fecha/hora en JSON |
| `@JsonProperty` | Campo | Cambia el nombre del campo en JSON |

**¿Cuándo usar `@JsonIgnoreProperties`?**
- Para evitar **bucles infinitos** en relaciones bidireccionales
- Ejemplo: Usuario tiene reservas, Reserva tiene usuario
- Sin `@JsonIgnoreProperties` → Spring intenta serializar: Usuario → Reservas → Usuario → Reservas → ... (infinito)
- Con `@JsonIgnoreProperties("usuariox")` en Usuario → Spring ignora el campo usuariox al serializar las reservas

```java
// En Usuario.java
@OneToMany(mappedBy = "usuariox")
@JsonIgnoreProperties("usuariox")  // Ignora usuariox al serializar las reservas
private List<Reserva> reservas;

// En Reserva.java
@ManyToOne
@JsonIgnoreProperties({"reservas", "password"})  // Ignora reservas y password al serializar el usuario
private Usuario usuariox;
```

---

## 🔟 RELACIONES ENTRE ENTIDADES

### 📌 1:N (Uno a Muchos) - @OneToMany

Un objeto tiene muchos objetos relacionados.
Ejemplo: **Un Usuario tiene muchas Reservas**

```java
// En Usuario.java (lado "uno")
@OneToMany(
    mappedBy = "usuariox",              // Nombre del campo en la entidad Reserva
    fetch = FetchType.LAZY,             // Carga perezosa (no carga automáticamente)
    cascade = {CascadeType.PERSIST, CascadeType.MERGE}  // Operaciones en cascada
)
@JsonIgnoreProperties("usuariox")       // Evita bucle infinito
private List<Reserva> reservas;
```

**Parámetros importantes:**
- `mappedBy`: Indica que la relación está mapeada en el otro lado (Reserva)
- `fetch`: 
  - `LAZY`: Carga los datos solo cuando se accede a ellos (más eficiente)
  - `EAGER`: Carga los datos automáticamente (puede ser lento)
- `cascade`: Define qué operaciones se propagan a las entidades relacionadas
  - `PERSIST`: Al guardar el usuario, guarda las reservas
  - `MERGE`: Al actualizar el usuario, actualiza las reservas
  - `REMOVE`: Al eliminar el usuario, elimina las reservas
  - `ALL`: Todas las operaciones

### 📌 N:1 (Muchos a Uno) - @ManyToOne

Muchos objetos pertenecen a un objeto.
Ejemplo: **Muchas Reservas pertenecen a un Usuario**

```java
// En Reserva.java (lado "muchos")
@ManyToOne(fetch = FetchType.EAGER)     // Carga automática
@JsonIgnoreProperties({"reservas", "password"})  // Evita bucle y no envía password
@JoinColumn(name = "usuario_id", nullable = false)  // Nombre de la columna FK
@NotNull(message = "La reserva debe tener un usuario")
private Usuario usuariox;
```

**Parámetros importantes:**
- `@JoinColumn`: Define la columna de clave foránea (FK) en la tabla
  - `name`: Nombre de la columna FK
  - `nullable`: Si puede ser null o no
- `fetch`: Igual que en @OneToMany
  - En @ManyToOne se suele usar EAGER porque es más común necesitar el dato relacionado

### 📌 N:M (Muchos a Muchos) - @ManyToMany

Muchos objetos se relacionan con muchos objetos.
Ejemplo: **Muchos Alumnos tienen Muchas Asignaturas**

```java
// En Alumno.java
@ManyToMany
@JoinTable(
    name = "alumno_asignatura",         // Tabla intermedia
    joinColumns = @JoinColumn(name = "alumno_id"),
    inverseJoinColumns = @JoinColumn(name = "asignatura_id")
)
private List<Asignatura> asignaturas;

// En Asignatura.java
@ManyToMany(mappedBy = "asignaturas")
private List<Alumno> alumnos;
```

**¿Cuál usar?**
- Si una entidad "contiene" a otra → @OneToMany en la contenedora
- Si una entidad "pertenece" a otra → @ManyToOne en la contenida
- Si ambas se relacionan mutuamente sin jerarquía → @ManyToMany

---

## 1️⃣1️⃣ FLUJO DE UNA PETICIÓN

### 📌 Ejemplo: POST /reservas (Crear reserva)

```
Cliente (Frontend/Postman)
    ↓
[1] Envía petición HTTP POST /reservas con JSON en el body
    ↓
JwtAuthenticationFilter
    ↓
[2] Extrae el token del header Authorization
[3] Valida el token con JwtService
[4] Carga el usuario desde CustomUserDetailsService
[5] Establece la autenticación en SecurityContext
    ↓
SecurityConfig
    ↓
[6] Verifica permisos según @PreAuthorize
    ↓
ControllerReserva
    ↓
[7] Recibe la petición en el método createReserva(@RequestBody Reserva reserva)
[8] Valida los datos con @Valid
[9] Llama a reservaServicio.guardar(reserva)
    ↓
ReservaServicio
    ↓
[10] Valida la lógica de negocio (no solapamiento)
[11] Carga las entidades relacionadas (Aula, Horario, Usuario) desde los repositorios
[12] Llama a reservaRepositorio.save(reserva)
    ↓
ReservaRepositorio
    ↓
[13] JpaRepository ejecuta la consulta SQL INSERT en la BD
[14] Devuelve la reserva guardada con el ID generado
    ↓
ReservaServicio
    ↓
[15] Devuelve la reserva al controlador
    ↓
ControllerReserva
    ↓
[16] Devuelve ResponseEntity con código 201 CREATED y la reserva en JSON
    ↓
Cliente (Frontend/Postman)
    ↓
[17] Recibe la respuesta con la reserva creada
```

---

## 🔐 AUTENTICACIÓN Y AUTORIZACIÓN

### 📌 Diferencia entre Autenticación y Autorización

- **Autenticación**: ¿Quién eres? (Login)
- **Autorización**: ¿Qué puedes hacer? (Permisos/Roles)

### 📌 Flujo de Autenticación con JWT

```
1. Usuario envía POST /auth/login con email y password
2. AuthController recibe la petición
3. AuthenticationManager valida las credenciales
4. Si son correctas, JwtService genera un token JWT
5. El token se devuelve al cliente
6. Cliente guarda el token (localStorage, sessionStorage, cookie)
7. En las siguientes peticiones, el cliente envía el token en el header:
   Authorization: Bearer {token}
8. JwtAuthenticationFilter intercepta la petición
9. Valida el token y carga el usuario
10. Establece la autenticación en SecurityContext
11. El controlador verifica los permisos con @PreAuthorize
12. Si tiene permisos, ejecuta el método; si no, devuelve 403 FORBIDDEN
```

### 📌 Autenticación Básica vs JWT

| Característica | Autenticación Básica | JWT |
|----------------|----------------------|-----|
| **Método** | Usuario y contraseña en cada petición | Token en el header |
| **Header** | `Authorization: Basic base64(email:password)` | `Authorization: Bearer {token}` |
| **Validez** | Siempre válida (mientras la contraseña no cambie) | Expira después de un tiempo (ej: 24 horas) |
| **Seguridad** | Menos segura (envía credenciales en cada petición) | Más segura (no envía credenciales) |
| **Uso** | Desarrollo, APIs internas | Producción, APIs públicas |

**¿Cuándo usar cada una?**
- **Autenticación Básica**: Solo para desarrollo o APIs internas
- **JWT**: Producción, aplicaciones web, móviles

### 📌 Roles y Permisos

**Hay DOS formas de configurar permisos:**

#### Forma 1: En SecurityConfig (Configuración centralizada)
```java
// En SecurityConfig.java
.requestMatchers(HttpMethod.GET, "/aulas/**").hasAnyRole("ADMIN", "PROFESOR")
.requestMatchers(HttpMethod.POST, "/aulas/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/aulas/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/aulas/**").hasRole("ADMIN")
```

✅ **Ventajas**:
- Toda la configuración de seguridad está en un solo lugar
- Más fácil de mantener para permisos generales
- No necesitas `@PreAuthorize` en los controladores

❌ **Desventajas**:
- Menos flexible para permisos específicos por método
- Difícil configurar condiciones complejas (ej: "solo el creador")

#### Forma 2: Con @PreAuthorize (Configuración por método)
```java
// En ControllerReserva.java
@GetMapping
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")  // Solo ADMIN y PROFESOR
public ResponseEntity<List<Reserva>> getAllReservas() {
```

✅ **Ventajas**:
- Más flexible, puedes ver los permisos directamente en el método
- Permite condiciones complejas con expresiones SpEL

❌ **Desventajas**:
- Permisos distribuidos por todo el código
- Requiere `@EnableMethodSecurity` en SecurityConfig

**Nota importante**: Si ya tienes los permisos configurados en `SecurityConfig`, **NO necesitas @PreAuthorize** en los controladores. Ambos funcionan, pero usar los dos a la vez puede crear confusión.

#### Condiciones complejas con @PreAuthorize

```java
// Solo ADMIN puede ejecutar este método
@PreAuthorize("hasRole('ADMIN')")

// ADMIN o PROFESOR pueden ejecutar este método
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")

// ADMIN o el creador de la reserva pueden eliminarla
@PreAuthorize("hasRole('ADMIN') or @reservaServicio.esCreadorReserva(#id, authentication.name)")
```

**Cuándo usar cada forma:**
- **SecurityConfig**: Permisos generales por URL (más común y simple)
- **@PreAuthorize**: Permisos específicos con condiciones complejas (ej: "solo el creador puede editar")

**Importante**: Los roles en la BD deben tener el prefijo `ROLE_`
- En BD: `ROLE_ADMIN`, `ROLE_PROFESOR`
- En @PreAuthorize y SecurityConfig: `hasRole('ADMIN')` (Spring añade el prefijo automáticamente)

---

## 🛠️ CONSEJOS Y BUENAS PRÁCTICAS

### ✅ DO (Hacer)

1. **Usa DTOs** para transferir datos, no expongas las entidades directamente
2. **Valida datos** con `@Valid` y anotaciones de validación
3. **Maneja excepciones** con try-catch y devuelve mensajes claros
4. **Usa @Transactional** en los servicios para operaciones de BD
5. **Usa @JsonIgnoreProperties** para evitar bucles infinitos
6. **Usa LAZY** para @OneToMany (más eficiente)
7. **Usa EAGER** para @ManyToOne si necesitas el dato relacionado
8. **Codifica contraseñas** con BCrypt (passwordEncoder.encode())
9. **Documenta** tus endpoints (comentarios, README, Swagger)
10. **Prueba** con Postman o Insomnia antes de integrar con el frontend

### ❌ DON'T (No hacer)

1. **No expongas contraseñas** en JSON (usa @JsonIgnoreProperties)
2. **No uses EAGER** para @OneToMany (puede causar problemas de rendimiento)
3. **No confíes en datos del cliente** sin validar
4. **No devuelvas entidades** directamente, usa DTOs
5. **No uses REMOVE en cascade** sin pensar (puede eliminar datos importantes)
6. **No olvides** @JsonIgnoreProperties en relaciones bidireccionales
7. **No uses el mismo nombre** para atributos en ambos lados de la relación
8. **No olvides** nullable = false en @JoinColumn para relaciones obligatorias

---

## 📝 EJEMPLO COMPLETO: CREAR UNA ENTIDAD DESDE CERO

### Paso 1: Crear la entidad

```java
@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotNull
    @Positive
    private Double precio;
    
    private Integer stock;
}
```

### Paso 2: Crear el repositorio

```java
@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContaining(String nombre);
}
```

### Paso 3: Crear el servicio

```java
@Service
@Transactional
public class ProductoServicio {
    @Autowired
    private ProductoRepositorio productoRepositorio;
    
    public List<Producto> obtenerTodos() {
        return productoRepositorio.findAll();
    }
    
    public Producto guardar(Producto producto) {
        return productoRepositorio.save(producto);
    }
    
    public void eliminar(Long id) {
        productoRepositorio.deleteById(id);
    }
}
```

### Paso 4: Crear el controlador

```java
@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ControllerProducto {
    @Autowired
    private ProductoServicio productoServicio;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<List<Producto>> getAll() {
        return ResponseEntity.ok(productoServicio.obtenerTodos());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> create(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoServicio.guardar(producto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Paso 5: Configurar permisos en SecurityConfig

```java
.requestMatchers(HttpMethod.GET, "/productos/**").hasAnyRole("ADMIN", "PROFESOR")
.requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")
```

---

## 🎯 RESUMEN RÁPIDO

1. **Entidades** → Tablas en BD (con @Entity, @Table, @Id, @Column, etc.)
2. **Repositorios** → Acceso a BD (extends JpaRepository, métodos CRUD automáticos)
3. **Servicios** → Lógica de negocio (valida, procesa, llama a repositorios)
4. **Controladores** → Endpoints REST (recibe peticiones HTTP, devuelve respuestas)
5. **DTOs** → Objetos para transferir datos (no exponer entidades)
6. **SecurityConfig** → Configuración de seguridad (rutas, roles, autenticación)
7. **JwtAuthenticationFilter** → Intercepta peticiones, valida tokens
8. **JwtService** → Genera y valida tokens JWT

**Flujo completo:**
```
Cliente → Controlador → Servicio → Repositorio → Base de Datos
                ↓
            Validación de seguridad (JWT + Roles)
```

---

## 📚 RECURSOS ADICIONALES

- [Documentación oficial de Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - Decodificar y debuggear tokens
- [Lombok](https://projectlombok.org/)
- [Postman](https://www.postman.com/) - Cliente HTTP para probar APIs

---

**¡Con esta chuleta tienes todo lo necesario para construir una aplicación Spring Boot completa desde cero!** 🚀
