package es.juanbosco.ruben.reservaraulas.controllers;

import es.juanbosco.ruben.reservaraulas.entities.Aula;
import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.services.AulaServicio;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aulas")
@AllArgsConstructor
@CrossOrigin(origins = "*")  //Permitir llamadas desde cualquier origen
public class ControllerAula {

    private final AulaServicio aulaServicio;

    @GetMapping
    public List<Aula> getAulas(
            @RequestParam(required = false) Integer capacidad,          //required false para que no sea obligatorio, ya que a veces es un parametro y a veces el otro o ninguno directamente
            @RequestParam(required = false) Boolean esOrdenadores
    ) {
        if(capacidad != null) {
            return aulaServicio.obtenerPorCapacidad(capacidad);
        }
        if(esOrdenadores != null) {
            return aulaServicio.obtenerAulasConOrdenador(esOrdenadores);
        }

        return aulaServicio.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aula> getAulaPorId(@PathVariable Long id) {
        return aulaServicio.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAulaPorId(@PathVariable Long id) {
        aulaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aula> updateAula(@RequestBody Aula aula, @PathVariable Long id) {
        return ResponseEntity.ok(aulaServicio.actualizar(aula, id));
    }

    @PostMapping
    public ResponseEntity<Aula> createAula(@RequestBody Aula aula) {
        return ResponseEntity.ok(aulaServicio.guardar(aula));
    }


    @GetMapping("/{id}/reservas")
    public List<Reserva> getReservasDeAula(@PathVariable Long id) {
        return aulaServicio.obtenerReservasDeAula(id);
    }



}



//Get (obtener)
//Post (Insertar)
//Put (actualizar)
