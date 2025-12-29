package tds.modelo.impl;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import tds.modelo.EstrategiaAlerta;
import tds.modelo.PeriodoAlerta;

// Estrategia de alerta para periodo MENSUAL.

public class EstrategiaAlertaMensualImpl implements EstrategiaAlerta {

    @Override
    public PeriodoAlerta getPeriodo() {
        return PeriodoAlerta.MENSUAL;
    }

    @Override
    public LocalDate calcularInicioPeriodo(LocalDate fechaReferencia) {
        if (fechaReferencia == null) return null;
        return fechaReferencia.with(TemporalAdjusters.firstDayOfMonth());
    }

    @Override
    public LocalDate calcularFinPeriodo(LocalDate fechaReferencia) {
        if (fechaReferencia == null) return null;
        return fechaReferencia.with(TemporalAdjusters.lastDayOfMonth());
    }

    @Override
    public String getNombrePeriodo() {
        return "mensual";
    }
}
