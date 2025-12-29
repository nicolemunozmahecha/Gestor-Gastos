package tds.modelo.estrategias;

import tds.modelo.EstrategiaAlerta;
import tds.modelo.PeriodoAlerta;
import tds.modelo.impl.EstrategiaAlertaMensualImpl;
import tds.modelo.impl.EstrategiaAlertaSemanalImpl;

public final class EstrategiaAlertaFactory {

    private EstrategiaAlertaFactory() {
    }

    public static EstrategiaAlerta crear(PeriodoAlerta periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo no puede ser null");
        }
        if (PeriodoAlerta.SEMANAL.equals(periodo)) {
            return new EstrategiaAlertaSemanalImpl();
        }
        if (PeriodoAlerta.MENSUAL.equals(periodo)) {
            return new EstrategiaAlertaMensualImpl();
        }
        throw new IllegalArgumentException("Periodo desconocido: " + periodo);
    }

    public static EstrategiaAlerta crearOporDefecto(PeriodoAlerta periodo) {
        try {
            return crear(periodo == null ? PeriodoAlerta.MENSUAL : periodo);
        } catch (Exception e) {
            return new EstrategiaAlertaMensualImpl();
        }
    }
}
