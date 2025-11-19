package modelo;

import java.util.Objects;


public class Persona {
    private String nombre;
    private double saldo;
    
    public Persona(String nombre) {
        this.nombre = nombre;
        this.saldo = 0.0;
    }

    public Persona(String nombre, double saldoInicial) {
        this(nombre);
        this.saldo = saldoInicial;
    }
    
    public void actualizarSaldo(double cantidad) {
        this.saldo += cantidad;
    }
    
    public void resetearSaldo() {
        this.saldo = 0.0;
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public double getSaldo() {
        return saldo;
    }
    
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Persona persona = (Persona) obj;
        return Objects.equals(nombre, persona.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
    
    @Override
    public String toString() {
        return String.format("%s (Saldo: %.2f€)", nombre, saldo);
    }
}
