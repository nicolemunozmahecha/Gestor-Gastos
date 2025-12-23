package tds.adapters.repository;

import java.util.List;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.CategoriaImpl;

public interface CategoriaRepository {

    /**
     * Devuelve una lista con todas las categorias cargadas
     * 
     * @return Lista de categorias
     */
   // List<? extends CategoriaImpl> getCategorias();
	 List<CategoriaImpl> getCategorias();
    /**
     * Busca una categoria por su nombre y la devuelve
     * 
     * @param nombre
     * @throws ElementoNoEncontradoException si no encuentra la categoria buscada
     * @return Categoria
     */
    CategoriaImpl findByNombre(String nombre) throws ElementoNoEncontradoException;

    /**
     * Añade una categoria 
     * 
     * @param categoria
     * @throws ElementoExistenteException si existe una categoria igual en el repositorio
     * @return void
     */
    void addCategoria(CategoriaImpl categoria) throws ElementoExistenteException, ErrorPersistenciaException;
    
    /**
     * Elimina una categoria del repositorio
     * 
     * @param categoria
     * @return void
     */
    void removeCategoria(CategoriaImpl categoria) throws ErrorPersistenciaException;
    
    /**
     * Actualiza una categoria en el repositorio
     * 
     * @param categoria
     * @return Categoria
     */
    CategoriaImpl updateCategoria(CategoriaImpl categoria) throws ErrorPersistenciaException;
}
