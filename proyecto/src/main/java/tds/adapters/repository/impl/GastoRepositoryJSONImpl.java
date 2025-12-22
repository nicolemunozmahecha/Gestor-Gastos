package tds.adapters.repository.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tds.Configuracion;
import tds.adapters.repository.GastoRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.GastoImpl;
import tds.modelo.impl.DatosGastos;

public class GastoRepositoryJSONImpl implements GastoRepository {
    
    private List<GastoImpl> gastos = null;
    private String rutaFichero;
    
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
                // Manejo la excepcion pero no la propago
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
        
        Optional<GastoImpl> gasto = gastos.stream().filter(g -> g.getNombre() == nombre).findFirst();
    
        if (gasto.isEmpty())
            throw new ElementoNoEncontradoException("El gasto con id " + nombre + " no existe");
        return gasto.orElse(null);
    }

    @Override
    public void addGasto(GastoImpl gasto) throws ElementoExistenteException, ErrorPersistenciaException {
        if (gastos == null) {
            getGastos();
        }
        
        // Si el gasto ya existe no puedo insertarlo
        if (gastos.contains(gasto)) {
            throw new ElementoExistenteException("El gasto " + gasto.getNombre() + " ya existe");
        }
        
        gastos.add(gasto);
        try {
            guardarGastos(gastos, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void removeGasto(GastoImpl gasto) throws ErrorPersistenciaException {
        if (gastos == null) {
            getGastos();
        }
        
        if (!gastos.contains(gasto)) 
            return;
        
        gastos.remove(gasto);
        try {
            guardarGastos(gastos, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public GastoImpl updateGasto(GastoImpl gasto) throws ErrorPersistenciaException {
        if (gastos == null) {
            getGastos();
        }
        
        try {
            guardarGastos(gastos, rutaFichero);
            return gasto;
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    private List<GastoImpl> cargarGastos(String rutaFichero) throws Exception {
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
        // NUEVO PARA EL ERROR DE FECHA:
        mapper.registerModule(new JavaTimeModule());
        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        
        DatosGastos datosCargados = mapper.readValue(ficheroStream, new TypeReference<DatosGastos>() {});
        datos.setGastos(datosCargados.getGastos());
        datos.setCuentas(datosCargados.getCuentas());
        datos.setCategorias(datosCargados.getCategorias());
        datos.setAlertas(datosCargados.getAlertas());
        datos.setPersonas(datosCargados.getPersonas());

        this.gastos = datosCargados.getGastos();
        return gastos;
    }

    private void guardarGastos(List<GastoImpl> gastos, String rutaFichero) throws Exception {
        // Se carga mediante URL para prevenir problemas con rutas con espacios en blanco o caracteres no estandar
        /*URL url = getClass().getResource(rutaFichero);
        
        // Cargo el fichero a partir de la URL local
        File ficheroJson = Paths.get(url.toURI()).toFile();*/
    	File ficheroJson = new File(rutaFichero);
    	if(ficheroJson.getParentFile() != null) {
    		ficheroJson.getParentFile().mkdirs();
    	}

        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        datos.setGastos(gastos);
        this.gastos = gastos;
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); 
        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJson, datos);
    }
}