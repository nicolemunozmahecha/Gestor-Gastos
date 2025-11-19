package modelo;

import java.util.Objects;


public class Alerta {
	
    private String nombre;
    private double limite;
    private PeriodoAlerta periodo;
    private Categoria categoria; // null si la alerta es general (no específica de categoría)    

    public Alerta(String nombre, double limite, PeriodoAlerta periodo) {
        this(nombre, limite, periodo, null);
    }
   
    
    public Alerta(String nombre, double limite, PeriodoAlerta periodo, Categoria categoria) {

        this.nombre = nombre;
        this.limite = limite;
        this.periodo = periodo;
        this.categoria = categoria;
    }
    

    public boolean superaLimite(double gastoActual) {
        return gastoActual > limite;
    }
    

    public boolean esGeneral() {
        return categoria == null;
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public double getLimite() {
        return limite;
    }
    
    public void setLimite(double limite) {
        this.limite = limite;
    }
    
    public PeriodoAlerta getPeriodo() {
        return periodo;
    }
    
    public void setPeriodo(PeriodoAlerta periodo) {
        this.periodo = periodo;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Alerta alerta = (Alerta) obj;
        return Objects.equals(nombre, alerta.nombre) &&
               periodo == alerta.periodo &&
               Objects.equals(categoria, alerta.categoria);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre, periodo, categoria);
    }
    
    @Override
    public String toString() {
        String tipo = esGeneral() ? "General" : "Categoría: " + categoria.getNombre();
        return String.format("Alerta: %s - Límite: %.2f€ - Período: %s - %s", nombre, limite, periodo, tipo);
    }
}
