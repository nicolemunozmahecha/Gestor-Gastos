package tds.vista;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import tds.Configuracion;
import tds.controlador.GestorGastos;
import tds.modelo.Gasto;

import java.util.List;

public class CalendarioController {

    // Componente de la librería
    private CalendarView calendarView;

    // Componente del FXML donde incrustaremos el calendario
    @FXML private BorderPane contenedorCalendario;

    private GestorGastos gestor; 

    public CalendarioController() {
        // Obtenemos el gestor (Patrón Singleton)
        gestor = Configuracion.getInstancia().getGestorGastos();
    }

    @FXML
    public void initialize() {
        try {
            // A. Obtener datos
            List<Gasto> listaGastos = gestor.getGastos(); 

            // B. Configurar el calendario (llamamos al método interno, sin crear new Controller)
            inicializarCalendario(listaGastos);

            // C. Incrustar la vista en el FXML
            contenedorCalendario.setCenter(this.calendarView);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar el calendario: " + e.getMessage());
        }
    }
    
    // Ahora este método es privado porque solo lo usa esta clase internamente
    private void inicializarCalendario(List<Gasto> misGastos) {
        calendarView = new CalendarView();

        // 1. Crear el calendario lógico
        Calendar gastosCalendar = new Calendar("Gastos");
        
        // Estilo visual (STYLE1 suele ser rojo/rosa por defecto en CalendarFX)
        gastosCalendar.setStyle(Calendar.Style.STYLE1); 

        // 2. Convertir y añadir gastos
        for (Gasto gasto : misGastos) {
            // Corregido: El tipo debe coincidir con la lista (Gasto, no GastoImpl)
            Entry<Gasto> entrada = crearEntradaDesdeGasto(gasto);
            gastosCalendar.addEntry(entrada);
        }

        // 3. Añadir el calendario a la fuente
        CalendarSource miFuente = new CalendarSource("Mis Finanzas");
        miFuente.getCalendars().add(gastosCalendar);
        
        // 4. Configurar la vista
        calendarView.getCalendarSources().add(miFuente);
        calendarView.setRequestedTime(java.time.LocalTime.now());
        
        // Limpieza visual: Ocultar paneles que no necesites
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowPageToolBarControls(true); // Muestra controles mes/semana/día
        
        String cssGeneral = getClass().getResource("/tds/app/estilos.css").toExternalForm();
        calendarView.getStylesheets().add(cssGeneral);
        
        String css = getClass().getResource("/tds/app/calendario.css").toExternalForm();
        calendarView.getStylesheets().add(css);
    }

    private Entry<Gasto> crearEntradaDesdeGasto(Gasto gasto) {
        // Título: "Supermercado - 50.0€"
        String titulo = String.format("%s - %.2f€", gasto.getNombre(), gasto.getCantidad());
        
        Entry<Gasto> entry = new Entry<>(titulo);
        
        entry.setUserObject(gasto);
        // Fechas
        entry.changeStartDate(gasto.getFecha());
        entry.changeEndDate(gasto.getFecha());
        entry.setFullDay(true); 

        return entry;
    }
    
    @FXML
    private void atras() {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
}