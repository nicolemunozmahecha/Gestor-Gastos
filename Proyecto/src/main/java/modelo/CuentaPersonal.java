package modelo;

public class CuentaPersonal extends Cuenta {
    private String propietario;
    
    public CuentaPersonal(String nombre, String propietario) {
        super(nombre);
        this.propietario = propietario;
    }
    
    public CuentaPersonal(String propietario) {
        this("Cuenta de " + propietario, propietario);
    }
    
    @Override
    public void agregarGasto(Gasto gasto) {
        // En cuenta personal, el pagador siempre es null o el propietario
        super.agregarGasto(gasto);
    }
    
    // Getter y Setter
    public String getPropietario() {
        return propietario;
    }
    
    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }
    
    @Override
    public String toString() {
        return String.format("Cuenta Personal: %s (%s) - %d gastos - Total: %.2f€", nombre, propietario, gastos.size(), calcularTotal());
    }
}
