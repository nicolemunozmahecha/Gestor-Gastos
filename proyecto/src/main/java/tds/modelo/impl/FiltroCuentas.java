package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Decorador que añade filtrado por categorías.
 */
public class FiltroCuentas extends FiltroBase {
    private List<String> cuentas;
    
    public FiltroCuentas(Filtro filtro, List<String> cuentas) {
        super(filtro);
        this.cuentas = cuentas;
    }
    
    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        // Primero aplicar el filtro envuelto
        List<Gasto> gastosFiltrados = super.aplicar(gastos);
        
        // Si no hay categorías especificadas, devolver tal cual
        if (cuentas == null || cuentas.isEmpty()) {
            return gastosFiltrados;
        }
        
        // Aplicar filtro de categorías
        return gastosFiltrados.stream()
            .filter(gasto -> cuentas.contains(gasto.getCuenta()))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return (cuentas != null && !cuentas.isEmpty()) || super.hayFiltrosActivos();
    }
}
