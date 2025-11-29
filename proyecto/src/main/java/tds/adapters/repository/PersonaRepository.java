package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.PersonaImpl;

public interface PersonaRepository {

    /**
     * Devuelve una lista con todas las personas cargadas
     * 
     * @return Lista de personas
     */
    List<? extends PersonaImpl> getPersonas();
    
    /**
     * Busca una persona por su nombre y la devuelve
     * 
     * @param nombre
     * @throws ElementoNoEncontradoException si no encuentra la persona buscada
     * @return Persona
     */
    PersonaImpl findByNombre(String nombre) throws ElementoNoEncontradoException;

    /**
     * Añade una persona 
     * 
     * @param persona
     * @throws ElementoExistenteException si existe una persona igual en el repositorio
     * @return void
     */
    void addPersona(PersonaImpl persona) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Elimina una persona del repositorio
     * 
     * @param persona
     * @return void
     */
    void removePersona(PersonaImpl persona) throws ErrorPersistenciaException;
    
    /**
     * Actualiza una persona en el repositorio
     * 
     * @param persona
     * @return Persona
     */
    PersonaImpl updatePersona(PersonaImpl persona) throws ErrorPersistenciaException;
}
