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
import tds.adapters.repository.CategoriaRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CategoriaImpl;
import tds.modelo.impl.DatosGastos;

public class CategoriaRepositoryJSONImpl implements CategoriaRepository {
    
    private List<CategoriaImpl> categorias = null;
    private String rutaFichero;
    
    private void cargaCategorias() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
            this.categorias = cargarCategorias(rutaFichero);
            if (categorias == null) categorias = new ArrayList<>();
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public List<? extends CategoriaImpl> getCategorias() {
        if (categorias == null) {
            try {
                cargaCategorias();
            } catch (ErrorPersistenciaException e) {
                // Manejo la excepcion pero no la propago
                categorias = new ArrayList<>();
            }
        }
        return categorias;
    }

    @Override
    public CategoriaImpl findByNombre(String nombre) throws ElementoNoEncontradoException {
        if (categorias == null) {
            getCategorias();
        }
        
        Optional<CategoriaImpl> categoria = categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
        
        if (categoria.isEmpty())
            throw new ElementoNoEncontradoException("La categoria " + nombre + " no existe");
        return categoria.orElse(null);
    }

    @Override
    public void addCategoria(CategoriaImpl categoria) throws ElementoExistenteException, ErrorPersistenciaException {
        if (categorias == null) {
            getCategorias();
        }
        
        // Si la categoria ya existe no puedo insertarla
        if (categorias.contains(categoria)) {
            throw new ElementoExistenteException("La categoria " + categoria.getNombre() + " ya existe");
        }
        
        categorias.add(categoria);
        try {
            guardarCategorias(categorias, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public void removeCategoria(CategoriaImpl categoria) throws ErrorPersistenciaException {
        if (categorias == null) {
            getCategorias();
        }
        
        if (!categorias.contains(categoria)) {
            return;
        }
        
        categorias.remove(categoria);
        try {
            guardarCategorias(categorias, rutaFichero);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public CategoriaImpl updateCategoria(CategoriaImpl categoria) throws ErrorPersistenciaException {
        if (categorias == null) {
            getCategorias();
        }
        
        try {
            guardarCategorias(categorias, rutaFichero);
            return categoria;
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    private List<CategoriaImpl> cargarCategorias(String rutaFichero) throws Exception {
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

        this.categorias = datosCargados.getCategorias();
        return categorias;
    }

    private void guardarCategorias(List<CategoriaImpl> categorias, String rutaFichero) throws Exception {
        // Se carga mediante URL para prevenir problemas con rutas con espacios en blanco
        URL url = getClass().getResource(rutaFichero);
        try {
            // Cargo el fichero a partir de la URL local
            File ficheroJSon = Paths.get(url.toURI()).toFile();
    
            DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
            datos.setCategorias(categorias);
            this.categorias = categorias;
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJSon, datos);

        } catch (Exception e) {
            throw e;
        }
    }
}
