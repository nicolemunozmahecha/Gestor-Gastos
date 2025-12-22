package tds.adapters.repository.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tds.Configuracion;
import tds.adapters.repository.CuentaRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CuentaImpl;
import tds.modelo.impl.DatosGastos;

public class CuentaRepositoryJSONImpl implements CuentaRepository {
    
    private List<CuentaImpl> cuentas = null;
    private String rutaFichero = "/data/gastos.json";
    
    private void cargaCuentas() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos() + "cuentas.json";
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
                // Manejo la excepcion pero no la propago
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
        
        // Si la cuenta ya existe no puedo insertarla
        if (cuentas.contains(cuenta)) {
            throw new ElementoExistenteException("La cuenta " + cuenta.getNombre() + " ya existe");
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
       /* InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);
        
        if (ficheroStream == null) {
            // Si no existe el fichero, devolver lista vacía
            return new ArrayList<>();
        }*/
    	File ficheroStream = new File(rutaFichero);
    	if (!ficheroStream.exists()) {
            // Si no existe el fichero, devolver lista vacía
            return new ArrayList<>();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        
        DatosGastos datosCargados = mapper.readValue(ficheroStream, new TypeReference<DatosGastos>() {});
        datos.setGastos(datosCargados.getGastos());
        datos.setCuentas(datosCargados.getCuentas());
        datos.setCategorias(datosCargados.getCategorias());
        datos.setAlertas(datosCargados.getAlertas());
        datos.setPersonas(datosCargados.getPersonas());

        this.cuentas = datosCargados.getCuentas();
        return cuentas;
    }

    private void guardarCuentas(List<CuentaImpl> cuentas, String rutaFichero) throws Exception {
        // Se carga mediante URL para prevenir problemas con rutas con espacios en blanco o caracteres no estandar
    	/*
    	if (rutaFichero == null) {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
        }
    	
        URL url = getClass().getResource(rutaFichero);
        
        // Cargo el fichero a partir de la URL local
        File ficheroJson = Paths.get(url.toURI()).toFile();*/
        File ficheroJson = new File(rutaFichero);
    	if(ficheroJson.getParentFile() != null) {
    		ficheroJson.getParentFile().mkdirs();
    	}
        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        datos.setCuentas(cuentas);
        this.cuentas = cuentas;
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJson, datos);
    }
}