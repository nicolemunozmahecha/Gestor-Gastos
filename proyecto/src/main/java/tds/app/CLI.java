package tds.app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import tds.Configuracion;
import tds.modelo.*;
import tds.modelo.impl.*;
import tds.controlador.GestorGastos;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;


public class CLI {
    
    private final GestorGastos gestor;
    private final Scanner scanner;
    private Cuenta cuentaActual;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public CLI(Configuracion configuracion) throws ElementoExistenteException, ErrorPersistenciaException {
        this.gestor = configuracion.getGestorGastos();
        this.scanner = new Scanner(System.in);
        
        // Obtener o crear cuenta Principal
        try {
            this.cuentaActual = gestor.getCuentaPorNombre("Principal");
        } catch (ElementoNoEncontradoException e) {
            gestor.crearCuentaPersonal("Principal");
            try {
                this.cuentaActual = gestor.getCuentaPorNombre("Principal");
            } catch (ElementoNoEncontradoException ex) {
                this.cuentaActual = new CuentaPersonalImpl("Principal");
            }
        }
    }
    
    public void ejecutar() throws ErrorPersistenciaException {
        mostrarBienvenida();
        
        boolean continuar = true;
        while (continuar) {
            System.out.print("\n> ");
            String comando = scanner.nextLine().trim();
            
            if (comando.isEmpty()) {
                continue;
            }
            
            String[] partes = comando.split("\\s+", 2);
            String accion = partes[0].toLowerCase();
            
            switch (accion) {
                case "ayuda":
                case "help":
                    mostrarAyuda();
                    break;
                case "registrar":
                case "crear":
                    registrarGasto();
                    break;
                case "listar":
                case "ver":
                    listarGastos();
                    break;
                case "modificar":
                case "editar":
                    modificarGasto();
                    break;
                case "borrar":
                case "eliminar":
                    borrarGasto();
                    break;
                case "cuenta":
                    cambiarCuenta();
                    break;
                case "salir":
                case "exit":
                    continuar = false;
                    System.out.println("Saliendo del programa");
                    break;
                default:
                    System.out.println("Comando no reconocido. Escribe 'ayuda' para ver los comandos disponibles.");
            }
        }
        
        scanner.close();
    }
    
    private void mostrarBienvenida() {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   TDS - Sistema de Gestión de Gastos (CLI)     ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("\nCuenta activa: " + cuentaActual.getNombre());
        System.out.println("Escribe 'ayuda' para ver los comandos disponibles.\n");
    }
    
    private void mostrarAyuda() {
        System.out.println("\n═══ COMANDOS DISPONIBLES ═══");
        System.out.println("  registrar    - Registrar un nuevo gasto");
        System.out.println("  listar       - Listar todos los gastos de la cuenta actual");
        System.out.println("  modificar    - Modificar un gasto existente");
        System.out.println("  borrar       - Eliminar un gasto");
        System.out.println("  cuenta       - Cambiar de cuenta");
        System.out.println("  ayuda        - Mostrar esta ayuda");
        System.out.println("  salir        - Salir de la aplicación");
        System.out.println();
    }
    
    private void registrarGasto() {
        System.out.println("\n--- REGISTRAR NUEVO GASTO ---");
        
        try {
            // Nombre
            System.out.print("Nombre del gasto: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) {
                System.out.println("Error: El nombre no puede estar vacío.");
                return;
            }
            
            // Cantidad
            System.out.print("Cantidad (€): ");
            String cantidadStr = scanner.nextLine().trim();
            double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadStr);
                if (cantidad <= 0) {
                    System.out.println("Error: La cantidad debe ser positiva.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Cantidad inválida.");
                return;
            }
            
            // Fecha
            System.out.print("Fecha (dd/MM/yyyy) [Enter para hoy]: ");
            String fechaStr = scanner.nextLine().trim();
            LocalDate fecha;
            if (fechaStr.isEmpty()) {
                fecha = LocalDate.now();
            } else {
                try {
                    fecha = LocalDate.parse(fechaStr, DATE_FORMAT);
                } catch (DateTimeParseException e) {
                    System.out.println("Error: Formato de fecha inválido.");
                    return;
                }
            }
            
            // Descripción
            System.out.print("Descripción [opcional]: ");
            String descripcion = scanner.nextLine().trim();
            
            // Categoría
            List<Categoria> categorias = gestor.getCategorias();
            System.out.println("\nCategorías disponibles:");
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + categorias.get(i).getNombre());
            }
            System.out.print("Selecciona categoría (número) [1]: ");
            String catStr = scanner.nextLine().trim();
            int catIndex = catStr.isEmpty() ? 0 : Integer.parseInt(catStr) - 1;
            if (catIndex < 0 || catIndex >= categorias.size()) {
                catIndex = 0;
            }
            Categoria categoria = categorias.get(catIndex);
            
            // Pagador (para cuentas compartidas)
            Persona pagador;
            String cuenta;
            if (cuentaActual instanceof CuentaCompartida) {
                CuentaCompartida cc = (CuentaCompartida) cuentaActual;
                List<Persona> participantes = cc.getPersonas();
                System.out.println("\nParticipantes:");
                for (int i = 0; i < participantes.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + participantes.get(i).getNombre());
                }
                System.out.print("Selecciona pagador (número) [1]: ");
                String pagStr = scanner.nextLine().trim();
                int pagIndex = pagStr.isEmpty() ? 0 : Integer.parseInt(pagStr) - 1;
                if (pagIndex < 0 || pagIndex >= participantes.size()) {
                    pagIndex = 0;
                }
                cuenta = cc.getNombre();
                pagador = participantes.get(pagIndex);
            } else {
                // Para cuenta personal, crear una persona con el propietario
                CuentaPersonal cp = (CuentaPersonal) cuentaActual;
                pagador = new PersonaImpl(cp.getPropietario());
                cuenta = cp.getNombre();
            }
            
            // Crear el gasto usando el método del gestor
            Gasto gasto = gestor.crearGasto(nombre, cantidad, fecha, descripcion, categoria, pagador, cuenta);
            boolean exito = gestor.agregarGastoACuenta(cuentaActual, gasto);
            
            if (exito) {
                System.out.println("\n✓ Gasto registrado exitosamente.");
            } else {
                System.out.println("\n✗ Error al registrar el gasto.");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void listarGastos() {
        List<Gasto> gastos = cuentaActual.getGastos();
        
        if (gastos.isEmpty()) {
            System.out.println("\nNo hay gastos registrados en esta cuenta.");
            return;
        }
        
        System.out.println("\n═══ GASTOS DE: " + cuentaActual.getNombre() + " ═══");
        for (int i = 0; i < gastos.size(); i++) {
            Gasto g = gastos.get(i);
            System.out.printf("%d. %s - %.2f€ - %s - %s\n", 
                i + 1, 
                g.getNombre(), 
                g.getCantidad(), 
                g.getFecha().format(DATE_FORMAT),
                g.getCategoria().getNombre());
            if (g.getDescripcion() != null && !g.getDescripcion().isEmpty()) {
                System.out.println("   Descripción: " + g.getDescripcion());
            }
        }
        System.out.printf("\nTotal: %.2f€\n", cuentaActual.calcularTotal());
    }
    
    private void modificarGasto() {
        List<Gasto> gastos = cuentaActual.getGastos();
        
        if (gastos.isEmpty()) {
            System.out.println("\nNo hay gastos para modificar.");
            return;
        }
        
        listarGastos();
        
        System.out.print("\nNúmero del gasto a modificar: ");
        String numStr = scanner.nextLine().trim();
        int index;
        try {
            index = Integer.parseInt(numStr) - 1;
            if (index < 0 || index >= gastos.size()) {
                System.out.println("Error: Número de gasto inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Número inválido.");
            return;
        }
        
        Gasto gasto = gastos.get(index);
        System.out.println("\n--- MODIFICAR GASTO ---");
        System.out.println("Deja en blanco para mantener el valor actual.");
        
        boolean modificado = false;
        
        // Nuevo nombre
        System.out.print("Nuevo nombre [" + gasto.getNombre() + "]: ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty() && !nombre.equals(gasto.getNombre())) {
            gasto.setNombre(nombre);
            modificado = true;
        }
        
        // Nueva cantidad
        System.out.print("Nueva cantidad [" + gasto.getCantidad() + "]: ");
        String cantidadStr = scanner.nextLine().trim();
        if (!cantidadStr.isEmpty()) {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad != gasto.getCantidad()) {
                    gasto.setCantidad(cantidad);
                    modificado = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Cantidad inválida, se mantiene la actual.");
            }
        }
        
        // Nueva fecha
        System.out.print("Nueva fecha [" + gasto.getFecha().format(DATE_FORMAT) + "]: ");
        String fechaStr = scanner.nextLine().trim();
        if (!fechaStr.isEmpty()) {
            try {
                LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMAT);
                if (!fecha.equals(gasto.getFecha())) {
                    gasto.setFecha(fecha);
                    modificado = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha inválido, se mantiene la actual.");
            }
        }
        
        // Nueva descripción
        System.out.print("Nueva descripción [" + (gasto.getDescripcion() != null ? gasto.getDescripcion() : "") + "]: ");
        String descripcion = scanner.nextLine().trim();
        if (!descripcion.isEmpty() && !descripcion.equals(gasto.getDescripcion())) {
            gasto.setDescripcion(descripcion);
            modificado = true;
        }
        
        if (modificado) {
            // Para persistir los cambios, necesitamos eliminar y volver a agregar el gasto
            // o actualizar la cuenta en el repositorio
            try {
                // Forzar actualización de la cuenta en el repositorio
                gestor.eliminarGastoDeCuenta(cuentaActual, gasto);
                gestor.agregarGastoACuenta(cuentaActual, gasto);
                System.out.println("\n✓ Gasto modificado exitosamente.");
            } catch (Exception e) {
                System.out.println("\n✗ Error al modificar el gasto: " + e.getMessage());
            }
        } else {
            System.out.println("\nNo se realizaron cambios.");
        }
    }
    
    private void borrarGasto() throws ErrorPersistenciaException {
        List<Gasto> gastos = cuentaActual.getGastos();
        
        if (gastos.isEmpty()) {
            System.out.println("\nNo hay gastos para borrar.");
            return;
        }
        
        listarGastos();
        
        System.out.print("\nNúmero del gasto a borrar: ");
        String numStr = scanner.nextLine().trim();
        int index;
        try {
            index = Integer.parseInt(numStr) - 1;
            if (index < 0 || index >= gastos.size()) {
                System.out.println("Error: Número de gasto inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Número inválido.");
            return;
        }
        
        Gasto gasto = gastos.get(index);
        System.out.print("¿Confirmar borrado de '" + gasto.getNombre() + "'? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        
        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            boolean exito = gestor.eliminarGastoDeCuenta(cuentaActual, gasto);
            if (exito) {
                System.out.println("\n✓ Gasto eliminado exitosamente.");
            } else {
                System.out.println("\n✗ Error al eliminar el gasto.");
            }
        } else {
            System.out.println("\nOperación cancelada.");
        }
    }
    
    private void cambiarCuenta() {
        List<Cuenta> cuentas = gestor.getCuentas();
        
        if (cuentas.size() <= 1) {
            System.out.println("\nSolo hay una cuenta disponible.");
            return;
        }
        
        System.out.println("\n═══ CUENTAS DISPONIBLES ═══");
        for (int i = 0; i < cuentas.size(); i++) {
            Cuenta c = cuentas.get(i);
            String tipo = c instanceof CuentaPersonal ? "Personal" : "Compartida";
            System.out.printf("%d. %s (%s) - %d gastos\n", 
                i + 1, c.getNombre(), tipo, c.getNumeroGastos());
        }
        
        System.out.print("\nSelecciona cuenta (número): ");
        String numStr = scanner.nextLine().trim();
        try {
            int index = Integer.parseInt(numStr) - 1;
            if (index >= 0 && index < cuentas.size()) {
                cuentaActual = cuentas.get(index);
                System.out.println("\n✓ Cuenta cambiada a: " + cuentaActual.getNombre());
            } else {
                System.out.println("Error: Número de cuenta inválido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Número inválido.");
        }
    }
}
