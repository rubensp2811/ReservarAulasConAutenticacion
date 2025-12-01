package es.juanbosco.ruben.reservaraulas.config;

// Este filtro se ejecuta en cada petición HTTP antes de llegar a los controladores.
// Su función es validar el token JWT (si existe) y extraer los datos del usuario autenticado.
// Si el token es válido, coloca la información del usuario y sus roles en el contexto de seguridad de Spring.
// Spring Security (configurado en SecurityConfig) usará esa información para decidir si el usuario tiene acceso a cada endpoint.

import es.juanbosco.ruben.reservaraulas.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extraer el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        // Esto permite que otros métodos de autenticación (como Basic Auth) funcionen
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer el token (sin el prefijo "Bearer ")
            String token = authHeader.substring(7);

            // Parsear el token y extraer los claims (información del usuario)
            Claims claims = Jwts.parser()
                    .verifyWith(jwtService.getSecretKey()) // Comprobamos la autenticidad del token
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Extraer el email (subject) y los roles
            String email = claims.getSubject(); // El email identifica al usuario
            String rolesString = claims.get("roles", String.class); // Los roles determinan los permisos

            // Convertir los roles a GrantedAuthority (objeto que Spring Security entiende)
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Crear el objeto de autenticación con el email y los roles
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Guardar la autenticación en el contexto de seguridad de Spring
            // A partir de aquí, Spring Security sabe quién es el usuario y qué roles tiene
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Cuando SecurityConfig verifica los permisos, consulta este contexto

        } catch (Exception e) {
            // Si hay algún error al parsear el token, simplemente no autenticar
            // Spring Security se encargará de rechazar la petición si requiere autenticación
            System.err.println("Error al validar JWT: " + e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
