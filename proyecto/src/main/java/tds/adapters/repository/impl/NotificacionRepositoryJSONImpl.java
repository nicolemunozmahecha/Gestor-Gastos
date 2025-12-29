package tds.adapters.repository.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tds.Configuracion;
import tds.adapters.repository.NotificacionRepository;
import tds.adapters.repository.exceptions.ElementoExistenteException;
import tds.adapters.repository.exceptions.ElementoNoEncontradoException;
import tds.adapters.repository.exceptions.ErrorPersistenciaException;
import tds.modelo.impl.DatosGastos;
import tds.modelo.impl.NotificacionImpl;

public class NotificacionRepositoryJSONImpl implements NotificacionRepository {

    private List<NotificacionImpl> notificaciones = null;
    private String rutaFichero = null;

    private void cargaNotificaciones() throws ErrorPersistenciaException {
        try {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
            this.notificaciones = cargarNotificaciones(rutaFichero);
            if (notificaciones == null) notificaciones = new ArrayList<>();
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }

    @Override
    public List<? extends NotificacionImpl> getNotificaciones() {
        if (notificaciones == null) {
            try {
                cargaNotificaciones();
            } catch (ErrorPersistenciaException e) {
                System.err.println("ERROR: No se han podido cargar notificaciones desde JSON (se devuelve lista vacía).");
                e.printStackTrace();
                notificaciones = new ArrayList<>();
            }
        }
        return notificaciones;
    }

    @Override
    public NotificacionImpl findById(String id) throws ElementoNoEncontradoException {
        if (notificaciones == null) {
            getNotificaciones();
        }

        Optional<NotificacionImpl> n = notificaciones.stream()
                .filter(no -> no.getId().equals(id))
                .findFirst();

        if (n.isEmpty()) {
            throw new ElementoNoEncontradoException("La notificación " + id + " no existe");
        }
        return n.orElse(null);
    }

    @Override
    public void addNotificacion(NotificacionImpl notificacion) throws ElementoExistenteException, ErrorPersistenciaException {
        if (notificaciones == null) {
            getNotificaciones();
        }

        if (rutaFichero == null) {
            rutaFichero = Configuracion.getInstancia().getRutaDatos();
        }

        boolean yaExiste = notificaciones.stream().anyMatch(n -> n.getId().equals(notificacion.getId()));
        if (yaExiste) {
            throw new ElementoExistenteException("La notificación " + notificacion.getId() + " ya existe");
        }

        notificaciones.add(notificacion);
        guardarNotificaciones();
    }

    @Override
    public void removeNotificacion(NotificacionImpl notificacion) throws ErrorPersistenciaException {
        if (notificaciones == null) {
            getNotificaciones();
        }

        if (!notificaciones.contains(notificacion)) return;

        notificaciones.remove(notificacion);
        guardarNotificaciones();
    }

    @Override
    public NotificacionImpl updateNotificacion(NotificacionImpl notificacion) throws ErrorPersistenciaException {
        if (notificaciones == null) {
            getNotificaciones();
        }

        guardarNotificaciones();
        return notificacion;
    }

    private List<NotificacionImpl> cargarNotificaciones(String rutaFichero) throws Exception {
        File ficheroStream = new File(rutaFichero);
        if (!ficheroStream.exists()) {
            return new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
        DatosGastos datosCargados = mapper.readValue(ficheroStream, new TypeReference<DatosGastos>() {});

        datos.setCuentas(datosCargados.getCuentas());
        datos.setCategorias(datosCargados.getCategorias());
        datos.setAlertas(datosCargados.getAlertas());
        datos.setPersonas(datosCargados.getPersonas());
        datos.setNotificaciones(datosCargados.getNotificaciones());

        this.notificaciones = datosCargados.getNotificaciones();
        if (this.notificaciones == null) this.notificaciones = new ArrayList<>();
        return this.notificaciones;
    }

    @Override
    public void guardarNotificaciones() throws ErrorPersistenciaException {
        try {
            if (rutaFichero == null) {
                rutaFichero = Configuracion.getInstancia().getRutaDatos();
            }

            File ficheroJson = new File(rutaFichero);
            if (ficheroJson.getParentFile() != null) {
                ficheroJson.getParentFile().mkdirs();
            }

            DatosGastos datos = (DatosGastos) Configuracion.getInstancia().getDatosGastos();
            datos.setNotificaciones(notificaciones);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.writerWithDefaultPrettyPrinter().writeValue(ficheroJson, datos);
        } catch (Exception e) {
            throw new ErrorPersistenciaException(e);
        }
    }
}
