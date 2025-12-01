package es.juanbosco.ruben.reservaraulas.repositories;

import es.juanbosco.ruben.reservaraulas.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepositorio extends JpaRepository<Horario, Long> {
}
