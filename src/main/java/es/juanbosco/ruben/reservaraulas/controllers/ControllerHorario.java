package es.juanbosco.ruben.reservaraulas.controllers;

import es.juanbosco.ruben.reservaraulas.entities.Horario;
import es.juanbosco.ruben.reservaraulas.services.HorarioServicio;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horarios")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ControllerHorario {
    private final HorarioServicio horarioServicio;

    @GetMapping
    public List<Horario> getHorarios() {
        return horarioServicio.obtenerTodas();
    }

    @PostMapping
    public ResponseEntity<?> createHorario(@RequestBody Horario horario) {
        try {
            return ResponseEntity.ok(horarioServicio.guardar(horario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horario> getHorarioPorId(@PathVariable Long id) {
        Horario horario = horarioServicio.obtenerPorId(id);
        if (horario != null) {
            return ResponseEntity.ok(horario);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorarioPorId(@PathVariable Long id) {
        Horario horario = horarioServicio.obtenerPorId(id);
        if (horario != null) {
            horarioServicio.eliminar(horario);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<?> updateHorario(@RequestBody Horario horario) {
        Horario existente = horarioServicio.obtenerPorId(horario.getId());
        if (existente != null) {
            try {
                Horario actualizado = horarioServicio.guardar(horario);
                return ResponseEntity.ok(actualizado);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.notFound().build();
    }
}
