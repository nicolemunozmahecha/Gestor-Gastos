package tds.modelo.impl;

import tds.modelo.Filtro;
import tds.modelo.Gasto;
import tds.modelo.Categoria;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FiltroImpl implements Filtro {
    private List<String> meses; // Nombres de meses o números (1-12)
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Categoria> categorias;
    
    public FiltroImpl() {
        this.meses = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.fechaInicio = null;
        this.fechaFin = null;
    }
    
    public FiltroImpl(List<String> meses, LocalDate fechaInicio, LocalDate fechaFin, List<Categoria> categorias) {
        this.meses = meses != null ? new ArrayList<>(meses) : new ArrayList<>();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.categorias = categorias != null ? new ArrayList<>(categorias) : new ArrayList<>();
    }

    public FiltroImpl(LocalDate fechaInicio, LocalDate fechaFin) {
        this(null, fechaInicio, fechaFin, null);
    }

    public FiltroImpl(List<Categoria> categorias) {
        this(null, null, null, categorias);
    }
    
    public static FiltroImpl porMeses(List<String> meses) {
        return new FiltroImpl(meses, null, null, null);
    }

    @Override
    public List<Gasto> aplicar(List<Gasto> gastos) {
        if (gastos == null) {
            return new ArrayList<>();
        }
        
        return gastos.stream().filter(this::cumpleFiltro).collect(Collectors.toList());
    }
    
    @Override
    public boolean cumpleFiltro(Gasto gasto) {
        if (gasto == null) {
            return false;
        }
        
        // Si no hay filtros configurados, todos los gastos pasan
        if (!hayFiltrosActivos()) {
            return true;
        }
        
        // Verificar filtro de meses
        if (!meses.isEmpty() && !cumpleFiltroMes(gasto)) {
            return false;
        }
        
        // Verificar filtro de rango de fechas
        if (!cumpleFiltroFechas(gasto)) {
            return false;
        }
        
        // Verificar filtro de categorías
        if (!categorias.isEmpty() && !cumpleFiltroCategoria(gasto)) {
            return false;
        }
        
        return true;
    }
    
    private boolean cumpleFiltroMes(Gasto gasto) {
        if (meses.isEmpty()) {
            return true;
        }
        
        int mesGasto = gasto.getFecha().getMonthValue();
        String nombreMes = gasto.getFecha().getMonth().toString();
        
        for (String mes : meses) {
            // Verificar si es número de mes (1-12)
            try {
                int numeroMes = Integer.parseInt(mes);
                if (numeroMes == mesGasto) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // No es número, verificar como nombre
                if (nombreMes.equalsIgnoreCase(mes)) {
                    return true;
                }
            }
        }
        
        return false;
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
    
    private boolean cumpleFiltroCategoria(Gasto gasto) {
        if (categorias.isEmpty()) {
            return true;
        }
        
        return categorias.contains(gasto.getCategoria());
    }
    
    @Override
    public boolean hayFiltrosActivos() {
        return !meses.isEmpty() || fechaInicio != null || fechaFin != null || !categorias.isEmpty();
    }
    
    @Override
    public void limpiar() {
        meses.clear();
        categorias.clear();
        fechaInicio = null;
        fechaFin = null;
    }
    
    // Métodos para agregar filtros individuales
    @Override
    public void agregarMes(String mes) {
        if (mes != null && !mes.trim().isEmpty() && !meses.contains(mes)) {
            meses.add(mes);
        }
    }
    
    @Override
    public void agregarCategoria(Categoria categoria) {
        if (categoria != null && !categorias.contains(categoria)) {
            categorias.add(categoria);
        }
    }
    
    @Override
    public void setRangoFechas(LocalDate inicio, LocalDate fin) {
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }
    
    // Getters y Setters
    @Override
    public List<String> getMeses() {
        return new ArrayList<>(meses);
    }
    
    @Override
    public void setMeses(List<String> meses) {
        this.meses = meses != null ? new ArrayList<>(meses) : new ArrayList<>();
    }
    
    @Override
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    @Override
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    @Override
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    @Override
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    @Override
    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }
    
    @Override
    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias != null ? new ArrayList<>(categorias) : new ArrayList<>();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Filtro: ");
        List<String> criterios = new ArrayList<>();
        
        if (!meses.isEmpty()) {
            criterios.add("Meses: " + String.join(", ", meses));
        }
        if (fechaInicio != null || fechaFin != null) {
            String rangoFechas = "Fechas: ";
            if (fechaInicio != null) rangoFechas += "desde " + fechaInicio;
            if (fechaInicio != null && fechaFin != null) rangoFechas += " ";
            if (fechaFin != null) rangoFechas += "hasta " + fechaFin;
            criterios.add(rangoFechas);
        }
        if (!categorias.isEmpty()) {
            String cats = categorias.stream().map(Categoria::getNombre).collect(Collectors.joining(", "));
            criterios.add("Categorías: " + cats);
        }
        
        if (criterios.isEmpty()) {
            sb.append("Sin restricciones");
        } else {
            sb.append(String.join(" | ", criterios));
        }
        
        return sb.toString();
    }
}
