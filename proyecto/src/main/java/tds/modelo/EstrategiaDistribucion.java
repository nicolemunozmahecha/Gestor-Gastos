package tds.modelo;

/**
 * Estrategia de distribución de un gasto en una cuenta compartida.
 *
 * Cada estrategia indica qué porcentaje (en fracción 0..1) le corresponde pagar
 * a cada participante.
 */
public interface EstrategiaDistribucion {

	// Identificador estable de la estrategia ("EQUITATIVA" o "PERSONALIZADA").
    String getId();

    // Porcentaje (en fracción 0..1) que le corresponde pagar a la persona.
    double calcularPorcentaje(Persona persona);

    boolean contienePersona(Persona persona);

    int getNumeroPersonas();
}
