package tds.modelo;

public interface Persona {
    String getNombre();
    void setNombre(String nombre);
    double getSaldo();
    double getPorcentaje();
    void setSaldo(double saldo);
    void setPorcentaje(double p);
    void actualizarSaldo(double cantidad);
    void resetearSaldo();
}
