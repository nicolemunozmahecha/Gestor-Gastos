package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import tds.modelo.Categoria;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Decorador que añade filtrado por categorías.
 */
public class FiltroCategorias extends FiltroBase {
    private List<Categoria> categorias;
    
    public FiltroCategorias(Filtro filtro, List<Categoria> categorias) {
        super(filtro);
        this.categorias = categorias;
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        // Primero aplicar el filtro envuelto
        List<Gasto> gastosFiltrados = super.aplicar(gastos);
        
        // Si no hay categorías especificadas, devolver tal cual
        if (categorias == null || categorias.isEmpty()) {
            return gastosFiltrados;
        }
        
        // Aplicar filtro de categorías
        return gastosFiltrados.stream()
            .filter(gasto -> categorias.contains(gasto.getCategoria()))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return (categorias != null && !categorias.isEmpty()) || super.hayFiltrosActivos();
    }
}
