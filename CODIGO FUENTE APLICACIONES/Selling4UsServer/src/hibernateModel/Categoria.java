package hibernateModel;
// Generated 03-abr-2023 13:06:50 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Categoria generated by hbm2java
 */
public class Categoria  implements java.io.Serializable {


     private Integer idCategoria;
     private String tipo;
    

    public Categoria() {
    }

	
    public Categoria(String tipo) {
        this.tipo = tipo;
    }
    
   
    public Integer getIdCategoria() {
        return this.idCategoria;
    }
    
    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
    public String getTipo() {
        return this.tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
  



}


