package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import tds.modelo.Categoria;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase base abstracta para el patrón Decorador de filtros.
 * Implementa Filtro delegando las operaciones al filtro envuelto.
 */
public abstract class FiltroBase implements Filtro {
    protected Filtro filtroEnvuelto;
    
    public FiltroBase(Filtro filtro) {
        this.filtroEnvuelto = filtro;
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        // Delegar al filtro envuelto
        return filtroEnvuelto.aplicar(gastos);
    }
    
    @Override
    public boolean cumpleFiltro(Gasto gasto) {
        return filtroEnvuelto.cumpleFiltro(gasto);
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return filtroEnvuelto.hayFiltrosActivos();
    }
    
    @Override
    public void limpiar() {
        filtroEnvuelto.limpiar();
    }
    
    @Override
    public void agregarMes(String mes) {
        filtroEnvuelto.agregarMes(mes);
    }
    
    @Override
    public void agregarCategoria(Categoria categoria) {
        filtroEnvuelto.agregarCategoria(categoria);
    }
    
    @Override
    public void setRangoFechas(LocalDate inicio, LocalDate fin) {
        filtroEnvuelto.setRangoFechas(inicio, fin);
    }
    
    @Override
    public List<String> getMeses() {
        return filtroEnvuelto.getMeses();
    }
    
    @Override
    public void setMeses(List<String> meses) {
        filtroEnvuelto.setMeses(meses);
    }
    
    @Override
    public LocalDate getFechaInicio() {
        return filtroEnvuelto.getFechaInicio();
    }
    
    @Override
    public void setFechaInicio(LocalDate fechaInicio) {
        filtroEnvuelto.setFechaInicio(fechaInicio);
    }
    
    @Override
    public LocalDate getFechaFin() {
        return filtroEnvuelto.getFechaFin();
    }
    
    @Override
    public void setFechaFin(LocalDate fechaFin) {
        filtroEnvuelto.setFechaFin(fechaFin);
    }
    
    @Override
    public List<Categoria> getCategorias() {
        return filtroEnvuelto.getCategorias();
    }
    
    @Override
    public void setCategorias(List<Categoria> categorias) {
        filtroEnvuelto.setCategorias(categorias);
    }
}
