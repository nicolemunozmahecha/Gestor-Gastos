package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.modelo.impl.GastoImpl;

public interface GastoRepository {


    List<? extends GastoImpl> getGastos();
    
    GastoImpl findByNombre(String nombre) throws ElementoNoEncontradoException;


}
