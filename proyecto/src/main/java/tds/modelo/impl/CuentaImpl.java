package tds.modelo.impl;

import tds.modelo.Cuenta;
import tds.modelo.Categoria;
import tds.modelo.Gasto;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class CuentaImpl implements Cuenta {
    protected String nombre;
    protected List<Gasto> gastos;
    
    public CuentaImpl(String nombre) {
        this.nombre = nombre;
        this.gastos = new ArrayList<>();
    }
    
    @Override
    public void agregarGasto(Gasto gasto) {
        gastos.add(gasto);
    }
    
    @Override
    public boolean eliminarGasto(Gasto gasto) {
        return gastos.remove(gasto);
    }
    
    @Override
    public double calcularTotal() {
        return gastos.stream().mapToDouble(Gasto::getCantidad).sum();
    }
    
    @Override
    public double calcularTotal(Categoria categoria) {
        return gastos.stream().filter(g -> g.getCategoria().equals(categoria)).mapToDouble(Gasto::getCantidad).sum();
    }
    
    @Override
    public List<Gasto> getGastosPorCategoria(Categoria categoria) {
        return gastos.stream().filter(g -> g.getCategoria().equals(categoria)).collect(Collectors.toList());
    }
    
    @Override
    public boolean tieneGastos() {
        return !gastos.isEmpty();
    }
    
    @Override
    public int getNumeroGastos() {
        return gastos.size();
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
    public List<Gasto> getGastos() {
        return new ArrayList<>(gastos);
    }
    
    @Override
    public void limpiarGastos() {
        gastos.clear();
    }
    
    @Override
    public String toString() {
        return String.format("%s - %d gastos - Total: %.2f€", nombre, gastos.size(), calcularTotal());
    }
}
