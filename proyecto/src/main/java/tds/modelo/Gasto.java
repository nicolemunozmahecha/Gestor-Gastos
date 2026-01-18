package tds.modelo;

import java.time.LocalDate;

public interface Gasto {
    String getNombre();
    void setNombre(String nombre);
    double getCantidad();
    void setCantidad(double cantidad);
    LocalDate getFecha();
    void setFecha(LocalDate fecha);
    String getDescripcion();
    void setDescripcion(String descripcion);
    Categoria getCategoria();
    void setCategoria(Categoria categoria);
    Persona getPagador();
    void setPagador(Persona pagador);
    String getCuenta();
    void setCuenta(String cuenta);
}
