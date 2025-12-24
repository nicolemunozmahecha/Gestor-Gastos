package tds.modelo.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Clase que almacena todos los datos del sistema de gastos para persistencia JSON
 * NOTA: Los gastos NO tienen un array separado - existen solo dentro de las cuentas
 */
public class DatosGastos {
    
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
        this.cuentasPersonales = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.personas = new ArrayList<>();
    }
    
    public DatosGastos(List<CuentaPersonalImpl> cuentasPersonales,
                      List<CuentaCompartidaImpl> cuentasCompartidas,
                      List<CategoriaImpl> categorias, List<AlertaImpl> alertas,
                      List<PersonaImpl> personas) {
        this.cuentasPersonales = cuentasPersonales;
        this.cuentasCompartidas = cuentasCompartidas;
        this.categorias = categorias;
        this.alertas = alertas;
        this.personas = personas;
    }
    
    // Métodos para obtener todas las cuentas como CuentaImpl
    @JsonIgnore
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
    
    // Método para establecer cuentas desde un JSON antiguo que tiene "cuentas" en lugar de separadas
    @JsonSetter("cuentas")
    public void setCuentasFromOldFormat(List<CuentaImpl> cuentas) {
        this.cuentasPersonales = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        
        if (cuentas != null) {
            for (CuentaImpl cuenta : cuentas) {
                if (cuenta instanceof CuentaPersonalImpl) {
                    this.cuentasPersonales.add((CuentaPersonalImpl) cuenta);
                } else if (cuenta instanceof CuentaCompartidaImpl) {
                    this.cuentasCompartidas.add((CuentaCompartidaImpl) cuenta);
                }
            }
        }
    }
    @JsonIgnore
    // Método para establecer cuentas (separa por tipo)
    public void setCuentas(List<CuentaImpl> cuentas) {
        this.cuentasPersonales = new ArrayList<>();
        this.cuentasCompartidas = new ArrayList<>();
        
        if (cuentas != null) {
            for (CuentaImpl cuenta : cuentas) {
                if (cuenta instanceof CuentaPersonalImpl) {
                    this.cuentasPersonales.add((CuentaPersonalImpl) cuenta);
                } else if (cuenta instanceof CuentaCompartidaImpl) {
                    this.cuentasCompartidas.add((CuentaCompartidaImpl) cuenta);
                }
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