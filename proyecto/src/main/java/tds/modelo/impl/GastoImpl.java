package tds.modelo.impl;

import tds.modelo.Gasto;
import tds.modelo.Categoria;
import tds.modelo.Persona;
import java.time.LocalDate;
import java.util.Objects;

public class GastoImpl implements Gasto {
	
    private String nombre;
    private double cantidad;
    private LocalDate fecha;
    private String descripcion;
    private Categoria categoria;
    private Persona pagador;

    public GastoImpl(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria, Persona pagador) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion != null ? descripcion : "";
        this.categoria = categoria;
        this.pagador = pagador;
    }

    public GastoImpl(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria) {
        this(nombre, cantidad, fecha, descripcion, categoria, null);
    }
    
    public GastoImpl(String nombre, double cantidad, Categoria categoria) {
        this(nombre, cantidad, LocalDate.now(), "", categoria, null);
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
    public double getCantidad() {
        return cantidad;
    }
    
    @Override
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
    
    @Override
    public LocalDate getFecha() {
        return fecha;
    }
    
    @Override
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    @Override
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion : "";
    }
    
    @Override
    public Categoria getCategoria() {
        return categoria;
    }
    
    @Override
    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        this.categoria = categoria;
    }
    
    @Override
    public Persona getPagador() {
        return pagador;
    }
    
    @Override
    public void setPagador(Persona pagador) {
        this.pagador = pagador;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GastoImpl gasto = (GastoImpl) obj;
        return Double.compare(gasto.cantidad, cantidad) == 0 &&
               Objects.equals(nombre, gasto.nombre) &&
               Objects.equals(fecha, gasto.fecha) &&
               Objects.equals(categoria, gasto.categoria);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre, cantidad, fecha, categoria);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %.2f€ - %s - %s", 
                           nombre, cantidad, categoria.getNombre(), fecha);
    }
}
