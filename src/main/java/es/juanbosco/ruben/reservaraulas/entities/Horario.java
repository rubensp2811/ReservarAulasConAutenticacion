package es.juanbosco.ruben.reservaraulas.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import es.juanbosco.ruben.reservaraulas.enums.DIA_SEMANA;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import es.juanbosco.ruben.reservaraulas.entities.Reserva;

@Entity
@Table(name = "horarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "El día de la semana no puede ser nulo")
    @Enumerated(EnumType.STRING) // Guardar el enum como String en la base de datos
    private DIA_SEMANA diaSemana;

    @NotNull(message = "La hora de inicio no puede ser nula")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin no puede ser nula")
    private LocalTime horaFin;

    // Como ahora `Reserva` referencia a un `Horario` (ManyToOne en Reserva),
    // es común tener la relación inversa en Horario para consultas bidireccionales:
    // 1 Horario -> N Reservas
    @OneToMany(mappedBy = "horario")
    @ToString.Exclude // evita recursión en toString por la relación bidireccional
    @JsonIgnore
    private List<Reserva> reservas;


    public boolean seSolapaCon(Horario otro) {
        // Comprobar si es el mismo dia de la semana y el horario se solapa
        if (this.diaSemana != otro.diaSemana) {
            return false;
        }
        // Ahora solo compara las horas, la fecha se compara en Reserva
        return horaInicio.isBefore(otro.horaFin) && horaFin.isAfter(otro.horaInicio);
    }
}
