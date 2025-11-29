package tds.modelo.impl;

import tds.modelo.Categoria;
import java.util.Objects;

public class CategoriaImpl implements Categoria {
    private String nombre;
    private boolean predefinida;
    
    public CategoriaImpl(String nombre, boolean predefinida) {
        this.nombre = nombre;
        this.predefinida = predefinida;
    }
    
    public CategoriaImpl(String nombre) {
        this(nombre, false);
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
    public boolean isPredefinida() {
        return predefinida;
    }
    
    @Override
    public void setPredefinida(boolean predefinida) {
        this.predefinida = predefinida;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CategoriaImpl categoria = (CategoriaImpl) obj;
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
