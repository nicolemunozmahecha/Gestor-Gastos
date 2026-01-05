package tds.adapters.repository.exceptions;

public class ErrorPersistenciaException extends Exception {

    public ErrorPersistenciaException(Throwable causa) {
        super(causa);
    }
    public ErrorPersistenciaException(String mensaje) {
        super(mensaje);
    }
}
