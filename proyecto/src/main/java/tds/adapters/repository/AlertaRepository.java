package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.AlertaImpl;

public interface AlertaRepository {

    /**
     * Devuelve una lista con todas las alertas cargadas
     * 
     * @return Lista de alertas
     */
    List<? extends AlertaImpl> getAlertas();
    
    /**
     * Busca una alerta por su nombre y la devuelve
     * 
     * @param nombre
     * @throws ElementoNoEncontradoException si no encuentra la alerta buscada
     * @return Alerta
     */
    AlertaImpl findByNombre(String nombre) throws ElementoNoEncontradoException;

    /**
     * Añade una alerta 
     * 
     * @param alerta
     * @throws ElementoExistenteException si existe una alerta igual en el repositorio
     * @return void
     */
    void addAlerta(AlertaImpl alerta) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Elimina una alerta del repositorio
     * 
     * @param alerta
     * @return void
     */
    void removeAlerta(AlertaImpl alerta) throws ErrorPersistenciaException;
    
    /**
     * Actualiza una alerta en el repositorio
     * 
     * @param alerta
     * @return Alerta
     */
    AlertaImpl updateAlerta(AlertaImpl alerta) throws ErrorPersistenciaException;
}
