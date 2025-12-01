package es.juanbosco.ruben.reservaraulas.services;
import es.juanbosco.ruben.reservaraulas.Beans.CopiarClase;
import es.juanbosco.ruben.reservaraulas.entities.Aula;
import es.juanbosco.ruben.reservaraulas.entities.Horario;
import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.repositories.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReservaServicio {

    private final ReservaRepositorio repository;
    private final HorarioRepositorio horarioRepositorio; // nuevo: repositorio para Horario
    private final AulaRepositorio aulaRepositorio;
    private final CopiarClase copiarClase = new CopiarClase();

    public List<Reserva> obtenerTodas(){
        return repository.findAll();
    }

    // java
    @SneakyThrows
    public Reserva actualizar(Reserva reservaMoficiada, Long id){
        Optional<Reserva> reservaOptional = obtenerPorId(id);

        if(reservaOptional.isPresent()){
            copiarClase.copyProperties(reservaOptional.get(), reservaMoficiada);

            // Cargar Horario si el cliente envía solo el id
            if (reservaMoficiada.getHorario() != null && reservaMoficiada.getHorario().getId() != null) {
                Horario h = horarioRepositorio.findById(reservaMoficiada.getHorario().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado para el id proporcionado"));
                reservaOptional.get().setHorario(h);
            }

            // Cargar Aula si el cliente envía solo el id
            if (reservaMoficiada.getAulaa() != null && reservaMoficiada.getAulaa().getId() != null) {
                Aula a = aulaRepositorio.findById(reservaMoficiada.getAulaa().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada para el id proporcionado"));
                reservaOptional.get().setAulaa(a);
            }

            // Validar la reserva antes de guardar
            validarReserva(reservaOptional.get());

            return repository.save(reservaOptional.get());
        }
        return reservaMoficiada;
    }



    public Reserva guardar(Reserva reserva){
        // Aseguramos que el Horario referenciado existe en la base de datos
        if (reserva.getHorario() == null || reserva.getHorario().getId() == null) {
            throw new IllegalArgumentException("La reserva debe referenciar un horario existente (horario.id). No se creará un horario nuevo al guardar la reserva.");
        }

        Horario horarioExistente = horarioRepositorio.findById(reserva.getHorario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado para el id proporcionado"));

        reserva.setHorario(horarioExistente);



        if (reserva.getAulaa() != null && reserva.getAulaa().getId() != null) {
            Aula aulaExistente = aulaRepositorio.findById(reserva.getAulaa().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Aula no encontrada"));
            reserva.setAulaa(aulaExistente);
        }



        validarReserva(reserva);
        return repository.save(reserva);
    }

    @Transactional
    public void eliminarPorId(Long id) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        repository.delete(reserva);
        repository.flush();
    }

    public Optional<Reserva> obtenerPorId(Long id){
        return repository.findById(id);
    }

    private void validarReserva(Reserva reserva) {
        // Validación 2: No permitir reservas en el pasado
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden hacer reservas en el pasado.");
        }

        // Validación 3: Número de asistentes no puede superar la capacidad del aula
        if (reserva.getAsistentes() > reserva.getAulaa().getCapacidad()) {
            throw new IllegalArgumentException("El número de asistentes supera la capacidad del aula.");
        }

        // Validación 1: No permitir reservas solapadas en el mismo aula y horario
        List<Reserva> reservasAula = repository.getReservaByAulaa_Id(reserva.getAulaa().getId());

        for (Reserva rExistente : reservasAula) {
            // IMPORTANTE: Si estamos editando, excluir la propia reserva de la comparación
            if (reserva.getId() != null && rExistente.getId().equals(reserva.getId())) {
                continue; // Saltar esta iteración, es la misma reserva
            }

            if (rExistente.getFecha().isEqual(reserva.getFecha())) {

                if (rExistente.getHorario() != null && reserva.getHorario() != null) {
                    Horario hExistente = rExistente.getHorario();
                    Horario hNuevo = reserva.getHorario();

                    if (hExistente.seSolapaCon(hNuevo)) {
                        throw new IllegalArgumentException("La reserva se solapa con otra reserva existente en el mismo aula y horario.");
                    }
                }
            }
        }
    }

}
