package tds.modelo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tds.modelo.EstrategiaDistribucion;
import tds.modelo.Persona;


public class DistribucionPersonalizadaImpl implements EstrategiaDistribucion {
    public static final String ID = "PERSONALIZADA";

    private final Map<Persona, Double> porcentajesFraccion; // 0..1

    public DistribucionPersonalizadaImpl(Map<Persona, Double> porcentajesFraccion) {
        if (porcentajesFraccion == null || porcentajesFraccion.isEmpty()) {
            throw new IllegalArgumentException("La distribución personalizada requiere porcentajes");
        }

        double suma = 0.0;
        for (double v : porcentajesFraccion.values()) {
            if (v < 0) {
                throw new IllegalArgumentException("Los porcentajes no pueden ser negativos");
            }
            suma += v;
        }

        if (Math.abs(suma - 1.0) > 1e-6) {
            throw new IllegalArgumentException("La suma de porcentajes (en fracción) debe ser 1.0. Suma=" + suma);
        }

        this.porcentajesFraccion = new HashMap<>(porcentajesFraccion);
    }

    public static DistribucionPersonalizadaImpl desdePersonas(List<Persona> participantes) {
        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("No hay participantes para una distribución personalizada");
        }

        int suma = 0;
        Map<Persona, Double> fraccion = new HashMap<>();
        for (Persona p : new ArrayList<>(participantes)) {
            int pct = p.getPorcentaje();
            if (pct < 0) {
                throw new IllegalArgumentException("Los porcentajes no pueden ser negativos");
            }
            suma += pct;
            fraccion.put(p, pct / 100.0);
        }

        if (suma != 100) {
            throw new IllegalArgumentException("Los porcentajes deben sumar 100. Suma=" + suma);
        }

        return new DistribucionPersonalizadaImpl(fraccion);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public double calcularPorcentaje(Persona persona) {
        return porcentajesFraccion.getOrDefault(persona, 0.0);
    }

    @Override
    public boolean contienePersona(Persona persona) {
        return porcentajesFraccion.containsKey(persona);
    }

    @Override
    public int getNumeroPersonas() {
        return porcentajesFraccion.size();
    }

    public Map<Persona, Double> getPorcentajesFraccion() {
        return new HashMap<>(porcentajesFraccion);
    }
}
