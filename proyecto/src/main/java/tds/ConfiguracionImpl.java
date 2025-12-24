package tds;

import tds.controlador.GestorGastos;
import tds.modelo.impl.DatosGastos;
import tds.adapters.repository.impl.*;

import java.nio.file.Paths;

/**
 * Implementación concreta de la configuración.
 * Instancia los controladores y repositorios reales.
 */
public class ConfiguracionImpl extends Configuracion {

    private GestorGastos gestor;
    private DatosGastos datosGastos;

    public ConfiguracionImpl() {

        Configuracion.setInstancia(this);

        // Crear el objeto de datos
        this.datosGastos = new DatosGastos();
        
        // Crear el controlador con los repositorios JSON
        this.gestor = GestorGastos.crearInstancia(
            new GastoRepositoryJSONImpl(),
            new CuentaRepositoryJSONImpl(),
            new CategoriaRepositoryJSONImpl(),
            new AlertaRepositoryJSONImpl(),
            new PersonaRepositoryJSONImpl()
        );
    }

    @Override
    public GestorGastos getGestorGastos() {
        return gestor;
    }

    @Override
    public String getRutaDatos() {
		return Paths.get("data", "datos.json").toString();
    }
    
    @Override
    public Object getDatosGastos() {
        return datosGastos;
    }
}
