package tds.importacion.csv;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import tds.importacion.GastoImportado;
import tds.importacion.ImportacionException;
import tds.importacion.ImportadorGastos;

// Adaptador específico para el formato csv

public class CsvImportadorGastosAdapter implements ImportadorGastos {

    private final CsvGastosReader reader;

    private static final DateTimeFormatter FECHA_HORA = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("M/d/yyyy")
            .optionalStart()
            .appendPattern(" H:mm")
            .optionalEnd()
            .toFormatter();

    public CsvImportadorGastosAdapter(CsvGastosReader reader) {
        this.reader = reader;
    }

    @Override
    public List<GastoImportado> importar(File fichero) throws ImportacionException {
        List<CsvGastoRow> rows = reader.leer(fichero);
        List<GastoImportado> out = new ArrayList<>();

        for (CsvGastoRow r : rows) {
            LocalDate fecha = parseFecha(r.date());
            double cantidad = parseCantidad(r.amount());

            String cuenta = normaliza(r.account());
            String categoria = normaliza(r.subcategory());
            if (categoria.isBlank()) {
                categoria = normaliza(r.category());
            }
            if (categoria.isBlank()) {
                categoria = "Sin categoría";
            }

            String nombre = normaliza(r.note());
            if (nombre.isBlank()) {
                nombre = categoria;
            }

            String descripcion = normaliza(r.category());

            String pagador = normaliza(r.payer());
            if (pagador.isBlank()) {
                pagador = "Me";
            }


            out.add(new GastoImportado(fecha, cuenta, categoria, nombre, descripcion, pagador, cantidad));
        }

        return out;
    }

    private static String normaliza(String s) {
        return s == null ? "" : s.trim();
    }

    private static LocalDate parseFecha(String raw) throws ImportacionException {
        String v = normaliza(raw);
        if (v.isBlank()) {
            throw new ImportacionException("Fecha vacía en CSV");
        }

        try {
            LocalDateTime dt = LocalDateTime.parse(v, FECHA_HORA);
            return dt.toLocalDate();
        } catch (DateTimeParseException ex) {

            throw new ImportacionException("Formato de fecha no válido: '" + v + "'", ex);
            
        }
    }

    private static double parseCantidad(String raw) throws ImportacionException {
        String v = normaliza(raw);
        if (v.isBlank()) {
            throw new ImportacionException("Cantidad vacía en CSV");
        }
        try {

            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            
            throw new ImportacionException("Cantidad no válida: '" + v + "'", e);
            
        }
    }
}
