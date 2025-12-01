package es.juanbosco.ruben.reservaraulas.services;

import es.juanbosco.ruben.reservaraulas.entities.Horario;
import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.repositories.HorarioRepositorio;
import es.juanbosco.ruben.reservaraulas.repositories.ReservaRepositorio;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class HorarioServicio {
    private final HorarioRepositorio repository;
    private final ReservaRepositorio reservaRepositorio;

    public List<Horario> obtenerTodas(){
        return repository.findAll();
    }

    public Horario guardar(Horario horario){
        // Validación: la hora de fin debe ser posterior a la hora de inicio
        if (horario.getHoraFin() == null || horario.getHoraInicio() == null) {
            throw new IllegalArgumentException("Debe especificar hora de inicio y hora de fin.");
        }
        if (!horario.getHoraFin().isAfter(horario.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        return repository.save(horario);
    }

    @Transactional
    public void eliminar(Horario horario){
        if (horario == null || horario.getId() == null) {
            throw new IllegalArgumentException("Horario no encontrado");
        }

        // Primero eliminar todas las reservas asociadas a este horario
        List<Reserva> reservasHorario = reservaRepositorio.findAll().stream()
                .filter(r -> r.getHorario() != null && r.getHorario().getId().equals(horario.getId()))
                .toList();

        if (!reservasHorario.isEmpty()) {
            reservaRepositorio.deleteAll(reservasHorario);
            reservaRepositorio.flush();
        }

        // Ahora sí podemos eliminar el horario
        repository.delete(horario);
        repository.flush();
    }

    public Horario obtenerPorId(Long id){
        return repository.findById(id).orElse(null);
    }

}
