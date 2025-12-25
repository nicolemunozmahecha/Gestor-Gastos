package tds.modelo;

public interface Persona {
    String getNombre();
    void setNombre(String nombre);
    double getSaldo();
    int getPorcentaje();
    void setSaldo(double saldo);
    void setPorcentaje(int p);
    void actualizarSaldo(double cantidad);
    void resetearSaldo();
}
