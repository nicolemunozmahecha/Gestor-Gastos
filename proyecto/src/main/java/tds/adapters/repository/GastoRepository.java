package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.GastoImpl;

public interface GastoRepository {

    /**
     * Devuelve una lista con todos los gastos cargados
     * 
     * @return Lista de gastos
     */
    List<? extends GastoImpl> getGastos();
    
    /**
     * Busca un gasto por su id y lo devuelve
     * 
     * @param id
     * @throws ElementoNoEncontradoException si no encuentra el gasto buscado
     * @return Gasto
     */
    GastoImpl findByNombre(String nombre) throws ElementoNoEncontradoException;

    /**
     * Añade un gasto 
     * 
     * @param gasto
     * @throws ElementoExistenteException si existe un gasto igual en el repositorio
     * @return void
     */
    void addGasto(GastoImpl gasto) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Elimina un gasto del repositorio
     * 
     * @param gasto
     * @return void
     */
    void removeGasto(GastoImpl gasto) throws ErrorPersistenciaException;
    
    /**
     * Actualiza un gasto en el repositorio
     * 
     * @param gasto
     * @return Gasto
     */
    GastoImpl updateGasto(GastoImpl gasto) throws ErrorPersistenciaException;
}
