package es.juanbosco.ruben.reservaraulas.services;

import es.juanbosco.ruben.reservaraulas.Beans.CopiarClase;
import es.juanbosco.ruben.reservaraulas.entities.Aula;
import es.juanbosco.ruben.reservaraulas.entities.Reserva;
import es.juanbosco.ruben.reservaraulas.repositories.AulaRepositorio;
import es.juanbosco.ruben.reservaraulas.repositories.ReservaRepositorio;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AulaServicio {
    private final AulaRepositorio repository;
    private final CopiarClase copiarClase = new CopiarClase();
    private final ReservaRepositorio repositoryReserva;

    public List<Aula> obtenerTodas(){
        return repository.findAll();
    }

    public Aula guardar(Aula aula){
        return repository.save(aula);

    }

    @SneakyThrows
    public Aula actualizar(Aula aulaModificada, Long id){
        Optional<Aula> aula = obtenerPorId(id);

        //gracias a copiarClase.copyProperties solo actualiza si no es nulo, de tal forma que no hace falta poner todas las propiedades que no vas a modificar, solo la que si vas a modificar, en cartero, en put entre corchetes pones el dato solo que quieres actiualizar.
        if(aula.isPresent()){
            copiarClase.copyProperties(aula.get(), aulaModificada);
            return repository.save(aula.get());
        }
        return aulaModificada;
    }

    public void eliminar(Long id){
        repository.deleteById(id);
    }

    public Optional<Aula> obtenerPorId(Long id){
        return repository.findById(id);
    }

    public List<Aula> obtenerPorCapacidad(Integer capacidad){
        return repository.findByCapacidadGreaterThanEqual(capacidad);
    }

    public List<Aula> obtenerAulasConOrdenador(boolean esOrdenadores){
        return repository.findByEsOrdenadores(esOrdenadores);
    }

    public List<Reserva> obtenerReservasDeAula(Long id){
        return repositoryReserva.getReservaByAulaa_Id(id);
    }
}
