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
import tds.adapters.repository.AlertaRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.AlertaImpl;
import tds.modelo.impl.DatosGastos;

public class AlertaRepositoryJSONImpl implements AlertaRepository {
    
    private List<AlertaImpl> alertas = null;
    private String rutaFichero = null;
    
    private void cargaAlertas() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
            this.alertas = cargarAlertas(rutaFichero);
            if (alertas == null) alertas = new ArrayList<>();
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public List<? extends AlertaImpl> getAlertas() {
        if (alertas == null) {
            try {
                cargaAlertas();
            } catch (ErrorPersistenciaException e) {
                    System.err.println("ERROR: No se han podido cargar datos desde JSON (se devuelve lista vacía).");
                    e.printStackTrace();
                    alertas = new ArrayList<>();
                }
        }
        return alertas;
    }

    @Override
    public AlertaImpl findByNombre(String nombre) throws ElementoNoEncontradoException {
        if (alertas == null) {
            getAlertas();
        }
        
        Optional<AlertaImpl> alerta = alertas.stream()
                .filter(a -> a.getNombre().equals(nombre))
                .findFirst();
        
        if (alerta.isEmpty())
            throw new ElementoNoEncontradoException("La alerta " + nombre + " no existe");
        return alerta.orElse(null);
    }

    @Override
    public void addAlerta(AlertaImpl alerta) throws ElementoExistenteException, ErrorPersistenciaException {
        if (alertas == null) {
            getAlertas();
        }
        
        if (rutaFichero == null) {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
        }
        
        // Si la alerta ya existe no puedo insertarla
        if (alertas.contains(alerta)) {
            throw new ElementoExistenteException("La alerta " + alerta.getNombre() + " ya existe");
        }
        
        alertas.add(alerta);
        try {
            guardarAlertas(alertas, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void removeAlerta(AlertaImpl alerta) throws ErrorPersistenciaException {
        if (alertas == null) {
            getAlertas();
        }
        
        if (!alertas.contains(alerta)) 
            return;
        
        alertas.remove(alerta);
        try {
            guardarAlertas(alertas, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public AlertaImpl updateAlerta(AlertaImpl alerta) throws ErrorPersistenciaException {
        if (alertas == null) {
            getAlertas();
        }
        
        try {
            guardarAlertas(alertas, rutaFichero);
            return alerta;
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    private List<AlertaImpl> cargarAlertas(String rutaFichero) throws Exception {
        /*InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);
        
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

        this.alertas = datosCargados.getAlertas();
        return alertas;
    }

    private void guardarAlertas(List<AlertaImpl> alertas, String rutaFichero) throws Exception {
        File ficheroJson = new File(rutaFichero);
        if(ficheroJson.getParentFile() != null) {
            ficheroJson.getParentFile().mkdirs();
        }

        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        datos.setAlertas(alertas);
        this.alertas = alertas;
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 
        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJson, datos);
    }
}