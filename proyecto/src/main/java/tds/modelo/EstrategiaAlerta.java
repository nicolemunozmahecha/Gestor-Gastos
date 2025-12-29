package tds.modelo;

import java.time.LocalDate;

/**
 * Estrategia asociada a una alerta.
 *
 * En la práctica, las alertas pueden ser semanales o mensuales.
 * Este interfaz encapsula el cálculo del periodo (inicio/fin) para aplicar
 * el patrón Estrategia en el subsistema de alertas.
 */
public interface EstrategiaAlerta {

    /**
     * Devuelve el tipo de periodo que implementa la estrategia.
     */
    PeriodoAlerta getPeriodo();

    /**
     * Calcula la fecha de inicio del periodo para una fecha de referencia.
     */
    LocalDate calcularInicioPeriodo(LocalDate fechaReferencia);

    /**
     * Calcula la fecha de fin del periodo para una fecha de referencia.
     */
    LocalDate calcularFinPeriodo(LocalDate fechaReferencia);

    /**
     * Texto legible para el periodo ("semanal", "mensual"...).
     */
    String getNombrePeriodo();
}
