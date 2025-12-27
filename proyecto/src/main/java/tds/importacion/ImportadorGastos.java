package tds.importacion;

import java.io.File;
import java.util.List;

// Interfaz (patrón Adaptador) para importar gastos
public interface ImportadorGastos {
    List<GastoImportado> importar(File fichero) throws ImportacionException;
}
