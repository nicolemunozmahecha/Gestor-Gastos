package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Decorador que añade filtrado por meses.
 */
public class FiltroMeses extends FiltroBase {
    private List<String> meses;
    
    public FiltroMeses(Filtro filtro, List<String> meses) {
        super(filtro);
        this.meses = meses;
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        // Primero aplicar el filtro envuelto
        List<Gasto> gastosFiltrados = super.aplicar(gastos);
        
        // Si no hay meses especificados, devolver tal cual
        if (meses == null || meses.isEmpty()) {
            return gastosFiltrados;
        }
        
        // Aplicar filtro de meses
        return gastosFiltrados.stream()
            .filter(this::cumpleFiltroMes)
            .collect(Collectors.toList());
    }
    
    private boolean cumpleFiltroMes(Gasto gasto) {
        int mesGasto = gasto.getFecha().getMonthValue();
        String nombreMes = gasto.getFecha().getMonth().toString();
        
        for (String mes : meses) {
            try {
                int numeroMes = Integer.parseInt(mes);
                if (numeroMes == mesGasto) {
                    return true;
                }
            } catch (NumberFormatException e) {
                if (nombreMes.equalsIgnoreCase(mes)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return (meses != null && !meses.isEmpty()) || super.hayFiltrosActivos();
    }
}
