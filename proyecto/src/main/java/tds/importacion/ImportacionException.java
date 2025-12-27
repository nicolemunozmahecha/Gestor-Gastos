package tds.importacion;

public class ImportacionException extends Exception {
    public ImportacionException(String message) {
        super(message);
    }

    public ImportacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
