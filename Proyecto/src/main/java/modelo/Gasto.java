package modelo;

import java.time.LocalDate;
import java.util.Objects;

public class Gasto {
	
    private String nombre;
    private double cantidad;
    private LocalDate fecha;
    private String descripcion;
    private Categoria categoria;
    private Persona pagador;

    public Gasto(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria, Persona pagador) {

        this.nombre = nombre;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion != null ? descripcion : "";
        this.categoria = categoria;
        this.pagador = pagador;
    }

    public Gasto(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria) {
        this(nombre, cantidad, fecha, descripcion, categoria, null);
    }
    

    public Gasto(String nombre, double cantidad, Categoria categoria) {
        this(nombre, cantidad, LocalDate.now(), "", categoria, null);
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public double getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion : "";
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        this.categoria = categoria;
    }
    
    public Persona getPagador() {
        return pagador;
    }
    
    public void setPagador(Persona pagador) {
        this.pagador = pagador;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Gasto gasto = (Gasto) obj;
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
