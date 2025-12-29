package tds.modelo.impl;

import tds.modelo.Alerta;
import tds.modelo.Categoria;
import tds.modelo.EstrategiaAlerta;
import tds.modelo.PeriodoAlerta;
import tds.modelo.estrategias.EstrategiaAlertaFactory;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AlertaImpl implements Alerta {
	
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("limite")
    private double limite;
    
    @JsonProperty("periodo")
    private PeriodoAlerta periodo;
    
    @JsonProperty("categoria")
    private CategoriaImpl categoria; // null si la alerta es general (no específica de categoría)    


    @JsonIgnore
    private transient EstrategiaAlerta estrategia;

    // Constructor sin argumentos para json
    public AlertaImpl() {
        this.nombre = "";
        this.limite = 0.0;
        this.periodo = PeriodoAlerta.MENSUAL;
        this.categoria = null;
        this.estrategia = EstrategiaAlertaFactory.crearOporDefecto(this.periodo);
    }

    public AlertaImpl(String nombre, double limite, PeriodoAlerta periodo) {
        this(nombre, limite, periodo, null);
    }
   
    public AlertaImpl(String nombre, double limite, PeriodoAlerta periodo, Categoria categoria) {
        this.nombre = nombre;
        this.limite = limite;
        this.periodo = periodo;
        // Convertir a implementación concreta
        this.categoria = categoria instanceof CategoriaImpl ? (CategoriaImpl) categoria :
                        (categoria != null ? new CategoriaImpl(categoria.getNombre(), categoria.isPredefinida()) : null);
        this.estrategia = EstrategiaAlertaFactory.crearOporDefecto(this.periodo);
    }
    
    @Override
    public boolean superaLimite(double gastoActual) {
        return gastoActual > limite;
    }
    
    @Override
    public boolean esGeneral() {
        return categoria == null;
    }
    
    // Getters y Setters
    @Override
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public double getLimite() {
        return limite;
    }
    
    @Override
    public void setLimite(double limite) {
        this.limite = limite;
    }
    
    @Override
    public PeriodoAlerta getPeriodo() {
        return periodo;
    }
    
    @Override
    public void setPeriodo(PeriodoAlerta periodo) {
        this.periodo = periodo;
        this.estrategia = EstrategiaAlertaFactory.crearOporDefecto(this.periodo);
    }

    @Override
    public EstrategiaAlerta getEstrategia() {
        if (estrategia == null) {
            estrategia = EstrategiaAlertaFactory.crearOporDefecto(this.periodo);
        }
        return estrategia;
    }
    
    @JsonIgnore
    @Override
    public Categoria getCategoria() {
        return categoria;
    }
    
    //TODO casting
    @JsonIgnore
    @Override
    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            this.categoria = null;
            return;
        }
        this.categoria = (categoria instanceof CategoriaImpl)
                ? (CategoriaImpl) categoria
                : new CategoriaImpl(categoria.getNombre(), categoria.isPredefinida());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlertaImpl alerta = (AlertaImpl) obj;
        return Double.compare(alerta.limite, limite) == 0 &&
               Objects.equals(nombre, alerta.nombre) &&
               periodo == alerta.periodo &&
               Objects.equals(categoria, alerta.categoria);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre, limite, periodo, categoria);
    }
    
    @Override
    public String toString() {
        String tipo = esGeneral() ? "General" : "Categoría: " + categoria.getNombre();
        return String.format("%s - %s - Límite: %.2f€ - Periodo: %s", nombre, tipo, limite, periodo);
    }
}