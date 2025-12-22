package tds;

import tds.controlador.GestorGastos;
import tds.modelo.impl.DatosGastos;
import tds.adapters.repository.impl.*;

/**
 * Implementación concreta de la configuración.
 * Instancia los controladores y repositorios reales.
 */
public class ConfiguracionImpl extends Configuracion {

    private GestorGastos gestor;
    private DatosGastos datosGastos;

    public ConfiguracionImpl() {
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
        //return "/classes/data/gastos.json";
    	return "data/";//gastos.json";
    }
    
    @Override
    public Object getDatosGastos() {
        return datosGastos;
    }
}
