package es.juanbosco.ruben.reservaraulas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (sin autenticación)
                        .requestMatchers("/auth/**").permitAll()

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

                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Con esto le decimos que no guarde sesión, es necesario para aplicaciones REST que usan tokens (JWT) o autenticación básica, porque cada petición debe ser independiente y contener sus credenciales
                .httpBasic(Customizer.withDefaults()); // Habilita autenticación básica con el nuevo método recomendado


        // Añade el filtro JWT solo para Bearer Token
        // Este filtro valida el token y pone los datos del usuario y sus roles en el contexto de seguridad
        // Así, Spring Security puede aplicar las reglas de acceso definidas arriba
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Permite todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todas las cabeceras, incluido Authorization
        configuration.setAllowCredentials(true); // Permite credenciales (cookies, headers de autenticación)
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Expone el header Authorization

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
