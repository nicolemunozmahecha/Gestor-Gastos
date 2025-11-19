package modelo;

import java.util.Objects;

public class Categoria {
    private String nombre;
    private boolean predefinida;
    
    public Categoria(String nombre, boolean predefinida) {
        this.nombre = nombre;
        this.predefinida = predefinida;
    }
    
    public Categoria(String nombre) {
        this(nombre, false);
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public boolean isPredefinida() {
        return predefinida;
    }
    
    public void setPredefinida(boolean predefinida) {
        this.predefinida = predefinida;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return Objects.equals(nombre, categoria.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
    
    @Override
    public String toString() {
        return nombre + (predefinida ? " (Predefinida)" : "");
    }
}
