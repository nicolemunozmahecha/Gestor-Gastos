package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import tds.modelo.Categoria;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Filtro vacío que no aplica ninguna restricción.
 * Sirve como componente base para el patrón decorador.
 */
public class FiltroVacio implements Filtro {
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        // Devolver todos los gastos sin filtrar
        return gastos != null ? new ArrayList<>(gastos) : new ArrayList<>();
    }
    
    @Override
    public boolean cumpleFiltro(Gasto gasto) {
        return true; // Acepta todos los gastos
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return false;
    }
    
    @Override
    public void limpiar() {
        // No hay nada que limpiar
    }
    
    @Override
    public void agregarMes(String mes) {
        // No soportado en filtro vacío
    }
    
    @Override
    public void agregarCategoria(Categoria categoria) {
        // No soportado en filtro vacío
    }
    
    @Override
    public void setRangoFechas(LocalDate inicio, LocalDate fin) {
        // No soportado en filtro vacío
    }
    
    @Override
    public List<String> getMeses() {
        return new ArrayList<>();
    }
    
    @Override
    public void setMeses(List<String> meses) {
        // No soportado en filtro vacío
    }
    
    @Override
    public LocalDate getFechaInicio() {
        return null;
    }
    
    @Override
    public void setFechaInicio(LocalDate fechaInicio) {
        // No soportado en filtro vacío
    }
    
    @Override
    public LocalDate getFechaFin() {
        return null;
    }
    
    @Override
    public void setFechaFin(LocalDate fechaFin) {
        // No soportado en filtro vacío
    }
    
    @Override
    public List<Categoria> getCategorias() {
        return new ArrayList<>();
    }
    
    @Override
    public void setCategorias(List<Categoria> categorias) {
        // No soportado en filtro vacío
    }
}
