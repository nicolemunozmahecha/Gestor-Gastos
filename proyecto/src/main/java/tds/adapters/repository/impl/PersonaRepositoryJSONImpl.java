package tds.adapters.repository.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tds.Configuracion;
import tds.adapters.repository.PersonaRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.PersonaImpl;
import tds.modelo.impl.DatosGastos;

public class PersonaRepositoryJSONImpl implements PersonaRepository {
    
    private List<PersonaImpl> personas = null;
    private String rutaFichero;
    
    private void cargaPersonas() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
            this.personas = cargarPersonas(rutaFichero);
            if (personas == null) personas = new ArrayList<>();
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public List<? extends PersonaImpl> getPersonas() {
        if (personas == null) {
            try {
                cargaPersonas();
            } catch (ErrorPersistenciaException e) {
                // Manejo la excepcion pero no la propago
                personas = new ArrayList<>();
            }
        }
        return personas;
    }

    @Override
    public PersonaImpl findByNombre(String nombre) throws ElementoNoEncontradoException {
        if (personas == null) {
            getPersonas();
        }
        
        Optional<PersonaImpl> persona = personas.stream()
                .filter(p -> p.getNombre().equals(nombre))
                .findFirst();
        
        if (persona.isEmpty())
            throw new ElementoNoEncontradoException("La persona " + nombre + " no existe");
        return persona.orElse(null);
    }

    @Override
    public void addPersona(PersonaImpl persona) throws ElementoExistenteException, ErrorPersistenciaException {
        if (personas == null) {
            getPersonas();
        }
        
        // Si la persona ya existe no puedo insertarla
        if (personas.contains(persona)) {
            throw new ElementoExistenteException("La persona " + persona.getNombre() + " ya existe");
        }
        
        personas.add(persona);
        try {
            guardarPersonas(personas, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void removePersona(PersonaImpl persona) throws ErrorPersistenciaException {
        if (personas == null) {
            getPersonas();
        }
        
        if (!personas.contains(persona)) 
            return;
        
        personas.remove(persona);
        try {
            guardarPersonas(personas, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public PersonaImpl updatePersona(PersonaImpl persona) throws ErrorPersistenciaException {
        if (personas == null) {
            getPersonas();
        }
        
        try {
            guardarPersonas(personas, rutaFichero);
            return persona;
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    private List<PersonaImpl> cargarPersonas(String rutaFichero) throws Exception {
        InputStream ficheroStream = getClass().getResourceAsStream(rutaFichero);
        
        if (ficheroStream == null) {
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

        this.personas = datosCargados.getPersonas();
        return personas;
    }

    private void guardarPersonas(List<PersonaImpl> personas, String rutaFichero) throws Exception {
        // Se carga mediante URL para prevenir problemas con rutas con espacios en blanco o caracteres no estandar
        URL url = getClass().getResource(rutaFichero);
        
        // Cargo el fichero a partir de la URL local
        File ficheroJson = Paths.get(url.toURI()).toFile();

        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        datos.setPersonas(personas);
        this.personas = personas;
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJson, datos);
    }
}