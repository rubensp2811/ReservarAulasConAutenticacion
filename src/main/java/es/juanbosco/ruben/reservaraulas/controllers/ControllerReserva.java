package es.juanbosco.ruben.reservaraulas.controllers;

import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.entities.Usuario;
import es.juanbosco.ruben.reservaraulas.services.ReservaServicio;
import es.juanbosco.ruben.reservaraulas.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ControllerReserva {
    private final ReservaServicio reservaServicio;
    private final UsuarioService usuarioService;

    @GetMapping
    public List<Reserva> getReservas() {
        return reservaServicio.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaPorId(@PathVariable Long id) {
        return reservaServicio.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservaPorId(@PathVariable Long id, Authentication authentication) {
        try {
            // Obtener el email y rol del usuario autenticado
            String email = authentication.getName();
            boolean esAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            // Si no es ADMIN, validar que sea el creador de la reserva
            if (!esAdmin) {
                Reserva reserva = reservaServicio.obtenerPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

                if (!reserva.getUsuariox().getEmail().equals(email)) {
                    return ResponseEntity.status(403)
                            .body("No tienes permiso para eliminar esta reserva. Solo puedes eliminar tus propias reservas.");
                }
            }

            reservaServicio.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<?> createReserva(@RequestBody Reserva reserva, Authentication authentication) {
        try {
            // Obtener el email del usuario autenticado desde el token JWT
            String email = authentication.getName();

            // Buscar el usuario en la base de datos
            Usuario usuario = usuarioService.obtenerPorEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado"));

            // Asignar el usuario a la reserva
            reserva.setUsuariox(usuario);

            return ResponseEntity.ok(reservaServicio.guardar(reserva));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReserva(@RequestBody Reserva reserva, @PathVariable Long id, Authentication authentication) {
        reserva.setId(id);
        try {
            // Obtener el email y rol del usuario autenticado
            String email = authentication.getName();
            boolean esAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            // Si no es ADMIN, validar que sea el creador de la reserva
            if (!esAdmin) {
                Reserva reservaExistente = reservaServicio.obtenerPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

                if (!reservaExistente.getUsuariox().getEmail().equals(email)) {
                    return ResponseEntity.status(403)
                            .body("No tienes permiso para editar esta reserva. Solo puedes editar tus propias reservas.");
                }
            }

            // Buscar el usuario en la base de datos
            Usuario usuario = usuarioService.obtenerPorEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado"));

            // Asignar el usuario a la reserva
            reserva.setUsuariox(usuario);

            return ResponseEntity.ok(reservaServicio.actualizar(reserva, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
