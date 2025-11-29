package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CuentaImpl;

public interface CuentaRepository {

    /**
     * Devuelve una lista con todas las cuentas cargadas
     * 
     * @return Lista de cuentas
     */
    List<? extends CuentaImpl> getCuentas();
    
    /**
     * Busca una cuenta por su nombre y la devuelve
     * 
     * @param nombre
     * @throws ElementoNoEncontradoException si no encuentra la cuenta buscada
     * @return Cuenta
     */
    CuentaImpl findByNombre(String nombre) throws ElementoNoEncontradoException;

    /**
     * Añade una cuenta 
     * 
     * @param cuenta
     * @throws ElementoExistenteException si existe una cuenta igual en el repositorio
     * @return void
     */
    void addCuenta(CuentaImpl cuenta) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Elimina una cuenta del repositorio
     * 
     * @param cuenta
     * @return void
     */
    void removeCuenta(CuentaImpl cuenta) throws ErrorPersistenciaException;
    
    /**
     * Actualiza una cuenta en el repositorio
     * 
     * @param cuenta
     * @return Cuenta
     */
    CuentaImpl updateCuenta(CuentaImpl cuenta) throws ErrorPersistenciaException;
}
