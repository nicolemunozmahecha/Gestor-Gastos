package tds.modelo.impl;

import java.util.ArrayList;
import java.util.List;

import tds.modelo.EstrategiaDistribucion;
import tds.modelo.Persona;


public class DistribucionEquitativaImpl implements EstrategiaDistribucion {
    public static final String ID = "EQUITATIVA";

    private final List<Persona> personas;

    public DistribucionEquitativaImpl(List<Persona> personas) {
        this.personas = new ArrayList<>(personas);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public double calcularPorcentaje(Persona persona) {
        if (!contienePersona(persona) || personas.isEmpty()) {
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
}
