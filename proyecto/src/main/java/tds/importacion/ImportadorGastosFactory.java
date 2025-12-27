package tds.importacion;

import java.io.File;

import tds.importacion.csv.CsvGastosReader;
import tds.importacion.csv.CsvImportadorGastosAdapter;

// Método para crear el adaptador concreto según la extensión del fichero (si se quisiese añadir otro formato sería aquí)
public final class ImportadorGastosFactory {

    private ImportadorGastosFactory() {}

    public static ImportadorGastos crear(File fichero) throws ImportacionException {
        if (fichero == null) {
            throw new ImportacionException("No se ha seleccionado ningún fichero");
        }

        String nombre = fichero.getName().toLowerCase();
        if (nombre.endsWith(".csv")) {
            return new CsvImportadorGastosAdapter(new CsvGastosReader());
        }

        throw new ImportacionException("Formato de fichero no soportado: " + fichero.getName());
    }
}
