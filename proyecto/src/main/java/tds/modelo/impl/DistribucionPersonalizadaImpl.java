package tds.modelo.impl;

import tds.modelo.EstrategiaDistribucion;
import tds.modelo.Persona;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class DistribucionPersonalizadaImpl implements EstrategiaDistribucion {
    private Map<Persona, Double> porcentajes;
    
    public DistribucionPersonalizadaImpl(Map<Persona, Double> porcentajes) {
        double suma = porcentajes.values().stream().mapToDouble(Double::doubleValue).sum();
        this.porcentajes = new HashMap<>(porcentajes);
    }

    public static DistribucionPersonalizadaImpl crearConPorcentajes(Map<Persona, Double> porcentajesPorCien) {
        Map<Persona, Double> porcentajesNormalizados = new HashMap<>();
        
        for (Map.Entry<Persona, Double> entry : porcentajesPorCien.entrySet()) {
            porcentajesNormalizados.put(entry.getKey(), entry.getValue() / 100.0);
        }
        
        return new DistribucionPersonalizadaImpl(porcentajesNormalizados);
    }
    
    @Override
    public double calcularPorcentaje(Persona persona) {
        return porcentajes.getOrDefault(persona, 0.0);
    }
    
    @Override
    public boolean contienePersona(Persona persona) {
        return porcentajes.containsKey(persona);
    }
    
    @Override
    public int getNumeroPersonas() {
        return porcentajes.size();
    }
    
    public double getPorcentajePorCien(Persona persona) {
        return calcularPorcentaje(persona) * 100;
    }
    
    public Map<Persona, Double> getPorcentajes() {
        return new HashMap<>(porcentajes);
    }
    
    public Set<Persona> getPersonas() {
        return porcentajes.keySet();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Distribución Personalizada: ");
        porcentajes.forEach((persona, porcentaje) -> 
            sb.append(String.format("%s: %.2f%%, ", persona.getNombre(), porcentaje * 100))
        );
        // Eliminar la última coma y espacio
        if (sb.length() > 27) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
