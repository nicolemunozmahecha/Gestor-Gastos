package tds.adapters.repository.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

import tds.Configuracion;
import tds.adapters.repository.GastoRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.GastoImpl;
import tds.modelo.impl.CuentaImpl;
import tds.modelo.impl.DatosGastos;

/**
 * Repositorio de gastos.
 * NOTA: Los gastos ahora existen solo dentro de las cuentas.
 * Este repositorio obtiene los gastos de todas las cuentas.
 */
public class GastoRepositoryJSONImpl implements GastoRepository {
    
    private List<GastoImpl> gastos = null;
    private String rutaFichero = null;
    
    private void cargaGastos() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
            this.gastos = cargarGastos(rutaFichero);
            if (gastos == null) gastos = new ArrayList<>();
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public List<? extends GastoImpl> getGastos() {
        if (gastos == null) {
            try {
                cargaGastos();
            } catch (ErrorPersistenciaException e) {
                    System.err.println("ERROR: No se han podido cargar datos desde JSON (se devuelve lista vacía).");
                    e.printStackTrace();
                    gastos = new ArrayList<>();
                }
        }
        return gastos;
    }

    @Override
    public GastoImpl findByNombre(String nombre) throws ElementoNoEncontradoException { 
        
    	if (gastos == null) {
            getGastos();
        }
        
        Optional<GastoImpl> gasto = gastos.stream()
            .filter(g -> g.getNombre().equals(nombre))
            .findFirst();
    
        if (gasto.isEmpty())
            throw new ElementoNoEncontradoException("El gasto con id " + nombre + " no existe");
        return gasto.orElse(null);
    }

    @Override
    public void addGasto(GastoImpl gasto) throws ElementoExistenteException, ErrorPersistenciaException {
        // Este método ya no debe usarse - los gastos se añaden a través de las cuentas
        throw new UnsupportedOperationException(
            "No se pueden añadir gastos directamente. Use agregarGastoACuenta() del GestorGastos");
    }

    @Override
    public void removeGasto(GastoImpl gasto) throws ErrorPersistenciaException {
        // Este método ya no debe usarse - los gastos se eliminan a través de las cuentas
        throw new UnsupportedOperationException(
            "No se pueden eliminar gastos directamente. Elimínelos de la cuenta correspondiente");
    }

    @Override
    public GastoImpl updateGasto(GastoImpl gasto) throws ErrorPersistenciaException {
        // Este método ya no debe usarse - los gastos se actualizan a través de las cuentas
        throw new UnsupportedOperationException(
            "No se pueden actualizar gastos directamente. Actualice la cuenta correspondiente");
    }

    /**
     * Carga los gastos de todas las cuentas
     */
    private List<GastoImpl> cargarGastos(String rutaFichero) throws Exception {
    	File ficheroStream = new File(rutaFichero);
    	if (!ficheroStream.exists()) {
            // Si no existe el fichero, devolver lista vacía
            return new ArrayList<>();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        
        DatosGastos datosCargados = mapper.readValue(ficheroStream, new TypeReference<DatosGastos>() {});
        datos.setCuentas(datosCargados.getCuentas());
        datos.setCategorias(datosCargados.getCategorias());
        datos.setAlertas(datosCargados.getAlertas());
        datos.setPersonas(datosCargados.getPersonas());

        // Obtener todos los gastos de todas las cuentas
        List<GastoImpl> todosLosGastos = new ArrayList<>();
        for (CuentaImpl cuenta : datosCargados.getCuentas()) {
            todosLosGastos.addAll(cuenta.getGastos().stream()
                .map(g -> (GastoImpl) g)
                .collect(Collectors.toList()));
        }
        
        this.gastos = todosLosGastos;
        return gastos;
    }
}