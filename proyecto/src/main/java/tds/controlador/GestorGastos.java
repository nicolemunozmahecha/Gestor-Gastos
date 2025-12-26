package tds.controlador;

import tds.modelo.*;
import tds.modelo.impl.*;
import tds.adapters.repository.*;
import tds.adapters.repository.exceptions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import tds.modelo.estrategias.EstrategiaDistribucionFactory;

public class GestorGastos {
    
    private static GestorGastos instancia;
    
    private final GastoRepository repositorioGastos;
    private final CuentaRepository repositorioCuentas;
    private final CategoriaRepository repositorioCategorias;
    private final AlertaRepository repositorioAlertas;
    private final PersonaRepository repositorioPersonas;
    
    private List<Cuenta> cuentas;
    private List<Categoria> categorias;
    private List<Alerta> alertas;
    private List<Notificacion> notificaciones;
    

    private GestorGastos(GastoRepository gastoRepo, CuentaRepository cuentaRepo, 
                        CategoriaRepository categoriaRepo, AlertaRepository alertaRepo,
                        PersonaRepository personaRepo) {
        this.repositorioGastos = gastoRepo;
        this.repositorioCuentas = cuentaRepo;
        this.repositorioCategorias = categoriaRepo;
        this.repositorioAlertas = alertaRepo;
        this.repositorioPersonas = personaRepo;
        
        this.cuentas = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        
        // Cargar datos desde los repositorios
        cargarDatos();
        inicializarCategoriasPredefinidas();
    }

    
    private void cargarDatos() {
        // Cargar las cuentas desde el repositorio
        this.cuentas = new ArrayList<>(repositorioCuentas.getCuentas());
        // Cargar las categorías desde el repositorio
        this.categorias = new ArrayList<>(repositorioCategorias.getCategorias());
        // Cargar las alertas desde el repositorio
        this.alertas = new ArrayList<>(repositorioAlertas.getAlertas());
    }
    
    // Método factory para crear la instancia con repositorios
    public static synchronized GestorGastos crearInstancia(GastoRepository gastoRepo, 
                                                           CuentaRepository cuentaRepo,
                                                           CategoriaRepository categoriaRepo, 
                                                           AlertaRepository alertaRepo,
                                                           PersonaRepository personaRepo) {
        if (instancia == null) {
            instancia = new GestorGastos(gastoRepo, cuentaRepo, categoriaRepo, alertaRepo, personaRepo);
        }
        return instancia;
    }
    
    // Obtener la instancia actual (debe haber sido creada previamente con crearInstancia)
    public static synchronized GestorGastos getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException("GestorGastos no ha sido inicializado. Use crearInstancia primero.");
        }
        return instancia;
    }
    
    // Resetear la instancia
    public static synchronized void resetInstancia() {
        instancia = null;
    }
    
    
    private void inicializarCategoriasPredefinidas() {
        // Solo añadir categorías predefinidas si no existen
        String[] nombresCategorias = {"Alimentación", "Transporte", "Entretenimiento"};
        
        for (String nombre : nombresCategorias) {
            // Verificar si la categoría ya existe
            boolean existe = categorias.stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));
            
            if (!existe) {
                CategoriaImpl categoria = new CategoriaImpl(nombre, true);
                categorias.add(categoria);
                // Persistir la categoría
                try {
                    repositorioCategorias.addCategoria(categoria);
                } catch (Exception e) {
                    // La categoría ya existe en el repositorio, no hacer nada
                }
            }
        }
    }
    
    
    // ========== GESTIÓN DE CUENTAS ==========


    public EstrategiaDistribucion crearEstrategiaDistribucion(String estrategiaId, List<Persona> participantes) {
        return EstrategiaDistribucionFactory.crearOporDefecto(estrategiaId, participantes);
    }


    public boolean actualizarEstrategiaDistribucion(CuentaCompartida cuenta, EstrategiaDistribucion estrategia) {
        if (cuenta == null || estrategia == null) return false;

        try {
            cuenta.setEstrategiaDistribucion(estrategia);
            CuentaImpl cuentaActualizada = repositorioCuentas.updateCuenta((CuentaImpl) cuenta);

            // Actualizar la cuenta en la lista local
            for (int i = 0; i < cuentas.size(); i++) {
                if (cuentas.get(i).getNombre().equals(cuenta.getNombre())) {
                    cuentas.set(i, cuentaActualizada);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar estrategia de distribución: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<CuentaCompartida> getCuentasCompartidas() {
        return cuentas.stream()
                .filter(c -> c instanceof CuentaCompartida)
                .map(c -> (CuentaCompartida) c)
                .collect(Collectors.toList());
    }

    
    public CuentaPersonal crearCuentaPersonal(CuentaPersonalImpl cuenta) {
        try {
            repositorioCuentas.addCuenta(cuenta);
            cuentas.add(cuenta);
        } catch (Exception e) {
            System.err.println("Error al crear cuenta personal: " + e.getMessage());
        }
        return cuenta;
    }
    
    
    public CuentaCompartida crearCuentaCompartida(String nombre, List<Persona> personas) {
        System.out.println("DEBUG: Creando cuenta compartida: " + nombre);
        System.out.println("DEBUG: Número de personas: " + personas.size());
        
        CuentaCompartidaImpl cuenta = new CuentaCompartidaImpl(nombre, personas);
        System.out.println("DEBUG: Cuenta creada en memoria");
        
        try {
            System.out.println("DEBUG: Intentando añadir al repositorio...");
            repositorioCuentas.addCuenta(cuenta);
            System.out.println("DEBUG: Cuenta añadida al repositorio");
            cuentas.add(cuenta);
            System.out.println("DEBUG: Cuenta añadida a la lista local");
        } catch (Exception e) {
            System.err.println("Error al crear cuenta compartida: " + e.getMessage());
            e.printStackTrace();  // Imprimir el stack trace completo
        }
        return cuenta;
    }
    
    public boolean eliminarCuenta(Cuenta cuenta) {
        try {
            repositorioCuentas.removeCuenta((CuentaImpl) cuenta);
            return cuentas.remove(cuenta);
        } catch (Exception e) {
            System.err.println("Error al eliminar cuenta: " + e.getMessage());
            return false;
        }
    }
    
    public Cuenta buscarCuenta(String nombre) {
        return cuentas.stream().filter(c -> c.getNombre().equals(nombre)).findFirst().orElse(null);
    }
    
    
    // ========== GESTIÓN DE CATEGORÍAS ==========
    
    public Categoria crearCategoria(String nombre) {
        CategoriaImpl categoria = new CategoriaImpl(nombre, false);
        System.out.println("repositorioCategorias = " + repositorioCategorias);
        System.out.println("categorias = " + categorias);

        try {
        	repositorioCategorias.addCategoria(categoria);
        	categorias.add(categoria);
        } catch (Exception e) {
            System.err.println("Error al crear categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return categoria;
    }

    public boolean eliminarCategoria(Categoria categoria) {
        
    	// Quizás en la interfaz grafica
    	if (categoria == null || categoria.isPredefinida()) {
            return false;
        }
        
        boolean enUso = cuentas.stream().flatMap(c -> c.getGastos().stream()).anyMatch(g -> g.getCategoria().equals(categoria));
        
        // Hay que comprobar si está en uso pero en la interfaz grafica.
        
        try {
            repositorioCategorias.removeCategoria((CategoriaImpl) categoria);
            return categorias.remove(categoria);
        } catch (Exception e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
    
    public Categoria buscarCategoria(String nombre) {
        return categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }
    
    // ========== GESTIÓN DE ALERTAS ==========
    
    public Alerta crearAlerta(String nombre, double limite, PeriodoAlerta periodo) {
        AlertaImpl alerta = new AlertaImpl(nombre, limite, periodo);
        try {
            repositorioAlertas.addAlerta(alerta);
            alertas.add(alerta);
        } catch (Exception e) {
            System.err.println("Error al crear alerta: " + e.getMessage());
        }
        return alerta;
    }

    public Alerta crearAlerta(String nombre, double limite, PeriodoAlerta periodo, Categoria categoria) {
        AlertaImpl alerta = new AlertaImpl(nombre, limite, periodo, categoria);
        try {
            repositorioAlertas.addAlerta(alerta);
            alertas.add(alerta);
        } catch (Exception e) {
            System.err.println("Error al crear alerta: " + e.getMessage());
        }
        return alerta;
    }
    
    public boolean eliminarAlerta(Alerta alerta) {
        try {
            repositorioAlertas.removeAlerta((AlertaImpl) alerta);
            return alertas.remove(alerta);
        } catch (Exception e) {
            System.err.println("Error al eliminar alerta: " + e.getMessage());
            return false;
        }
    }
    
    public void verificarAlertas() {
        
        for (Alerta alerta : alertas) {
            double gastoActual = calcularGastoPeriodo(alerta, LocalDate.now());
            
            if (alerta.superaLimite(gastoActual)) {
                    Notificacion notificacion = new NotificacionImpl(alerta);
                    notificaciones.add(notificacion);
            }
        }
    }
    
 // ========== GESTIÓN DE GASTOS ==========
    

    public Gasto crearGasto(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria, Persona pagador) {
        return new GastoImpl(nombre, cantidad, fecha, descripcion, categoria, pagador);
    }
    

    public Gasto crearGasto(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria) {
        return new GastoImpl(nombre, cantidad, fecha, descripcion, categoria);
    }
    

    public boolean agregarGastoACuenta(Cuenta cuenta, Gasto gasto) {
        if (cuenta == null || gasto == null) {
            return false;
        }
        
        try {
            // Añadir el gasto a la cuenta
            cuenta.agregarGasto(gasto);
            
            // Actualizar la cuenta en el repositorio para persistir el cambio
            CuentaImpl cuentaActualizada = repositorioCuentas.updateCuenta((CuentaImpl) cuenta);
            
            // Actualizar la cuenta en la lista local de cuentas del gestor
            for (int i = 0; i < cuentas.size(); i++) {
                if (cuentas.get(i).getNombre().equals(cuenta.getNombre())) {
                    cuentas.set(i, cuentaActualizada);
                    break;
                }
            }
            
            System.out.println("DEBUG: Gasto añadido. Cuenta tiene " + cuenta.getNumeroGastos() + " gastos");
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar gasto a cuenta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    public Cuenta getCuentaPorNombre(String nombre) throws ElementoNoEncontradoException {
        try {
            return repositorioCuentas.findByNombre(nombre);
        } catch (Exception e) {
            throw new ElementoNoEncontradoException("No se encontró la cuenta: " + nombre);
        }
    }
    

    private double calcularGastoPeriodo(Alerta alerta, LocalDate fecha) {
        LocalDate inicio;
        LocalDate fin = fecha;
        
        if (alerta.getPeriodo() == PeriodoAlerta.SEMANAL) {
            inicio = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } else { // MENSUAL
            inicio = fecha.with(TemporalAdjusters.firstDayOfMonth());
        }
        
        return getGastosFiltrados(new FiltroImpl(null, inicio, fin, 
                                 alerta.esGeneral() ? null : List.of(alerta.getCategoria())))
               .stream()
               .mapToDouble(Gasto::getCantidad)
               .sum();
    }
    
    public boolean eliminarGastoDeCuenta(Cuenta cuenta, Gasto gasto) {
        if (cuenta == null || gasto == null) return false;

        try {
            boolean eliminado = cuenta.eliminarGasto(gasto);
            if (!eliminado) return false;

            // Persistir cambios
            repositorioCuentas.updateCuenta((CuentaImpl) cuenta);

            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar gasto de cuenta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    
    // ========== GESTIÓN DE GASTOS ==========
    

    public List<Gasto> getGastos() {
        return cuentas.stream()
                     .flatMap(c -> c.getGastos().stream())
                     .collect(Collectors.toList());
    }
    

    public List<Gasto> getGastosPorCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.getGastos();
    }
    

    public List<Gasto> getGastosFiltrados(Filtro filtro) {
        if (filtro == null) {
            return getGastos();
        }
        return filtro.aplicar(getGastos());
    }
    

    public Filtro crearFiltroCompuesto(List<String> meses, LocalDate fechaInicio, 
                                        LocalDate fechaFin, List<Categoria> categorias) {
        // Comienza con filtro vacío
        Filtro filtro = new FiltroVacio();
        
        // Decorar con filtro de meses si hay meses seleccionados
        if (meses != null && !meses.isEmpty()) {
            filtro = new FiltroMeses(filtro, meses);
        }
        
        // Decorar con filtro de fechas si hay rango especificado
        if (fechaInicio != null || fechaFin != null) {
            filtro = new FiltroFechas(filtro, fechaInicio, fechaFin);
        }
        
        // Decorar con filtro de categorías si hay categorías seleccionadas
        if (categorias != null && !categorias.isEmpty()) {
            filtro = new FiltroCategorias(filtro, categorias);
        }
        
        return filtro;
    }
    

    public double getTotalCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        return cuenta.calcularTotal();
    }
    

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
    

    public List<Notificacion> getNotificacionesNoLeidas() {
        return notificaciones.stream()
                           .filter(n -> !n.isLeida())
                           .collect(Collectors.toList());
    }
    

    public void marcarNotificacionComoLeida(Notificacion notificacion) {
        if (notificacion != null) {
            notificacion.marcarComoLeida();
        }
    }
    

    public void marcarTodasNotificacionesComoLeidas() {
        notificaciones.forEach(Notificacion::marcarComoLeida);
    }
    

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
    
    // ========== GESTIÓN DE PERSONAS ==========
    

    private Persona buscarOCrearPersona(String nombre) {
        try {
            // Primero intentar buscar la persona
            return repositorioPersonas.findByNombre(nombre);
        } catch (ElementoNoEncontradoException e) {
            // Si no existe, la creamos
            PersonaImpl nuevaPersona = new PersonaImpl(nombre);
            try {
                repositorioPersonas.addPersona(nuevaPersona);
                return nuevaPersona;
            } catch (Exception ex) {
                // Si hay error persistiendo, devolvemos la persona sin persistir
                return nuevaPersona;
            }
        }
    }
    

    public CuentaCompartida crearCuentaCompartidaConNombres(String nombreCuenta, List<String> nombresPropietarios) {
        if (nombresPropietarios == null || nombresPropietarios.size() < 2) {
            return null;
        }
        
        // Convertir nombres a personas (buscar o crear)
        List<Persona> personas = new ArrayList<>();
        for (String nombre : nombresPropietarios) {
            if (nombre != null && !nombre.trim().isEmpty()) {
                Persona persona = buscarOCrearPersona(nombre.trim());
                personas.add(persona);
            }
        }
        
        // Verificar que tengamos al menos 2 personas
        if (personas.size() < 2) {
            return null;
        }
        
        // Crear la cuenta compartida con las personas
        return crearCuentaCompartida(nombreCuenta, personas);
    }

}