package hibernateModel;
// Generated 03-abr-2023 13:06:50 by Hibernate Tools 4.3.1



/**
 * Moderador generated by hbm2java
 */
public class Moderador  implements java.io.Serializable {


     private Integer idModerador;
     private Categoria categoria;
     private Usuario usuario;

    public Moderador() {
    }

    public Moderador(Categoria categoria, Usuario usuario) {
       this.categoria = categoria;
       this.usuario = usuario;
    }
   
    public Integer getIdModerador() {
        return this.idModerador;
    }
    
    public void setIdModerador(Integer idModerador) {
        this.idModerador = idModerador;
    }
    public Categoria getCategoria() {
        return this.categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    public Usuario getUsuario() {
        return this.usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }




}


