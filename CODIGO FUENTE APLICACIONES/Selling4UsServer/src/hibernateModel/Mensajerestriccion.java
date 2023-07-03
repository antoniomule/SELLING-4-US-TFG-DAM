package hibernateModel;
// Generated 03-abr-2023 13:06:50 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Mensajeresnotificacion generated by hbm2java
 */
public class Mensajerestriccion  implements java.io.Serializable {


     private Integer idMensajeRestriccion;
     private String tipoMensaje;
     private String mensaje;
     private Set notificacions = new HashSet(0);

    public Mensajerestriccion() {
    }

	
    public Mensajerestriccion(String tipoMensaje, String mensaje) {
        this.tipoMensaje = tipoMensaje;
        this.mensaje = mensaje;
    }
    public Mensajerestriccion(String tipoMensaje, String mensaje, Set notificacions) {
       this.tipoMensaje = tipoMensaje;
       this.mensaje = mensaje;
       this.notificacions = notificacions;
    }

    public Integer getIdMensajeRestriccion() {
        return idMensajeRestriccion;
    }

    public void setIdMensajeRestriccion(Integer idMensajeRestriccion) {
        this.idMensajeRestriccion = idMensajeRestriccion;
    }
   
   
    public String getTipoMensaje() {
        return this.tipoMensaje;
    }
    
    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }
    public String getMensaje() {
        return this.mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public Set getNotificacions() {
        return this.notificacions;
    }
    
    public void setNotificacions(Set notificacions) {
        this.notificacions = notificacions;
    }




}


