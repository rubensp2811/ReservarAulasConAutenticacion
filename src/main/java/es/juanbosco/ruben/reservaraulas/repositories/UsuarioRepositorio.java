package es.juanbosco.ruben.reservaraulas.repositories;

import es.juanbosco.ruben.reservaraulas.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

}
