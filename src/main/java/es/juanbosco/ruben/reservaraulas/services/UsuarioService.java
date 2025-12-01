package es.juanbosco.ruben.reservaraulas.services;

import es.juanbosco.ruben.reservaraulas.Beans.CopiarClase;
import es.juanbosco.ruben.reservaraulas.entities.Aula;
import es.juanbosco.ruben.reservaraulas.entities.Horario;
import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.entities.Usuario;
import es.juanbosco.ruben.reservaraulas.repositories.ReservaRepositorio;
import es.juanbosco.ruben.reservaraulas.repositories.UsuarioRepositorio;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepositorio repository;
    private final ReservaRepositorio reservaRepositorio;
    private final CopiarClase copiarClase = new CopiarClase();

    public UsuarioService(UsuarioRepositorio repository, ReservaRepositorio reservaRepositorio) {
        this.repository = repository;
        this.reservaRepositorio = reservaRepositorio;
    }

    public Optional<Usuario> obtenerPorId(Long id){
        return repository.findById(id);
    }

    @Transactional
    public void eliminarPorId(Long id) {
        // Verificar que el usuario existe
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Primero eliminar todas las reservas asociadas al usuario
        List<Reserva> reservasUsuario = reservaRepositorio.findAll().stream()
                .filter(r -> r.getUsuariox().getId().equals(id))
                .toList();

        if (!reservasUsuario.isEmpty()) {
            reservaRepositorio.deleteAll(reservasUsuario);
            reservaRepositorio.flush();
        }

        // Ahora sí podemos eliminar el usuario
        repository.delete(usuario);
        repository.flush();
    }



    @SneakyThrows
    public Usuario actualizar(Usuario usuarioModificado, Long id){
        Optional<Usuario> usuarioOptional = obtenerPorId(id);

        if(usuarioOptional.isPresent()){
            copiarClase.copyProperties(usuarioOptional.get(), usuarioModificado);

            // Validar la reserva antes de guardar
            validarUsuario(usuarioOptional.get());

            return repository.save(usuarioOptional.get());
        }
        return usuarioModificado;
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return repository.findByEmail(email);
    }





    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        if (!usuario.getRol().equals("ROLE_ADMIN") && !usuario.getRol().equals("ROLE_PROFESOR")) {
            throw new IllegalArgumentException("El rol debe ser 'ROLE_ADMIN' o 'ROLE_PROFESOR'");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
    }

}
