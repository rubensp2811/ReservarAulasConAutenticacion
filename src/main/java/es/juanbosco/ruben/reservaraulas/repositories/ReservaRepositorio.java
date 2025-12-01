package es.juanbosco.ruben.reservaraulas.repositories;

import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {
    List<Reserva> getReservaByAulaa_Id(Long aulaId);
}
