package modelo;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class Cuenta {
    protected String nombre;
    protected List<Gasto> gastos;
    
    
    public Cuenta(String nombre) {
        this.nombre = nombre;
        this.gastos = new ArrayList<>();
    }
    

    public void agregarGasto(Gasto gasto) {
        gastos.add(gasto);
    }
    
    public boolean eliminarGasto(Gasto gasto) {
        return gastos.remove(gasto);
    }
    

    public double calcularTotal() {
        return gastos.stream().mapToDouble(Gasto::getCantidad).sum();
    }
    
    public double calcularTotal(Categoria categoria) {
        return gastos.stream().filter(g -> g.getCategoria().equals(categoria)).mapToDouble(Gasto::getCantidad).sum();
    }
    
    public List<Gasto> getGastosPorCategoria(Categoria categoria) {
        return gastos.stream().filter(g -> g.getCategoria().equals(categoria)).collect(Collectors.toList());
    }
    
    public boolean tieneGastos() {
        return !gastos.isEmpty();
    }
    
    
    public int getNumeroGastos() {
        return gastos.size();
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public List<Gasto> getGastos() {
        return new ArrayList<>(gastos);
    }
    
    public void limpiarGastos() {
        gastos.clear();
    }
    
    @Override
    public String toString() {
        return String.format("%s - %d gastos - Total: %.2f€", nombre, gastos.size(), calcularTotal());
    }
}
