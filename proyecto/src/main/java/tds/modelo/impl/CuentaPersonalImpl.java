package tds.modelo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import tds.modelo.CuentaPersonal;
import tds.modelo.Gasto;

public class CuentaPersonalImpl extends CuentaImpl implements CuentaPersonal {
	
	@JsonProperty("propietario")
	private String propietario;

	public CuentaPersonalImpl() {
	    super();
	    this.propietario = "";
	}
    
    public CuentaPersonalImpl(String nombre, String propietario) {
        super(nombre);
        this.propietario = propietario;
    }
    
    public CuentaPersonalImpl(String propietario) {
        this("Cuenta de " + propietario, propietario);
    }
    
    @Override
    public void agregarGasto(Gasto gasto) {
        // En cuenta personal, el pagador siempre es null o el propietario
        super.agregarGasto(gasto);
    }
    
    // Getter y Setter
    @Override
    public String getPropietario() {
        return propietario;
    }
    
    @Override
    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }
    
    @Override
    public String toString() {
        return String.format("Cuenta Personal: %s (%s) - %d gastos - Total: %.2f€", nombre, propietario, gastos.size(), calcularTotal());
    }
}