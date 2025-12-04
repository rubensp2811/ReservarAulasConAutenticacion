package es.juanbosco.ruben.reservaraulas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso PÚBLICO a recursos estáticos y frontend (sin popup de login)
                        .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // Rutas públicas de autenticación (sin autenticación)
                        .requestMatchers("/auth/**").permitAll()

                        // Rutas con Basic Auth - Solo para herramientas HTTP como Postman/curl
                        .requestMatchers("/api/basic/**").authenticated()

                        // AULAS - Solo ADMIN puede crear, editar y eliminar
                        .requestMatchers(HttpMethod.GET, "/aulas/**").hasAnyRole("PROFESOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/aulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/aulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/aulas/**").hasRole("ADMIN")

                        // HORARIOS - Solo ADMIN puede crear, editar y eliminar
                        .requestMatchers(HttpMethod.GET, "/horarios/**").hasAnyRole("PROFESOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/horarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/horarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/horarios/**").hasRole("ADMIN")

                        // RESERVAS - Ambos pueden crear y ver, editar/eliminar requiere ser ADMIN o creador (validación en servicio)
                        .requestMatchers(HttpMethod.GET, "/reservas/**").hasAnyRole("PROFESOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/reservas/**").hasAnyRole("PROFESOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/reservas/**").hasAnyRole("PROFESOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasAnyRole("PROFESOR", "ADMIN")

                        // USUARIOS - Solo para consultas básicas
                        .requestMatchers("/usuarios/**").hasAnyRole("PROFESOR", "ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Habilita Basic Auth SOLO para rutas /api/basic/** (no se aplica a recursos estáticos)
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Solo solicitar Basic Auth si la ruta es /api/basic/**
                            if (request.getRequestURI().startsWith("/api/basic/")) {
                                response.addHeader("WWW-Authenticate", "Basic realm=\"Restricted\"");
                                response.sendError(401, "Unauthorized");
                            } else {
                                // Para otras rutas protegidas, retornar 401 sin popup
                                response.sendError(401, "Unauthorized");
                            }
                        })
                );

        // Añade el filtro JWT ANTES de la autenticación básica
        // El filtro JWT procesa tokens Bearer, y si no hay token JWT, permite que Basic Auth se ejecute
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Permite todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Permite todas las cabeceras, incluido Authorization
        configuration.setAllowCredentials(true); // Permite credenciales (cookies, headers de autenticación)
        configuration.setExposedHeaders(List.of("Authorization")); // Expone el header Authorization

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
