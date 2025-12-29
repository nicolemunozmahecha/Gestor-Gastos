package tds.adapters.repository;

import java.util.List;

import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.NotificacionImpl;

public interface NotificacionRepository {


    List<? extends NotificacionImpl> getNotificaciones();

    NotificacionImpl findById(String id) throws ElementoNoEncontradoException;

    void addNotificacion(NotificacionImpl notificacion) throws ElementoExistenteException, ErrorPersistenciaException;
    
    void removeNotificacion(NotificacionImpl notificacion) throws ErrorPersistenciaException;

    NotificacionImpl updateNotificacion(NotificacionImpl notificacion) throws ErrorPersistenciaException;

    void guardarNotificaciones() throws ErrorPersistenciaException;
}
