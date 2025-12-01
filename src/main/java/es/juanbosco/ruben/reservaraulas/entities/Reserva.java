package es.juanbosco.ruben.reservaraulas.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Import explícito de las otras entidades para evitar problemas de resolución
import es.juanbosco.ruben.reservaraulas.entities.Aula;
import es.juanbosco.ruben.reservaraulas.entities.Horario;
import es.juanbosco.ruben.reservaraulas.entities.Usuario;

@Entity
@Table(name = "reservas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reserva {

    @Id //Marca el atributo id como primary key
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de la reserva no puede ser nula")
    private LocalDate fecha;
    private String motivo;

    @NotNull(message = "El número de asistentes no puede ser nulo")
    @Positive(message = "El número de asistentes debe ser un valor positivo")
    private Integer asistentes;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    // Cambiado a EAGER para que se cargue automáticamente al serializar a JSON
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"reservas"})
    @JoinColumn(name = "aula_id", nullable = false)
    @NotNull(message = "La reserva debe estar asociada a un aula")
    private Aula aulaa;


    // Cambiado a EAGER para que se cargue automáticamente al serializar a JSON
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"reservas"})
    @JoinColumn(name = "horario_id", nullable = false)
    @NotNull(message = "La reserva debe estar asociada a un horario")
    private Horario horario;


    // Cambiado a EAGER para que se cargue automáticamente al serializar a JSON
    @ManyToOne(fetch = FetchType.EAGER) //con lazy, evita cargar el usuario cada vez que consultas una reserva
    @JsonIgnoreProperties({"reservas", "password"})
    @JoinColumn(name = "usuario_id", nullable = false) //nombre de la columna de la FK en la tabla reservas, nula ble false porque una reserva siempre debe tener un usuario asociado.  (aqui ponemos JoinColumn y en Usuario ponemos mappedBy)
    @NotNull(message = "La reserva debe estar asociada a un usuario")
    private Usuario usuariox;

}