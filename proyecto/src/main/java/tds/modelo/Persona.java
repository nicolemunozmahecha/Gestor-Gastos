package tds.modelo;

public interface Persona {
    String getNombre();
    void setNombre(String nombre);
    double getSaldo();
    void setSaldo(double saldo);
    void actualizarSaldo(double cantidad);
    void resetearSaldo();
}
