package es.juanbosco.ruben.reservaraulas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity//Le dice que funciona como si fuera una tabla
@Table(name = "aulas") //le dice a que tabla de la base de datos referencia
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Aula {
    @Id //Marca el atributo id como primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Indica que es autoincremental
    private Long id;

    @NotNull(message = "El nombre del aula no puede ser nulo")
    @NotBlank(message = "El nombre del aula es obligatorio")
    private String nombre;

    @NotNull(message = "La capacidad del aula no puede ser nula")
    @Positive(message = "La capacidad del aula debe ser un valor positivo")
    private Integer capacidad;

    @NotNull(message = "indicar si el aula tiene ordenadores es obligatorio")
    private Boolean esOrdenadores;

    //1 a muchos
    //Todas las reservas va a tener un campo aula
    @OneToMany(mappedBy = "aulaa", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Reserva> reservas;
}
