package tds.modelo;

import java.time.LocalDate;
import java.util.List;

public interface Filtro {
    List<Gasto> aplicar(List<Gasto> gastos);
    boolean cumpleFiltro(Gasto gasto);
    boolean hayFiltrosActivos();
    void limpiar();
    void agregarMes(String mes);
    void agregarCategoria(Categoria categoria);
    void setRangoFechas(LocalDate inicio, LocalDate fin);
    List<String> getMeses();
    void setMeses(List<String> meses);
    LocalDate getFechaInicio();
    void setFechaInicio(LocalDate fechaInicio);
    LocalDate getFechaFin();
    void setFechaFin(LocalDate fechaFin);
    List<Categoria> getCategorias();
    void setCategorias(List<Categoria> categorias);
}
