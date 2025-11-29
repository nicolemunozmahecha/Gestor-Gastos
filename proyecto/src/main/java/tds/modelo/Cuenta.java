package tds.modelo;

import java.util.List;

public interface Cuenta {
    void agregarGasto(Gasto gasto);
    boolean eliminarGasto(Gasto gasto);
    double calcularTotal();
    double calcularTotal(Categoria categoria);
    List<Gasto> getGastosPorCategoria(Categoria categoria);
    boolean tieneGastos();
    int getNumeroGastos();
    String getNombre();
    void setNombre(String nombre);
    List<Gasto> getGastos();
    void limpiarGastos();
}
