package es.juanbosco.ruben.reservaraulas.controllers;

import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.entities.Usuario;
import es.juanbosco.ruben.reservaraulas.services.ReservaServicio;
import es.juanbosco.ruben.reservaraulas.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ControllerUsuario {
    private final UsuarioService usuarioService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservaPorId(@PathVariable Long id) {
        usuarioService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
        //validar solapamiento, fecha pasada, asistente<=capacidad
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@RequestBody Usuario usuario, @PathVariable Long id) {
        //Asegura que el ID de la entidad coincide con el path variable antes de actualizar
        usuario.setId(id);
        try {
            return ResponseEntity.ok(usuarioService.actualizar(usuario, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/cambiar-pass")
    public ResponseEntity<?> cambiarPassword(Authentication authentication, @RequestParam String nuevaPassword) {
        try {
            // Obtener el email del usuario autenticado
            String email = authentication.getName();
            // Buscar el usuario por email
            Usuario usuario = usuarioService.obtenerPorEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para el usuario autenticado"));

            usuario.setPassword(nuevaPassword);
            Usuario actualizado = usuarioService.actualizar(usuario, usuario.getId());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
