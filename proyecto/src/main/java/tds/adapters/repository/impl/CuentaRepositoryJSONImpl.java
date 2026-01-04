package tds.adapters.repository.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tds.Configuracion;
import tds.adapters.repository.CuentaRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CuentaImpl;
import tds.modelo.impl.DatosGastos;

public class CuentaRepositoryJSONImpl implements CuentaRepository {
    
    private List<CuentaImpl> cuentas = null;
    private String rutaFichero = null;
    
    private void cargaCuentas() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
            this.cuentas = cargarCuentas(rutaFichero);
            if (cuentas == null) cuentas = new ArrayList<>();
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public List<? extends CuentaImpl> getCuentas() {
        if (cuentas == null) {
            try {
                cargaCuentas();
            } catch (ErrorPersistenciaException e) {
                    System.err.println("ERROR: No se han podido cargar datos desde JSON (se devuelve lista vacía).");
                    e.printStackTrace();
                    cuentas = new ArrayList<>();
                }
        }
        return cuentas;
    }

    @Override
    public CuentaImpl findByNombre(String nombre) throws ElementoNoEncontradoException { 
        if (cuentas == null) {
            getCuentas();
        }
        
        Optional<CuentaImpl> cuenta = cuentas.stream()
                .filter(c -> c.getNombre().equals(nombre))
                .findFirst();
    
        if (cuenta.isEmpty())
            throw new ElementoNoEncontradoException("La cuenta " + nombre + " no existe");
        return cuenta.orElse(null);
    }

    @Override
    public void addCuenta(CuentaImpl cuenta) throws ElementoExistenteException, ErrorPersistenciaException {
        if (cuentas == null) {
            getCuentas();
        }
        
        if (rutaFichero == null) {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
        }
        
        // Verificar si ya existe una cuenta con el mismo nombre
        boolean yaExiste = cuentas.stream()
            .anyMatch(c -> c.getNombre().equals(cuenta.getNombre()));
        
        if (yaExiste) {
            throw new ElementoExistenteException("La cuenta '" + cuenta.getNombre() + "' ya existe.");
        }
        
        cuentas.add(cuenta);
        try {
            guardarCuentas(cuentas, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void removeCuenta(CuentaImpl cuenta) throws ErrorPersistenciaException {
        if (cuentas == null) {
            getCuentas();
        }
        
        if (!cuentas.contains(cuenta)) 
            return;
        
        cuentas.remove(cuenta);
        try {
            guardarCuentas(cuentas, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public CuentaImpl updateCuenta(CuentaImpl cuenta) throws ErrorPersistenciaException {
        if (cuentas == null) {
            getCuentas();
        }
        
        try {
            guardarCuentas(cuentas, rutaFichero);
            return cuenta;
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    private List<CuentaImpl> cargarCuentas(String rutaFichero) throws Exception {

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
        datos.setNotificaciones(datosCargados.getNotificaciones());

        this.cuentas = datosCargados.getCuentas();
        return cuentas;
    }

    private void guardarCuentas(List<CuentaImpl> cuentas, String rutaFichero) throws Exception {
        if (rutaFichero == null) {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
        }

        File ficheroJson = new File(rutaFichero);
        if (ficheroJson.getParentFile() != null) {
            ficheroJson.getParentFile().mkdirs();
        }
        
        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        datos.setCuentas(cuentas);
        this.cuentas = cuentas;
                
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
            mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJson, datos);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}