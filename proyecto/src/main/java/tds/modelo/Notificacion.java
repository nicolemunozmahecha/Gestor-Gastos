package tds.modelo;

import java.time.LocalDateTime;

public interface Notificacion {
    String getId();
    String getMensaje();
    void setMensaje(String mensaje);
    LocalDateTime getFechaHora();
    Alerta getAlertaOrigen();
    boolean isLeida();
    void marcarComoLeida();
    void marcarComoNoLeida();
    String getFechaHoraFormateada();
}
