package tds.controlador;

import tds.modelo.*;
import tds.modelo.impl.*;
import tds.adapters.repository.*;
import tds.adapters.repository.exceptions.*;

import tds.importacion.GastoImportado;
import tds.importacion.ImportacionException;
import tds.importacion.ImportadorGastos;
import tds.importacion.ImportadorGastosFactory;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import tds.modelo.estrategias.EstrategiaDistribucionFactory;

public class GestorGastos {
    
    private static GestorGastos instancia;
    private static final String CUENTA_PRINCIPAL = "principal";
    private static final String CUENTA_PERSONAL = "personal";
    
    private final GastoRepository repositorioGastos;
    private final CuentaRepository repositorioCuentas;
    private final CategoriaRepository repositorioCategorias;
    private final AlertaRepository repositorioAlertas;
    private final PersonaRepository repositorioPersonas;
    private final NotificacionRepository repositorioNotificaciones;
    
    private List<Cuenta> cuentas;
    private List<Categoria> categorias;
    private List<Alerta> alertas;
    private List<Notificacion> notificaciones;
    

    private GestorGastos(GastoRepository gastoRepo, CuentaRepository cuentaRepo,
                        CategoriaRepository categoriaRepo, AlertaRepository alertaRepo,
                        PersonaRepository personaRepo, NotificacionRepository notificacionRepo) {
        this.repositorioGastos = gastoRepo;
        this.repositorioCuentas = cuentaRepo;
        this.repositorioCategorias = categoriaRepo;
        this.repositorioAlertas = alertaRepo;
        this.repositorioPersonas = personaRepo;
        this.repositorioNotificaciones = notificacionRepo;
        
        this.cuentas = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        
        // Cargar datos desde los repositorios
        cargarDatos();
        inicializarCategoriasPredefinidas();
    }

    
    private void cargarDatos() {
        // Cargar las cuentas, categorias y alertas desde el repositorio
        this.cuentas = new ArrayList<>(repositorioCuentas.getCuentas());
        this.categorias = new ArrayList<>(repositorioCategorias.getCategorias());
        this.alertas = new ArrayList<>(repositorioAlertas.getAlertas());
        // OJO: mantenemos referencia a la lista del repositorio para que cualquier cambio se persista correctamente.
        @SuppressWarnings("unchecked")
        List<Notificacion> listaRepo = (List<Notificacion>) (List<?>) repositorioNotificaciones.getNotificaciones();
        this.notificaciones = (listaRepo != null) ? listaRepo : new ArrayList<>();
    }
    
    // Método para crear la instancia con repositorios
    public static synchronized GestorGastos crearInstancia(GastoRepository gastoRepo,
                                                           CuentaRepository cuentaRepo,
                                                           CategoriaRepository categoriaRepo,
                                                           AlertaRepository alertaRepo,
                                                           PersonaRepository personaRepo,
                                                           NotificacionRepository notificacionRepo) {
        if (instancia == null) {
            instancia = new GestorGastos(gastoRepo, cuentaRepo, categoriaRepo, alertaRepo, personaRepo, notificacionRepo);
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

    private void actualizarCuentaEnLista(CuentaImpl cuenta) {
        for (int i = 0; i < cuentas.size(); i++) {
            if (cuentas.get(i).getNombre().equals(cuenta.getNombre())) {
                cuentas.set(i, cuenta);
                break;
            }
        }
    }

    public boolean actualizarEstrategiaDistribucion(CuentaCompartida cuenta, EstrategiaDistribucion estrategia)
    		throws ErrorPersistenciaException {
        if (cuenta == null || estrategia == null) throw new IllegalArgumentException("Cuenta o estrategia nulos");

        cuenta.setEstrategiaDistribucion(estrategia);
        CuentaImpl cuentaActualizada = repositorioCuentas.updateCuenta((CuentaImpl) cuenta);

        // Actualizar la cuenta en la lista local
        actualizarCuentaEnLista(cuentaActualizada);
        return true;
    }

    public List<CuentaCompartida> getCuentasCompartidas() {
        return cuentas.stream()
                .filter(c -> c instanceof CuentaCompartida)
                .map(c -> (CuentaCompartida) c)
                .collect(Collectors.toList());
    }
    
    public List<String> getNombresCuentas() {
        return cuentas.stream()
                .map(c -> c.getNombre())
                .collect(Collectors.toList());
    }

    
    public CuentaPersonal crearCuentaPersonal(CuentaPersonalImpl cuenta) 
    		throws ElementoExistenteException, ErrorPersistenciaException{
        
        repositorioCuentas.addCuenta(cuenta);
        cuentas.add(cuenta);
        return cuenta;
    }
    
    
    public CuentaCompartida crearCuentaCompartida(String nombre, List<Persona> personas) 
    		throws ElementoExistenteException, ErrorPersistenciaException{
        CuentaCompartidaImpl cuenta = new CuentaCompartidaImpl(nombre, personas);
        
        repositorioCuentas.addCuenta(cuenta);
        cuentas.add(cuenta);
        return cuenta;
    }
    
    public boolean eliminarCuenta(Cuenta cuenta) throws ErrorPersistenciaException{
       repositorioCuentas.removeCuenta((CuentaImpl) cuenta);
       return cuentas.remove(cuenta); 
    }
    
    public Cuenta buscarCuenta(String nombre) {
        return cuentas.stream().filter(c -> c.getNombre().equals(nombre)).findFirst().orElse(null);
    }
    
    
    // ========== GESTIÓN DE CATEGORÍAS ==========
    
    public Categoria crearCategoria(String nombre) 
    		throws ElementoExistenteException, ErrorPersistenciaException{
        CategoriaImpl categoria = new CategoriaImpl(nombre, false);
        repositorioCategorias.addCategoria(categoria);
        categorias.add(categoria);
        return categoria;
    }

    public boolean eliminarCategoria(Categoria categoria) 
    		throws ErrorPersistenciaException{
        
    	// Quizás en la interfaz grafica
    	if (categoria == null || categoria.isPredefinida()) {
    		throw new IllegalArgumentException("Categoria nula o predefinida");
        }
        repositorioCategorias.removeCategoria((CategoriaImpl) categoria);
        return categorias.remove(categoria);
    }
    
    public Categoria buscarCategoria(String nombre) {
        return categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }
    
    // ========== GESTIÓN DE ALERTAS ==========
    
    public Alerta crearAlerta(String nombre, double limite, PeriodoAlerta periodo)
    		throws ElementoExistenteException, ErrorPersistenciaException{
        AlertaImpl alerta = new AlertaImpl(nombre, limite, periodo);
        repositorioAlertas.addAlerta(alerta);
        alertas.add(alerta);
        return alerta;
    }

    public Alerta crearAlerta(String nombre, double limite, PeriodoAlerta periodo, Categoria categoria) 
    		throws ErrorPersistenciaException, ElementoExistenteException{
       AlertaImpl alerta = new AlertaImpl(nombre, limite, periodo, categoria);
       repositorioAlertas.addAlerta(alerta);
       alertas.add(alerta);
       
       return alerta;
    }
    
    public boolean eliminarAlerta(Alerta alerta) throws ErrorPersistenciaException{
    	repositorioAlertas.removeAlerta((AlertaImpl) alerta);
        return alertas.remove(alerta);
    }
    

    public void verificarAlertas(Gasto gastoRecienAnadido) {
        if (gastoRecienAnadido == null) throw new IllegalArgumentException("Gasto nulo");

        LocalDate fechaRef = (gastoRecienAnadido.getFecha() != null) ? gastoRecienAnadido.getFecha() : LocalDate.now();
        Categoria categoriaGasto = gastoRecienAnadido.getCategoria();

        for (Alerta alerta : alertas) {
  
            if (!alerta.esGeneral()) {
                if (categoriaGasto == null || alerta.getCategoria() == null) continue;
                if (!alerta.getCategoria().equals(categoriaGasto)) continue;
            }

            double gastoActual = calcularGastoPeriodoNatural(alerta, fechaRef);
            if (!alerta.superaLimite(gastoActual)) {
                continue;
            }

            LocalDate inicio = alerta.getEstrategia().calcularInicioPeriodo(fechaRef);
            LocalDate fin = alerta.getEstrategia().calcularFinPeriodo(fechaRef);

            // Siempre se genera una nueva notificación
            String mensaje = construirMensajeNotificacion(alerta, gastoActual, inicio, fin);
            NotificacionImpl notificacion = new NotificacionImpl(alerta, mensaje, inicio, fin);
            try {
                repositorioNotificaciones.addNotificacion(notificacion);
            } catch (Exception e) {
           
                System.err.println("Error al persistir notificación: " + e.getMessage());
                if (!notificaciones.contains(notificacion)) {
                    notificaciones.add(notificacion);
                }
            }
        }
    }

    private String construirMensajeNotificacion(Alerta alerta, double gastoActual, LocalDate inicio, LocalDate fin) {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String periodoTxt = alerta.getEstrategia().getNombrePeriodo();
        String rango = (inicio != null && fin != null) ? (inicio.format(df) + " - " + fin.format(df)) : "";
        String categoriaTxt = alerta.esGeneral()
                ? "(general)"
                : "(categoría: " + alerta.getCategoria().getNombre() + ")";
        return String.format(
                "Alerta '%s' superada %s. Total: %.2f€ (límite: %.2f€). Periodo %s %s.",
                alerta.getNombre(), categoriaTxt, gastoActual, alerta.getLimite(), periodoTxt, rango
        );
    }
    
 // ========== GESTIÓN DE GASTOS ==========
    

    public Gasto crearGasto(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria, Persona pagador, String cuenta)
    		throws ElementoExistenteException, ErrorPersistenciaException{
        return new GastoImpl(nombre, cantidad, fecha, descripcion, categoria, pagador, cuenta);
    }
    

    public Gasto crearGasto(String nombre, double cantidad, LocalDate fecha, String descripcion, Categoria categoria, String cuenta)
    		throws ElementoExistenteException, ErrorPersistenciaException{
        return new GastoImpl(nombre, cantidad, fecha, descripcion, categoria, cuenta);
    }
    

    public boolean agregarGastoACuenta(Cuenta cuenta, Gasto gasto) 
    		throws ErrorPersistenciaException{
        if (cuenta == null || gasto == null) {
        	throw new IllegalArgumentException("Cuenta o gasto nulos");
        }
        
        // Añadir el gasto a la cuenta
        cuenta.agregarGasto(gasto);
       // System.out.println("DEBUG: gast añadido: " + gasto);
        try {
	       
	        // Actualizar la cuenta en el repositorio para persistir el cambio
	        CuentaImpl cuentaActualizada = repositorioCuentas.updateCuenta((CuentaImpl) cuenta);
	        
	        // Actualizar la cuenta en la lista local de cuentas del gestor
	        actualizarCuentaEnLista(cuentaActualizada);
	        
	        // Tras cualquier modificación de gastos, comprobamos alertas y generamos notificaciones si procede.
	        // (Usando la fecha/categoría del propio gasto para aplicar el periodo natural.)
	        verificarAlertas(gasto);
	        return true;
	        
        }catch(Exception e){
	    	if (e instanceof ErrorPersistenciaException) {
	            throw (ErrorPersistenciaException) e;
	        }
	    }
        return false;
    }
    
    public Cuenta getCuentaPorNombre(String nombre) throws ElementoNoEncontradoException {
        try {
            return repositorioCuentas.findByNombre(nombre);
        } catch (Exception e) {
            throw new ElementoNoEncontradoException("No se encontró la cuenta: " + nombre);
        }
    }
    

    /**
     * Calcula el gasto acumulado dentro del periodo NATURAL de la alerta que contiene a fecha,
     * pero acumulado HASTA la fecha indicada (inclusive).
     * 
     * Ejemplo: alerta mensual -> desde día 1 del mes hasta la fecha del gasto.
     */
    private double calcularGastoPeriodoNatural(Alerta alerta, LocalDate fecha) {
        LocalDate inicio = alerta.getEstrategia().calcularInicioPeriodo(fecha);
        LocalDate fin = fecha;

        return getGastosFiltrados(new FiltroImpl(null, inicio, fin,
                                 alerta.esGeneral() ? null : List.of(alerta.getCategoria())))
               .stream()
               .mapToDouble(Gasto::getCantidad)
               .sum();
    }
    
    public boolean eliminarGastoDeCuenta(Cuenta cuenta, Gasto gasto) 
            throws ErrorPersistenciaException {
        if (cuenta == null || gasto == null) {
            throw new IllegalArgumentException("Cuenta o gasto nulos");
        }
       
        boolean eliminado = cuenta.eliminarGasto(gasto);
        if (!eliminado) return false;

        try {
            repositorioCuentas.updateCuenta((CuentaImpl) cuenta);

        } catch (Exception e) {
            cuenta.agregarGasto(gasto);

            if (e instanceof ErrorPersistenciaException) {
                throw (ErrorPersistenciaException) e;
            }
        }
        return true;
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
    

    public Filtro crearFiltroCompuesto(List<String> cuentas, List<String> meses, LocalDate fechaInicio, 
                                        LocalDate fechaFin, List<Categoria> categorias) {
        // Comienza con filtro vacío
        Filtro filtro = new FiltroVacio();
        
        // Decorar con filtro de cuentas si hay categorías seleccionadas
        if (cuentas != null && !cuentas.isEmpty()) {
            filtro = new FiltroCuentas(filtro, cuentas);
        }
        
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

    /**
     * Devuelve el número de notificaciones sin leer.
     * Útil para mostrar un indicador en la interfaz (menú Notificaciones, badge, etc.).
     */
    public int getNumeroNotificacionesNoLeidas() {
        return (int) notificaciones.stream().filter(n -> !n.isLeida()).count();
    }
    

    public void marcarNotificacionComoLeida(Notificacion notificacion) {
        if (notificacion != null) {
            notificacion.marcarComoLeida();
            if (notificacion instanceof NotificacionImpl) {
                try {
                    repositorioNotificaciones.updateNotificacion((NotificacionImpl) notificacion);
                } catch (Exception e) {
                    System.err.println("Error al persistir cambio de notificación: " + e.getMessage());
                }
            }
        }
    }
    

    public void marcarTodasNotificacionesComoLeidas() {
        notificaciones.forEach(Notificacion::marcarComoLeida);
        try {
            repositorioNotificaciones.guardarNotificaciones();
        } catch (Exception e) {
            System.err.println("Error al persistir notificaciones: " + e.getMessage());
        }
    }

    public boolean eliminarNotificacion(Notificacion notificacion) throws ErrorPersistenciaException {
        if (notificacion == null) throw new IllegalArgumentException("Notificacion nula");
		
        if (notificacion instanceof NotificacionImpl) {
		        repositorioNotificaciones.removeNotificacion((NotificacionImpl) notificacion);
	    }
	    return notificaciones.remove(notificacion);
        
    }
    

    public void limpiarNotificacionesAntiguas() {
        LocalDate hace30Dias = LocalDate.now().minusDays(30);
        notificaciones.removeIf(n -> n.getFechaHora().toLocalDate().isBefore(hace30Dias));
        try {
            repositorioNotificaciones.guardarNotificaciones();
        } catch (Exception e) {
            System.err.println("Error al persistir limpieza de notificaciones: " + e.getMessage());
        }
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


    public CuentaCompartida crearCuentaCompartidaConNombres(String nombreCuenta, List<String> nombresPropietarios) 
    		throws ElementoExistenteException, ErrorPersistenciaException {
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


    // ========== IMPORTACIÓN DE GASTOS ==========


    public boolean importarGastos(File fichero) throws ElementoExistenteException, ErrorPersistenciaException, ImportacionException {
        ImportadorGastos importador = ImportadorGastosFactory.crear(fichero);
        List<GastoImportado> importados = importador.importar(fichero);

        if (importados == null || importados.isEmpty()) {
        	throw new ImportacionException("El fichero está vacío o no contiene gastos válidos.");
        }

        // Preparar participantes por cuenta compartida (para crear cuentas si no existen)
        Map<String, Set<String>> participantesPorCuenta = new HashMap<>();
        for (GastoImportado g : importados) {
            String cuenta = normalizarNombreCuenta(g.cuenta());
            if (esCuentaPersonal(cuenta)) continue;
            participantesPorCuenta
                    .computeIfAbsent(cuenta, k -> new LinkedHashSet<>())
                    .add(g.pagador().trim());
        }

        // Comprobar que existen las cuentas compartidas necesarias
        for (Map.Entry<String, Set<String>> e : participantesPorCuenta.entrySet()) {
            String nombreCuenta = e.getKey();
            Cuenta existente = buscarCuenta(nombreCuenta);
            if (existente != null) continue;

            List<Persona> personas = e.getValue().stream()
                    .filter(n -> n != null && !n.isBlank())
                    .map(this::buscarOCrearPersona)
                    .collect(Collectors.toList());


            crearCuentaCompartida(nombreCuenta, personas);
        }


        Cuenta principal = buscarCuenta("Principal");
        if (principal == null) {
            crearCuentaPersonal(new CuentaPersonalImpl("Principal"));
            principal = buscarCuenta("Principal");
        }

        // Importar cada gasto
        int importadosOk = 0;
        for (GastoImportado gi : importados) {
            String nombreCuenta = normalizarNombreCuenta(gi.cuenta());
            Cuenta cuentaDestino = esCuentaPersonal(nombreCuenta)
                    ? principal
                    : buscarCuenta(nombreCuenta);

            if (cuentaDestino == null) {
                continue;
            }

            String nombreCategoria = (gi.categoria() == null || gi.categoria().isBlank())
                    ? "Sin categoría"
                    : gi.categoria().trim();

            Categoria categoria = buscarCategoria(nombreCategoria);
            if (categoria == null) {
                categoria = crearCategoria(nombreCategoria);
            }

            Persona pagador = buscarOCrearPersona(gi.pagador().trim());

            // Si es cuenta compartida y el pagador no está en la cuenta, lo añadimos.
            if (cuentaDestino instanceof CuentaCompartidaImpl ccImpl) {
                ccImpl.addPersonaSiNoExiste(pagador);
            }

            String descripcion = (gi.descripcion() == null) ? "" : gi.descripcion().trim();
            String nombre = (gi.nombre() == null || gi.nombre().isBlank())
                    ? "(Sin nombre)"
                    : gi.nombre().trim();

            Gasto gasto = crearGasto(nombre, gi.cantidad(), gi.fecha(), descripcion, categoria, pagador, nombreCuenta);
            boolean ok = agregarGastoACuenta(cuentaDestino, gasto);
            if (ok) {
                importadosOk++;
            }
        }

        return importadosOk > 0;
    }

    private static boolean esCuentaPersonal(String nombreCuentaNormalizado) {
        if (nombreCuentaNormalizado == null) return true;
        String v = nombreCuentaNormalizado.trim();
        return CUENTA_PRINCIPAL.equalsIgnoreCase(v) || CUENTA_PERSONAL.equalsIgnoreCase(v);
    }

    private static String normalizarNombreCuenta(String raw) {
        if (raw == null) return "";
        String v = raw.trim();
        if (v.equalsIgnoreCase("Personal")) return "Principal";
        return v;
    }


}