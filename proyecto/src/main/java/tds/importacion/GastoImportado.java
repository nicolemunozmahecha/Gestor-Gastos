package tds.importacion;

import java.time.LocalDate;

// Objeto que representa el gasto importado independientemente del formato original (y antes de crear el gasto)
public record GastoImportado(
        LocalDate fecha,
        String cuenta,
        String categoria,
        String nombre,
        String descripcion,
        String pagador,
        double cantidad
) {
}
