package tds.modelo.impl;

import tds.modelo.Notificacion;
import tds.modelo.Alerta;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificacionImpl implements Notificacion {
    @JsonProperty("id")
    private String id;

    @JsonProperty("mensaje")
    private String mensaje;

    @JsonProperty("fechaHora")
    private LocalDateTime fechaHora;

    @JsonProperty("alertaOrigen")
    private AlertaImpl alertaOrigen;

    @JsonProperty("leida")
    private boolean leida;

    @JsonProperty("periodoInicio")
    private LocalDate periodoInicio;

    @JsonProperty("periodoFin")
    private LocalDate periodoFin;

    // Constructor sin argumentos para json
    public NotificacionImpl() {
        this.id = UUID.randomUUID().toString();
        this.mensaje = "";
        this.fechaHora = LocalDateTime.now();
        this.alertaOrigen = null;
        this.leida = false;
        this.periodoInicio = null;
        this.periodoFin = null;
    }
    
    public NotificacionImpl(Alerta alertaOrigen, String mensaje, LocalDate periodoInicio, LocalDate periodoFin) {
        this.id = UUID.randomUUID().toString();
        this.mensaje = mensaje;
        this.fechaHora = LocalDateTime.now();
        this.alertaOrigen = (alertaOrigen instanceof AlertaImpl)
                ? (AlertaImpl) alertaOrigen
                : (alertaOrigen != null ? new AlertaImpl(alertaOrigen.getNombre(), alertaOrigen.getLimite(), alertaOrigen.getPeriodo(), alertaOrigen.getCategoria()) : null);
        this.leida = false;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
    }
    
    public NotificacionImpl(String id, String mensaje, LocalDateTime fechaHora, Alerta alertaOrigen, boolean leida, LocalDate periodoInicio, LocalDate periodoFin) {
        this.id = id;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.alertaOrigen = (alertaOrigen instanceof AlertaImpl)
                ? (AlertaImpl) alertaOrigen
                : (alertaOrigen != null ? new AlertaImpl(alertaOrigen.getNombre(), alertaOrigen.getLimite(), alertaOrigen.getPeriodo(), alertaOrigen.getCategoria()) : null);
        this.leida = leida;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
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

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
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
        String nombreAlerta = (alertaOrigen == null) ? "(sin alerta)" : alertaOrigen.getNombre();
        return String.format("[%s] %s - %s - Alerta: %s (%s)", estado, getFechaHoraFormateada(), mensaje, nombreAlerta, id.substring(0, 8));
    }
}
