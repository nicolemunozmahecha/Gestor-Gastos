package tds.modelo.impl;

import tds.modelo.Alerta;
import tds.modelo.Categoria;
import tds.modelo.PeriodoAlerta;
import java.util.Objects;

public class AlertaImpl implements Alerta {
	
    private String nombre;
    private double limite;
    private PeriodoAlerta periodo;
    private Categoria categoria; // null si la alerta es general (no específica de categoría)    

    public AlertaImpl(String nombre, double limite, PeriodoAlerta periodo) {
        this(nombre, limite, periodo, null);
    }
   
    public AlertaImpl(String nombre, double limite, PeriodoAlerta periodo, Categoria categoria) {
        this.nombre = nombre;
        this.limite = limite;
        this.periodo = periodo;
        this.categoria = categoria;
    }
    
    @Override
    public boolean superaLimite(double gastoActual) {
        return gastoActual > limite;
    }
    
    @Override
    public boolean esGeneral() {
        return categoria == null;
    }
    
    // Getters y Setters
    @Override
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public double getLimite() {
        return limite;
    }
    
    @Override
    public void setLimite(double limite) {
        this.limite = limite;
    }
    
    @Override
    public PeriodoAlerta getPeriodo() {
        return periodo;
    }
    
    @Override
    public void setPeriodo(PeriodoAlerta periodo) {
        this.periodo = periodo;
    }
    
    @Override
    public Categoria getCategoria() {
        return categoria;
    }
    
    @Override
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlertaImpl alerta = (AlertaImpl) obj;
        return Double.compare(alerta.limite, limite) == 0 &&
               Objects.equals(nombre, alerta.nombre) &&
               periodo == alerta.periodo &&
               Objects.equals(categoria, alerta.categoria);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre, limite, periodo, categoria);
    }
    
    @Override
    public String toString() {
        String tipo = esGeneral() ? "General" : "Categoría: " + categoria.getNombre();
        return String.format("%s - %s - Límite: %.2f€ - Periodo: %s", nombre, tipo, limite, periodo);
    }
}
