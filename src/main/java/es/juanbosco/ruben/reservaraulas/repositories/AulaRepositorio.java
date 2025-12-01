package es.juanbosco.ruben.reservaraulas.repositories;

import es.juanbosco.ruben.reservaraulas.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AulaRepositorio extends JpaRepository<Aula, Long> {
    List<Aula> findByCapacidad(Integer capacidad);

    List<Aula> findByCapacidadGreaterThanEqual(Integer capacidadIsGreaterThan);

    List<Aula> findByEsOrdenadores(Boolean esOrdenadores);
}

