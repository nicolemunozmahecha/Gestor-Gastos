package modelo;

import java.util.List;
import java.util.ArrayList;

public class DistribucionEquitativa implements EstrategiaDistribucion {
    private List<Persona> personas;

    public DistribucionEquitativa(List<Persona> personas) {
        this.personas = new ArrayList<>(personas);
    }
    
    @Override
    public double calcularPorcentaje(Persona persona) {
        if (!contienePersona(persona)) {
            return 0.0;
        }
        return 1.0 / personas.size();
    }
    
    @Override
    public boolean contienePersona(Persona persona) {
        return personas.contains(persona);
    }
    
    @Override
    public int getNumeroPersonas() {
        return personas.size();
    }
    
    public List<Persona> getPersonas() {
        return new ArrayList<>(personas);
    }
    
    @Override
    public String toString() {
        return String.format("Distribución Equitativa entre %d personas (%.2f%% cada una)", personas.size(), calcularPorcentaje(personas.get(0)) * 100);
    }
}
