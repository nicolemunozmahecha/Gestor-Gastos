package tds.modelo.impl;

import tds.modelo.Notificacion;
import tds.modelo.Alerta;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class NotificacionImpl implements Notificacion {
    private String id;
    private String mensaje;
    private LocalDateTime fechaHora;
    private Alerta alertaOrigen;
    private boolean leida;
    
    public NotificacionImpl(Alerta alertaOrigen) {
        this.id = UUID.randomUUID().toString();
        //this.mensaje = mensaje; Hay que escribir el mensaje
        this.fechaHora = LocalDateTime.now();
        this.alertaOrigen = alertaOrigen;
        this.leida = false;
    }
    
    public NotificacionImpl(String id, String mensaje, LocalDateTime fechaHora, Alerta alertaOrigen, boolean leida) {
        this.id = id;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.alertaOrigen = alertaOrigen;
        this.leida = leida;
    }
    
    @Override
    public void marcarComoLeida() {
        this.leida = true;
    }
    
    @Override
    public void marcarComoNoLeida() {
        this.leida = false;
    }
 
    @Override
    public String getFechaHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fechaHora.format(formatter);
    }
    
    // Getters y Setters
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getMensaje() {
        return mensaje;
    }
    
    @Override
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    @Override
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    @Override
    public Alerta getAlertaOrigen() {
        return alertaOrigen;
    }
    
    @Override
    public boolean isLeida() {
        return leida;
    }
   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NotificacionImpl that = (NotificacionImpl) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        String estado = leida ? "Leída" : "No leída";
        return String.format("[%s] %s - %s - Alerta: %s (%s)", estado, getFechaHoraFormateada(), mensaje, alertaOrigen.getNombre(), id.substring(0, 8));
    }
}
