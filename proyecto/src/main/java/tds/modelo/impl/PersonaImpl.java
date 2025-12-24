package tds.modelo.impl;

import tds.modelo.Persona;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonaImpl implements Persona {
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("saldo")
    private double saldo;
    
    @JsonProperty("porcentaje")
    private double porcentaje;
    
    // Constructor sin argumentos para Jackson
    public PersonaImpl() {
        this.nombre = "";
        this.saldo = 0.0;
        this.porcentaje = 0.0;
    }
    
    public PersonaImpl(String nombre) {
        this.nombre = nombre;
        this.saldo = 0.0;
        this.porcentaje = 0.0;
    }

    public PersonaImpl(String nombre, double saldoInicial) {
        this(nombre);
        this.saldo = saldoInicial;
    }
    
    @Override
    public void actualizarSaldo(double cantidad) {
        this.saldo += cantidad;
    }
    
    @Override
    public void resetearSaldo() {
        this.saldo = 0.0;
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
    public double getSaldo() {
        return saldo;
    }
    @Override
    public double getPorcentaje() {
        return porcentaje;
    }
    @Override
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    @Override
    public void setPorcentaje(double p) {
        this.porcentaje = p;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PersonaImpl persona = (PersonaImpl) obj;
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