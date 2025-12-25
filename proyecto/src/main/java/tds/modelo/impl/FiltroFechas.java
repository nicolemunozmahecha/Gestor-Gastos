package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Decorador que añade filtrado por rango de fechas.
 */
public class FiltroFechas extends FiltroBase {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    public FiltroFechas(Filtro filtro, LocalDate fechaInicio, LocalDate fechaFin) {
        super(filtro);
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        // Primero aplicar el filtro envuelto
        List<Gasto> gastosFiltrados = super.aplicar(gastos);
        
        // Aplicar filtro de fechas
        return gastosFiltrados.stream()
            .filter(this::cumpleFiltroFechas)
            .collect(Collectors.toList());
    }
    
    private boolean cumpleFiltroFechas(Gasto gasto) {
        LocalDate fechaGasto = gasto.getFecha();
        
        if (fechaInicio != null && fechaGasto.isBefore(fechaInicio)) {
            return false;
        }
        
        if (fechaFin != null && fechaGasto.isAfter(fechaFin)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return (fechaInicio != null || fechaFin != null) || super.hayFiltrosActivos();
    }
}
