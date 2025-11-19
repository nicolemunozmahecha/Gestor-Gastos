package modelo;

/**
 * Interfaz que define la estrategia de distribución de gastos en cuentas compartidas.
 * Implementa el patrón estrategia
 */
public interface EstrategiaDistribucion {
    

    double calcularPorcentaje(Persona persona);
    
    boolean contienePersona(Persona persona);
    
    int getNumeroPersonas();
}
