package tds.modelo;

import java.util.List;
import java.util.Map;

public interface CuentaCompartida extends Cuenta {
    Map<Persona, Double> calcularSaldos();
    double getSaldo(Persona persona);
    double getSaldo(String nombrePersona);
    double getTotalGastadoPor(Persona persona);
    List<Persona> getPersonas();
    EstrategiaDistribucion getEstrategiaDistribucion();
    void setEstrategiaDistribucion(EstrategiaDistribucion estrategia);
    boolean contienePersona(Persona persona);
    int getNumeroPersonas();
}
