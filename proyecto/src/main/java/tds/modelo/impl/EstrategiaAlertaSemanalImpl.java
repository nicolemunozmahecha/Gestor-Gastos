package tds.modelo.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import tds.modelo.EstrategiaAlerta;
import tds.modelo.PeriodoAlerta;

// Estrategia de alerta para periodo SEMANAL (lunes-domingo)
public class EstrategiaAlertaSemanalImpl implements EstrategiaAlerta {

    @Override
    public PeriodoAlerta getPeriodo() {
        return PeriodoAlerta.SEMANAL;
    }

    @Override
    public LocalDate calcularInicioPeriodo(LocalDate fechaReferencia) {
        if (fechaReferencia == null) return null;
        return fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @Override
    public LocalDate calcularFinPeriodo(LocalDate fechaReferencia) {
        if (fechaReferencia == null) return null;
        return fechaReferencia.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    @Override
    public String getNombrePeriodo() {
        return "semanal";
    }
}
