package es.juanbosco.ruben.reservaraulas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link es.juanbosco.ruben.reservaraulas.entities.Usuario}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioPerfilResponse(
        @NotNull(message = "El nombre no puede ser nulo") @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotNull(message = "El rol no puede ser nulo") @NotBlank(message = "El rol es obligatorio") String rol,
        @NotNull(message = "El email no puede ser nulo") @NotBlank(message = "El email es obligatorio") String email,
        boolean enabled) implements Serializable {
}