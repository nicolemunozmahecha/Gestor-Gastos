package tds.modelo.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import tds.modelo.CuentaCompartida;
import tds.modelo.EstrategiaDistribucion;
import tds.modelo.Gasto;
import tds.modelo.Persona;
import tds.modelo.estrategias.EstrategiaDistribucionFactory;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CuentaCompartidaImpl extends CuentaImpl implements CuentaCompartida {

    @JsonProperty("personas")
    private List<PersonaImpl> personas;

    // Tipo de estrategia (por defecto: EQUITATIVA).
    @JsonProperty("estrategiaId")
    private String estrategiaId;

    @JsonIgnore
    private transient EstrategiaDistribucion estrategia;

    // Constructor sin argumentos para Jackson
    public CuentaCompartidaImpl() {
        super("");
        this.personas = new ArrayList<>();
        this.estrategiaId = DistribucionEquitativaImpl.ID;
        this.estrategia = null;
    }

    public CuentaCompartidaImpl(String nombre, List<Persona> personas) {
        super(nombre);
        this.personas = convertirAPersonaImpl(personas);
        this.estrategiaId = DistribucionEquitativaImpl.ID;
        this.estrategia = null;
        aplicarPorcentajesEquitativos();
        inicializarSaldos();
    }

    public CuentaCompartidaImpl(String nombre, List<Persona> personas, EstrategiaDistribucion estrategia) {
        super(nombre);
        this.personas = convertirAPersonaImpl(personas);
        this.estrategia = estrategia;
        this.estrategiaId = (estrategia != null) ? estrategia.getId() : DistribucionEquitativaImpl.ID;

        if (DistribucionEquitativaImpl.ID.equals(this.estrategiaId)) {
            aplicarPorcentajesEquitativos();
        } else {
            validarPorcentajesPersonalizados();
        }

        inicializarSaldos();
    }

    private static List<PersonaImpl> convertirAPersonaImpl(List<Persona> personas) {
        List<PersonaImpl> out = new ArrayList<>();
        if (personas == null) return out;

        for (Persona p : personas) {
            if (p instanceof PersonaImpl) {
                out.add((PersonaImpl) p);
            } else {
                PersonaImpl pi = new PersonaImpl(p.getNombre(), p.getSaldo());
                pi.setPorcentaje(p.getPorcentaje());
                out.add(pi);
            }
        }
        return out;
    }

    private void inicializarSaldos() {
        for (PersonaImpl persona : personas) {
            persona.setSaldo(0.0);
        }
    }

    @Override
    public void agregarGasto(Gasto gasto) {
        super.agregarGasto(gasto);
        actualizarSaldos(gasto);
    }

    @Override
    public boolean eliminarGasto(Gasto gasto) {
        boolean eliminado = super.eliminarGasto(gasto);
        if (eliminado) {
            recalcularTodosSaldos();
        }
        return eliminado;
    }

    private void actualizarSaldos(Gasto gasto) {
        asegurarEstrategia();

        Persona pagador = gasto.getPagador();
        double cantidad = gasto.getCantidad();

        for (Persona persona : personas) {
            double porcentaje = estrategia.calcularPorcentaje(persona);
            double deuda = cantidad * porcentaje;

            if (persona.equals(pagador)) {
                persona.actualizarSaldo(cantidad - deuda);
            } else {
                persona.actualizarSaldo(-deuda);
            }
        }
    }

    private void recalcularTodosSaldos() {
        asegurarEstrategia();
        inicializarSaldos();
        for (Gasto gasto : gastos) {
            actualizarSaldos(gasto);
        }
    }

    @Override
    public Map<Persona, Double> calcularSaldos() {
        Map<Persona, Double> saldos = new HashMap<>();
        for (Persona persona : personas) {
            saldos.put(persona, persona.getSaldo());
        }
        return saldos;
    }

    @Override
    public double getSaldo(Persona persona) {
        return persona.getSaldo();
    }

    @Override
    public double getSaldo(String nombrePersona) {
        for (Persona persona : personas) {
            if (persona.getNombre().equals(nombrePersona)) {
                return persona.getSaldo();
            }
        }
        throw new IllegalArgumentException("No existe una persona con ese nombre en la cuenta");
    }

    @Override
    public double getTotalGastadoPor(Persona persona) {
        return gastos.stream()
                .filter(g -> persona.equals(g.getPagador()))
                .mapToDouble(Gasto::getCantidad)
                .sum();
    }

    @Override
    public List<Persona> getPersonas() {
        return Collections.unmodifiableList(new ArrayList<>(personas));
    }

    // ===== Estrategia =====

    @JsonIgnore
    @Override
    public EstrategiaDistribucion getEstrategiaDistribucion() {
        asegurarEstrategia();
        return estrategia;
    }

    @JsonIgnore
    @Override
    public void setEstrategiaDistribucion(EstrategiaDistribucion estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser null");
        }

        this.estrategia = estrategia;
        this.estrategiaId = estrategia.getId();

        if (DistribucionEquitativaImpl.ID.equals(this.estrategiaId)) {
            aplicarPorcentajesEquitativos();
        } else {
            // Para personalizada, los porcentajes se esperan en Persona.
            // (La vista ya los modifica antes de aplicar.)
            validarPorcentajesPersonalizados();
        }

        recalcularTodosSaldos();
    }

    private void asegurarEstrategia() {
        if (estrategia != null) return;

        if (estrategiaId == null || estrategiaId.isBlank()) {
            estrategiaId = inferirEstrategiaId();
        }

        if (DistribucionEquitativaImpl.ID.equals(estrategiaId)) {
            // Asegurar que los porcentajes de Persona estén en un estado válido
            aplicarPorcentajesEquitativosSiHaceFalta();
        } else {
            // Si la estrategia es personalizada pero los porcentajes no suman 100,
            // volvemos a equitativa para no romper el cálculo.
            if (!sonPorcentajesPersonalizadosValidos()) {
                estrategiaId = DistribucionEquitativaImpl.ID;
                aplicarPorcentajesEquitativos();
            }
        }

        estrategia = EstrategiaDistribucionFactory.crearOporDefecto(estrategiaId, new ArrayList<>(personas));
    }

    private String inferirEstrategiaId() {
        // Si hay porcentajes bien formados y NO son todos iguales, interpretamos personalizada.
        if (personas == null || personas.isEmpty()) return DistribucionEquitativaImpl.ID;

        int suma = 0;
        boolean todosIguales = true;
        int primero = personas.get(0).getPorcentaje();

        for (PersonaImpl p : personas) {
            suma += p.getPorcentaje();
            if (p.getPorcentaje() != primero) {
                todosIguales = false;
            }
        }

        if (suma == 100 && !todosIguales) {
            return DistribucionPersonalizadaImpl.ID;
        }
        return DistribucionEquitativaImpl.ID;
    }

    private void aplicarPorcentajesEquitativosSiHaceFalta() {
        if (personas == null || personas.isEmpty()) return;

        int suma = 0;
        for (PersonaImpl p : personas) {
            suma += p.getPorcentaje();
        }
        if (suma != 100) {
            aplicarPorcentajesEquitativos();
        }
    }

    private void aplicarPorcentajesEquitativos() {
        if (personas == null || personas.isEmpty()) return;

        int n = personas.size();
        int base = 100 / n;
        int resto = 100 % n;

        for (int i = 0; i < n; i++) {
            personas.get(i).setPorcentaje(base + (i < resto ? 1 : 0));
        }
    }

    private boolean sonPorcentajesPersonalizadosValidos() {
        if (personas == null || personas.isEmpty()) return false;
        int suma = 0;
        for (PersonaImpl p : personas) {
            if (p.getPorcentaje() < 0) return false;
            suma += p.getPorcentaje();
        }
        return suma == 100;
    }

    private void validarPorcentajesPersonalizados() {
        if (!sonPorcentajesPersonalizadosValidos()) {
            throw new IllegalArgumentException("Los porcentajes personalizados deben sumar 100");
        }
    }

    @Override
    public boolean contienePersona(Persona persona) {
        return personas.contains(persona);
    }

    @Override
    public int getNumeroPersonas() {
        return personas.size();
    }
}
