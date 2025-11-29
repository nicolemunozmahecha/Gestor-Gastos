package tds;

import tds.controlador.GestorGastos;
import tds.vista.SceneManager;

/**
 * Almacena los distintos controladores y servicios de la aplicación.
 * 
 * La clase App debe llamar a:
 *    Configuracion.setInstancia(new ConfiguracionImpl());
 *
 * El resto de la aplicación puede obtener los controladores mediante:
 *    Configuracion.getInstancia().getGestorGastos();
 */
public abstract class Configuracion {

    private static Configuracion instancia;

    private final SceneManager sceneManager = new SceneManager();

    /**
     * Método invocado solo desde App para establecer la configuración concreta.
     */
    public static void setInstancia(Configuracion impl) {
        Configuracion.instancia = impl;
    }

    /**
     * Acceso global a la configuración.
     */
    public static Configuracion getInstancia() {
        return instancia;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * Servicios que debe proporcionar la implementación concreta.
     */

    public abstract GestorGastos getGestorGastos();

    public abstract String getRutaDatos();
    
    public abstract Object getDatosGastos();
}
