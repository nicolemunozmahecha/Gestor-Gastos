package tds.modelo.impl;

import tds.modelo.Gasto;
import tds.modelo.Categoria;
import tds.modelo.Persona;
import java.time.LocalDate;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class GastoImpl implements Gasto {
	
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("cantidad")
    private double cantidad;
    
    @JsonProperty("fecha")
    private LocalDate fecha;
    
    @JsonProperty("descripcion")
    private String descripcion;
    
    @JsonProperty("categoria")
    private CategoriaImpl categoria;  // Usar implementación concreta
    
    @JsonProperty("pagador")
    private PersonaImpl pagador;  // Usar implementación concreta

    // Constructor sin argumentos para Jackson
    public GastoImpl() {
        this.nombre = "";
        this.cantidad = 0.0;
        this.fecha = LocalDate.now();
        this.descripcion = "";
        this.categoria = null;
        this.pagador = null;
    }

    public GastoImpl(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria, Persona pagador) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.descripcion = descripcion != null ? descripcion : "";
        // Convertir a implementaciones concretas
        this.categoria = categoria instanceof CategoriaImpl ? (CategoriaImpl) categoria : 
                        (categoria != null ? new CategoriaImpl(categoria.getNombre(), categoria.isPredefinida()) : null);
        this.pagador = pagador instanceof PersonaImpl ? (PersonaImpl) pagador :
                      (pagador != null ? new PersonaImpl(pagador.getNombre(), pagador.getSaldo()) : null);
    }

    public GastoImpl(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria) {
        this(nombre, cantidad, fecha, descripcion, categoria, null);
    }
    public GastoImpl(String nombre, double cantidad, LocalDate fecha, Categoria categoria) {
        this(nombre, cantidad, fecha, "", categoria, null);
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
    
    @JsonIgnore
    @Override
    public Categoria getCategoria() {
        return categoria;
    }
    
    @JsonIgnore
    @Override
    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        this.categoria = (CategoriaImpl) categoria;
    }
    
    @JsonIgnore
    @Override
    public Persona getPagador() {
        return pagador;
    }
    
    //TODO chequear si en lugar del casting es mejor cambiar el tipo del parametro
    @JsonIgnore
    @Override
    public void setPagador(Persona pagador) {
        this.pagador = (PersonaImpl) pagador;
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