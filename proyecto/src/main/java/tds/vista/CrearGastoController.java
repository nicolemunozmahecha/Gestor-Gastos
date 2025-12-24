package tds.vista;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Categoria;
import tds.modelo.CuentaPersonal;
import tds.modelo.impl.CategoriaImpl;
import tds.modelo.impl.CuentaPersonalImpl;
import tds.modelo.impl.GastoImpl;

public class CrearGastoController {
	
	@FXML private TextField campoNombreGasto;
	@FXML private TextField campoCantidad;
	@FXML private DatePicker campoFechaGasto;
	@FXML private TextArea campoDescripcion;
	@FXML private MenuButton categorias;

	private GestorGastos gestor;
	private List<Categoria> categoriasDisp;
	private double cantidadFinal;

	@FXML
    public void initialize() {
        gestor = Configuracion.getInstancia().getGestorGastos();
        cargarCategorias();
    }
	
    
	private boolean ningunoSeleccionado() {
        return categorias.getItems().stream()
                .map(item -> (CheckMenuItem) item)               // Convertimos a CheckMenuItem
                .noneMatch(CheckMenuItem::isSelected);          // Ninguno está seleccionado
    }
	
    // PARA CARGAR LAS CATEGORIAS NUEVAS, Y QUE SOLO DEJE SELECCIONAR UNA
    public void cargarCategorias() {
    	categorias.getItems().clear();
        categoriasDisp = gestor.getCategorias();
        for (Categoria c : categoriasDisp) {
            CheckMenuItem item = new CheckMenuItem(c.getNombre());
            item.setUserData(c);
            categorias.getItems().add(item);
        }
        for (MenuItem m : categorias.getItems()) {
            m.setOnAction(e -> {
			                if (((CheckMenuItem) m).isSelected()) {
			                    for (MenuItem mi : categorias.getItems()) {
			                        if (mi != m && mi instanceof CheckMenuItem) {
			                            ((CheckMenuItem) mi).setSelected(false);
			                        }
			                    }
			                }
			});
        }
    }
    
    
    @FXML
    private void crearGasto() {
        String nombre = campoNombreGasto.getText().trim();
        if(nombre.isEmpty()) {
            System.out.println("El gasto debe tener un nombre");
            return;
        }
        
        String cantidad = campoCantidad.getText().trim();
        if (cantidad.isEmpty()) {
            System.out.println("El gasto debe tener un importe");
            return;
        }
        
        // Verificar si es un número
        try {
            cantidadFinal = Double.parseDouble(cantidad);
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un número válido");
            return;
        }
        
        LocalDate fecha = campoFechaGasto.getValue();
        if (fecha == null) {
            System.out.println("El gasto debe tener una fecha");
            return;
        }
        
        
        if(ningunoSeleccionado()) {
            System.out.println("El gasto debe tener una categoria");
            return;
        }else {
            // Obtenemos lista de categorias, y solo queremos una, SOLO SE ELIGE 1 CATEGORIA
            Optional<CheckMenuItem> c = categorias.getItems().stream()
                    .filter(item -> item instanceof CheckMenuItem)
                    .map(item -> (CheckMenuItem) item)
                    .filter(CheckMenuItem::isSelected)
                    .findFirst();
            // creamos categoria nueva para la alerta, pero YA EXISTE en la lista de categorias
            CheckMenuItem item = c.get();
            Categoria cat = new CategoriaImpl(item.getText()); 
            String descripcion = campoDescripcion.getText().trim();
            
            // IMPORTANTE: Primero añadir gasto, luego cambiar de ventana
            VentanaPrincipalController controller = Configuracion.getInstancia().getSceneManager().getVentanaPrincipalController();
            
            if (descripcion.isEmpty()) {
                GastoImpl g = (GastoImpl) gestor.crearGasto(nombre, cantidadFinal, fecha, "", cat);
                if (controller != null) {
                    // Añadir a la cuenta Y persistir el cambio
                    boolean exito = gestor.agregarGastoACuenta(controller.getCuenta(), g);
                    if (exito) {
                        System.out.println("DEBUG: Gasto añadido exitosamente");
                        // Actualizar la tabla visual
                        controller.añadirGastoTabla(g);
                        
                        // CRÍTICO: Recargar la cuenta desde el repositorio
                        try {
                            CuentaPersonal cuentaActualizada = (CuentaPersonal) gestor.getCuentaPorNombre(controller.getCuenta().getNombre());
                            controller.setCuenta(cuentaActualizada);
                            Configuracion.getInstancia().getSceneManager().setPrincipal((CuentaPersonalImpl) cuentaActualizada);
                            System.out.println("DEBUG: Cuenta recargada. Total gastos: " + cuentaActualizada.getNumeroGastos());
                        } catch (Exception e) {
                            System.err.println("Error al recargar cuenta: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("ERROR: No se pudo añadir el gasto");
                    }
                } else {
                    System.err.println("ERROR: Controller es null");
                }
            }else {
                GastoImpl g = (GastoImpl) gestor.crearGasto(nombre, cantidadFinal, fecha, descripcion, cat);
                if (controller != null) {
                    // Añadir a la cuenta Y persistir el cambio
                    boolean exito = gestor.agregarGastoACuenta(controller.getCuenta(), g);
                    if (exito) {
                        System.out.println("DEBUG: Gasto añadido exitosamente");
                        // Actualizar la tabla visual
                        controller.añadirGastoTabla(g);
                        
                        // CRÍTICO: Recargar la cuenta desde el repositorio
                        try {
                            CuentaPersonal cuentaActualizada = (CuentaPersonal) gestor.getCuentaPorNombre(controller.getCuenta().getNombre());
                            controller.setCuenta(cuentaActualizada);
                            Configuracion.getInstancia().getSceneManager().setPrincipal((CuentaPersonalImpl) cuentaActualizada);
                            System.out.println("DEBUG: Cuenta recargada. Total gastos: " + cuentaActualizada.getNumeroGastos());
                        } catch (Exception e) {
                            System.err.println("Error al recargar cuenta: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("ERROR: No se pudo añadir el gasto");
                    }
                } else {
                    System.err.println("ERROR: Controller es null");
                }
            }
            
        }
        
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    @FXML
    private void atras() {
    	Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}
