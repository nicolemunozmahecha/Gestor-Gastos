package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;


public class Notificacion {
    private String id;
    private String mensaje;
    private LocalDateTime fechaHora;
    private Alerta alertaOrigen;
    private boolean leida;
    


    public Notificacion(Alerta alertaOrigen) {
        this.id = UUID.randomUUID().toString();
        //this.mensaje = mensaje; Hay que escribir el mensaje
        this.fechaHora = LocalDateTime.now();
        this.alertaOrigen = alertaOrigen;
        this.leida = false;
    }
    

    public Notificacion(String id, String mensaje, LocalDateTime fechaHora, Alerta alertaOrigen, boolean leida) {
        
        this.id = id;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.alertaOrigen = alertaOrigen;
        this.leida = leida;
    }
    


    public void marcarComoLeida() {
        this.leida = true;
    }
    

    public void marcarComoNoLeida() {
        this.leida = false;
    }
 
    public String getFechaHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fechaHora.format(formatter);
    }
    
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public Alerta getAlertaOrigen() {
        return alertaOrigen;
    }
    
    public boolean isLeida() {
        return leida;
    }
   
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Notificacion that = (Notificacion) obj;
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
