package modelo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

public class GestorGastos {
    
    private static GestorGastos instancia;
    
    private List<Cuenta> cuentas;
    private List<Categoria> categorias;
    private List<Alerta> alertas;
    private List<Notificacion> notificaciones;
    

    private GestorGastos() {
        this.cuentas = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
           
    }
    
    // ?
    public static synchronized GestorGastos getInstancia() {
        if (instancia == null) {
            instancia = new GestorGastos();
        }
        return instancia;
    }
    // ?
    public static synchronized void resetInstancia() {
        instancia = null;
    }
    
    /*
    private void inicializarCategoriasPredefinidas() {
        categorias.add(new Categoria("Alimentación", true));
        categorias.add(new Categoria("Transporte", true));
        categorias.add(new Categoria("Entretenimiento", true));
        categorias.add(new Categoria("Salud", true));
        categorias.add(new Categoria("Educación", true));
        categorias.add(new Categoria("Vivienda", true));
        categorias.add(new Categoria("Ropa", true));
        categorias.add(new Categoria("Otros", true));
    }
    */
    
    // ========== GESTIÓN DE CUENTAS ==========

    // NO SE PUEDEN CREAR CUENTAS PERSONALES, SOLO HAY UNA.
    public CuentaPersonal crearCuentaPersonal(String nombre, String propietario) {
        CuentaPersonal cuenta = new CuentaPersonal(nombre, propietario);
        cuentas.add(cuenta);
        return cuenta;
    }

    public CuentaCompartida crearCuentaCompartida(String nombre, List<Persona> personas) {
        CuentaCompartida cuenta = new CuentaCompartida(nombre, personas);
        cuentas.add(cuenta);
        return cuenta;
    }

    public CuentaCompartida crearCuentaCompartida(String nombre, List<Persona> personas, EstrategiaDistribucion estrategia) {
        CuentaCompartida cuenta = new CuentaCompartida(nombre, personas, estrategia);
        cuentas.add(cuenta);
        return cuenta;
    }

    public boolean eliminarCuenta(Cuenta cuenta) {
        return cuentas.remove(cuenta);
    }
    
    public Cuenta buscarCuenta(String nombre) {
        return cuentas.stream().filter(c -> c.getNombre().equals(nombre)).findFirst().orElse(null);
    }
    
    // ========== GESTIÓN DE CATEGORÍAS ==========
    
    public Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria(nombre, false);
        categorias.add(categoria);
        return categoria;
    }

    public boolean eliminarCategoria(Categoria categoria) {
        
    	// Quizás en la interfaz grafica
    	if (categoria == null || categoria.isPredefinida()) {
            return false;
        }
        
        boolean enUso = cuentas.stream().flatMap(c -> c.getGastos().stream()).anyMatch(g -> g.getCategoria().equals(categoria));
        
        // Hay que comprobar si está en uso pero en la interfaz grafica.
        
        return categorias.remove(categoria);
    }
    
    public Categoria buscarCategoria(String nombre) {
        return categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }
    
    // ========== GESTIÓN DE ALERTAS ==========
    
    public Alerta crearAlerta(String nombre, double limite, PeriodoAlerta periodo) {
        Alerta alerta = new Alerta(nombre, limite, periodo);
        alertas.add(alerta);
        return alerta;
    }

    public Alerta crearAlerta(String nombre, double limite, PeriodoAlerta periodo, Categoria categoria) {
        Alerta alerta = new Alerta(nombre, limite, periodo, categoria);
        alertas.add(alerta);
        return alerta;
    }
    
    public boolean eliminarAlerta(Alerta alerta) {
        return alertas.remove(alerta);
    }
    
    public void verificarAlertas() {
        
        for (Alerta alerta : alertas) {
            double gastoActual = calcularGastoPeriodo(alerta, LocalDate.now());
            
            if (alerta.superaLimite(gastoActual)) {
                    Notificacion notificacion = new Notificacion(alerta);
                    notificaciones.add(notificacion);
            }
        }
    }
    
    /**
     * Calcula el gasto para el período de una alerta
     * @param alerta Alerta a verificar
     * @param fecha Fecha de referencia
     * @return Total de gasto en el período
     */
    private double calcularGastoPeriodo(Alerta alerta, LocalDate fecha) {
        LocalDate inicio;
        LocalDate fin = fecha;
        
        if (alerta.getPeriodo() == PeriodoAlerta.SEMANAL) {
            inicio = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } else { // MENSUAL
            inicio = fecha.with(TemporalAdjusters.firstDayOfMonth());
        }
        
        return getGastosFiltrados(new Filtro(null, inicio, fin, 
                                 alerta.esGeneral() ? null : List.of(alerta.getCategoria())))
               .stream()
               .mapToDouble(Gasto::getCantidad)
               .sum();
    }
    
    
    // ========== GESTIÓN DE GASTOS ==========
    
    /**
     * Obtiene todos los gastos del sistema
     * @return Lista de todos los gastos
     */
    public List<Gasto> getGastos() {
        return cuentas.stream()
                     .flatMap(c -> c.getGastos().stream())
                     .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los gastos de una cuenta específica
     * @param cuenta Cuenta de la cual obtener gastos
     * @return Lista de gastos de la cuenta
     */
    public List<Gasto> getGastosPorCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.getGastos();
    }
    
    /**
     * Obtiene gastos filtrados según criterios
     * @param filtro Filtro a aplicar
     * @return Lista de gastos filtrados
     */
    public List<Gasto> getGastosFiltrados(Filtro filtro) {
        if (filtro == null) {
            return getGastos();
        }
        return filtro.aplicar(getGastos());
    }
    
    /**
     * Obtiene el total de gastos de una cuenta
     * @param cuenta Cuenta de la cual calcular el total
     * @return Total de gastos
     */
    public double getTotalCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.calcularTotal();
    }
    
    /**
     * Obtiene el total de gastos por categoría
     * @return Mapa con totales por categoría
     */
    public Map<Categoria, Double> getTotalesPorCategoria() {
        Map<Categoria, Double> totales = new HashMap<>();
        
        for (Categoria categoria : categorias) {
            double total = getGastos().stream()
                                     .filter(g -> g.getCategoria().equals(categoria))
                                     .mapToDouble(Gasto::getCantidad)
                                     .sum();
            if (total > 0) {
                totales.put(categoria, total);
            }
        }
        
        return totales;
    }
    
    // ========== GESTIÓN DE NOTIFICACIONES ==========
    
    /**
     * Obtiene las notificaciones no leídas
     * @return Lista de notificaciones no leídas
     */
    public List<Notificacion> getNotificacionesNoLeidas() {
        return notificaciones.stream()
                           .filter(n -> !n.isLeida())
                           .collect(Collectors.toList());
    }
    
    /**
     * Marca una notificación como leída
     * @param notificacion Notificación a marcar
     */
    public void marcarNotificacionComoLeida(Notificacion notificacion) {
        if (notificacion != null) {
            notificacion.marcarComoLeida();
        }
    }
    
    /**
     * Marca todas las notificaciones como leídas
     */
    public void marcarTodasNotificacionesComoLeidas() {
        notificaciones.forEach(Notificacion::marcarComoLeida);
    }
    
    /**
     * Elimina las notificaciones antiguas (más de 30 días)
     */
    public void limpiarNotificacionesAntiguas() {
        LocalDate hace30Dias = LocalDate.now().minusDays(30);
        notificaciones.removeIf(n -> n.getFechaHora().toLocalDate().isBefore(hace30Dias));
    }
    
    // ========== GETTERS ==========
    
    public List<Cuenta> getCuentas() {
        return new ArrayList<>(cuentas);
    }
    
    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }
    
    public List<Categoria> getCategoriasPredefinidas() {
        return categorias.stream()
                        .filter(Categoria::isPredefinida)
                        .collect(Collectors.toList());
    }
    
    public List<Categoria> getCategoriasPersonalizadas() {
        return categorias.stream()
                        .filter(c -> !c.isPredefinida())
                        .collect(Collectors.toList());
    }
    
    public List<Alerta> getAlertas() {
        return new ArrayList<>(alertas);
    }
    
    public List<Notificacion> getNotificaciones() {
        return new ArrayList<>(notificaciones);
    }

}
