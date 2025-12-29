package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CuentaImpl;

public interface CuentaRepository {

    List<? extends CuentaImpl> getCuentas();

    CuentaImpl findByNombre(String nombre) throws ElementoNoEncontradoException;

    void addCuenta(CuentaImpl cuenta) throws ElementoExistenteException, ErrorPersistenciaException;

    void removeCuenta(CuentaImpl cuenta) throws ErrorPersistenciaException;
    
    CuentaImpl updateCuenta(CuentaImpl cuenta) throws ErrorPersistenciaException;
}
