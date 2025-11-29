package tds.modelo;

public interface Alerta {
    String getNombre();
    void setNombre(String nombre);
    double getLimite();
    void setLimite(double limite);
    PeriodoAlerta getPeriodo();
    void setPeriodo(PeriodoAlerta periodo);
    Categoria getCategoria();
    void setCategoria(Categoria categoria);
    boolean superaLimite(double gastoActual);
    boolean esGeneral();
}
