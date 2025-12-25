package tds.modelo.estrategias;

import java.util.List;

import tds.modelo.EstrategiaDistribucion;
import tds.modelo.Persona;
import tds.modelo.impl.DistribucionEquitativaImpl;
import tds.modelo.impl.DistribucionPersonalizadaImpl;

/**
 * Factoría sencilla de estrategias de distribución.
 *
 * - No usa reflexión ni escaneo de clases.
 * - El controlador solo maneja IDs (String) y la factoría devuelve la estrategia.
 */
public final class EstrategiaDistribucionFactory {

    private EstrategiaDistribucionFactory() {
    }

    public static EstrategiaDistribucion crear(String id, List<Persona> participantes) {
        if (id == null) {
            throw new IllegalArgumentException("El id de estrategia no puede ser null");
        }
        if (DistribucionEquitativaImpl.ID.equals(id)) {
            return new DistribucionEquitativaImpl(participantes);
        }
        if (DistribucionPersonalizadaImpl.ID.equals(id)) {
            return DistribucionPersonalizadaImpl.desdePersonas(participantes);
        }
        throw new IllegalArgumentException("Estrategia desconocida: " + id);
    }

    public static EstrategiaDistribucion crearOporDefecto(String id, List<Persona> participantes) {
        String usar = (id == null || id.isBlank()) ? DistribucionEquitativaImpl.ID : id;
        try {
            return crear(usar, participantes);
        } catch (Exception e) {
            // Si la estrategia solicitada falla, devolvemos equitativa.
            return new DistribucionEquitativaImpl(participantes);
        }
    }
}
