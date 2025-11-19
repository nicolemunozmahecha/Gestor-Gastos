package modelo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class CuentaCompartida extends Cuenta {
    private List<Persona> personas;
    private EstrategiaDistribucion estrategia;
    
    public CuentaCompartida(String nombre, List<Persona> personas) {
        super(nombre);
        
        this.personas = new ArrayList<>(personas);
        this.estrategia = new DistribucionEquitativa(this.personas);
        inicializarSaldos();
    }
    
    public CuentaCompartida(String nombre, List<Persona> personas, EstrategiaDistribucion estrategia) {
        super(nombre);
        this.personas = new ArrayList<>(personas);
        this.estrategia = estrategia;
        inicializarSaldos();
    }
    
    private void inicializarSaldos() {
        for (Persona persona : personas) {
            persona.setSaldo(0.0);
        }
    }
    
    @Override
    public void agregarGasto(Gasto gasto) {

        Persona pagador = gasto.getPagador();
        super.agregarGasto(gasto);
        actualizarSaldos(gasto);
    }
    
    @Override
    public boolean eliminarGasto(Gasto gasto) {
        boolean eliminado = super.eliminarGasto(gasto);
        if (eliminado) {
            recalcularTodosSaldos();
        }
        return eliminado;
    }
    
    private void actualizarSaldos(Gasto gasto) {
        Persona pagador = gasto.getPagador();
        double cantidad = gasto.getCantidad();
        
        for (Persona persona : personas) {
            double porcentaje = estrategia.calcularPorcentaje(persona);
            double deuda = cantidad * porcentaje;
            
            if (persona.equals(pagador)) {
                persona.actualizarSaldo(cantidad - deuda);
            } else {
                persona.actualizarSaldo(-deuda);
            }
        }
    }

    private void recalcularTodosSaldos() {
        inicializarSaldos();
        for (Gasto gasto : gastos) {
            actualizarSaldos(gasto);
        }
    }
    
    public Map<Persona, Double> calcularSaldos() {
        Map<Persona, Double> saldos = new HashMap<>();
        for (Persona persona : personas) {
            saldos.put(persona, persona.getSaldo());
        }
        return saldos;
    }

    public double getSaldo(Persona persona) {
        return persona.getSaldo();
    }
    
    double getSaldo(String nombrePersona) {
        for (Persona persona : personas) {
            if (persona.getNombre().equals(nombrePersona)) {
                return persona.getSaldo();
            }
        }
        throw new IllegalArgumentException(
            "No existe una persona con ese nombre en la cuenta"
        );
    }
    
    public double getTotalGastadoPor(Persona persona) {
        return gastos.stream().filter(g -> persona.equals(g.getPagador())).mapToDouble(Gasto::getCantidad).sum();
    }
    
    // Getters y Setters
    public List<Persona> getPersonas() {
        return Collections.unmodifiableList(personas);
    }
    
    public EstrategiaDistribucion getEstrategiaDistribucion() {
        return estrategia;
    }
    
    public void setEstrategiaDistribucion(EstrategiaDistribucion estrategia) {
        this.estrategia = estrategia;
        recalcularTodosSaldos();
    }
    
    public boolean contienePersona(Persona persona) {
        return personas.contains(persona);
    }
    
    public int getNumeroPersonas() {
        return personas.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Cuenta Compartida: %s - %d personas - %d gastos - Total: %.2f€\n", 
                              nombre, personas.size(), gastos.size(), calcularTotal()));
        sb.append("Saldos: ");
        for (Persona persona : personas) {
            sb.append(String.format("%s: %.2f€, ", persona.getNombre(), persona.getSaldo()));
        }
        // Eliminar la última coma y espacio
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
