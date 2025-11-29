package tds.modelo.impl;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que almacena todos los datos del sistema de gastos para persistencia JSON
 */
public class DatosGastos {
    
    @JsonProperty("gastos")
    private List<GastoImpl> gastos;
    
    @JsonProperty("cuentasPersonales")
    private List<CuentaPersonalImpl> cuentasPersonales;
    
    @JsonProperty("cuentasCompartidas")
    private List<CuentaCompartidaImpl> cuentasCompartidas;
    
    @JsonProperty("categorias")
    private List<CategoriaImpl> categorias;
    
    @JsonProperty("alertas")
    private List<AlertaImpl> alertas;
    
    @JsonProperty("personas")
    private List<PersonaImpl> personas;
    
    public DatosGastos() {
        this.gastos = new ArrayList<>();
        this.cuentasPersonales = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.personas = new ArrayList<>();
    }
    
    public DatosGastos(List<GastoImpl> gastos, List<CuentaPersonalImpl> cuentasPersonales,
                      List<CuentaCompartidaImpl> cuentasCompartidas,
                      List<CategoriaImpl> categorias, List<AlertaImpl> alertas,
                      List<PersonaImpl> personas) {
        this.gastos = gastos;
        this.cuentasPersonales = cuentasPersonales;
        this.cuentasCompartidas = cuentasCompartidas;
        this.categorias = categorias;
        this.alertas = alertas;
        this.personas = personas;
    }
    
    // Getters y Setters
    public List<GastoImpl> getGastos() {
        return gastos;
    }
    
    public void setGastos(List<GastoImpl> gastos) {
        this.gastos = gastos;
    }
    
    // Métodos para obtener todas las cuentas como CuentaImpl
    public List<CuentaImpl> getCuentas() {
        List<CuentaImpl> todasLasCuentas = new ArrayList<>();
        if (cuentasPersonales != null) {
            todasLasCuentas.addAll(cuentasPersonales);
        }
        if (cuentasCompartidas != null) {
            todasLasCuentas.addAll(cuentasCompartidas);
        }
        return todasLasCuentas;
    }
    
    // Método para establecer cuentas (separa por tipo)
    public void setCuentas(List<CuentaImpl> cuentas) {
        this.cuentasPersonales = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        
        for (CuentaImpl cuenta : cuentas) {
            if (cuenta instanceof CuentaPersonalImpl) {
                this.cuentasPersonales.add((CuentaPersonalImpl) cuenta);
            } else if (cuenta instanceof CuentaCompartidaImpl) {
                this.cuentasCompartidas.add((CuentaCompartidaImpl) cuenta);
            }
        }
    }
    
    public List<CuentaPersonalImpl> getCuentasPersonales() {
        return cuentasPersonales;
    }
    
    public void setCuentasPersonales(List<CuentaPersonalImpl> cuentasPersonales) {
        this.cuentasPersonales = cuentasPersonales;
    }
    
    public List<CuentaCompartidaImpl> getCuentasCompartidas() {
        return cuentasCompartidas;
    }
    
    public void setCuentasCompartidas(List<CuentaCompartidaImpl> cuentasCompartidas) {
        this.cuentasCompartidas = cuentasCompartidas;
    }
    
    public List<CategoriaImpl> getCategorias() {
        return categorias;
    }
    
    public void setCategorias(List<CategoriaImpl> categorias) {
        this.categorias = categorias;
    }
    
    public List<AlertaImpl> getAlertas() {
        return alertas;
    }
    
    public void setAlertas(List<AlertaImpl> alertas) {
        this.alertas = alertas;
    }
    
    public List<PersonaImpl> getPersonas() {
        return personas;
    }
    
    public void setPersonas(List<PersonaImpl> personas) {
        this.personas = personas;
    }
}