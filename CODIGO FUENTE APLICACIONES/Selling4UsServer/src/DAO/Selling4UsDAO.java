/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;


import Utils.HibernateUtil;
import hibernateModel.Anuncio;
import hibernateModel.Categoria;
import hibernateModel.Chat;
import hibernateModel.Compra;
import hibernateModel.Favoritos;
import hibernateModel.Mensaje;
import hibernateModel.Mensajerestriccion;
import hibernateModel.Moderador;
import hibernateModel.Notificacion;
import hibernateModel.Oferta;
import hibernateModel.Rol;
import utils.SHA;
import hibernateModel.Usuario;
import hibernateModel.Valoracion;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Antonio David
 */
public class Selling4UsDAO {
    
    public static boolean getUsuarioXNombre(String nombre, String contraseña){
        Usuario usuario=null;
    Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
   
        try{
            tx = session.beginTransaction();
          
            Query query = session.createQuery("FROM Usuario where nombre=:nom and contraseña=:cont");
            query.setParameter("nom",nombre);
            query.setParameter("cont", contraseña);
            //cambiar
            usuario = (Usuario) query.uniqueResult();
            
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) tx.rollback();
                e.printStackTrace();
        }
        if(usuario != null){
                return true;
            }else {return false;}
       
    }
    
    public Usuario getUsuarioXNombreUser(String nombre, String contraseña){
       
        Usuario usuario = null;
    Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
   
        try{
            tx = session.beginTransaction();
          
            Query query = session.createQuery("FROM Usuario where nombre=:nom and contraseña=:cont");
            query.setParameter("nom",nombre);
            query.setParameter("cont", contraseña);
            
            usuario = (Usuario) query.uniqueResult();
            
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) tx.rollback();
                e.printStackTrace();
        } 
        return usuario;
       
    }
    
    
    
    public List getCategorias(){
       List lista = new ArrayList<>();
            Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            List<Categoria> Categorias = session.createQuery("FROM Categoria").list();
            for(int i=0; i<Categorias.size(); i++){
                lista.add(Categorias.get(i).getTipo());
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }
    
    
    public String getCategoriaModerador(int id) {
       String categoriaModerador=null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Moderador moderador = (Moderador) session.createQuery("FROM Moderador where idUsuario='"+id+"'").uniqueResult();
            categoriaModerador = moderador.getCategoria().getTipo();
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return categoriaModerador;
    }
    
    
    
    
        public boolean añadirModerador(String nombre, String contraseña,String fecha, String Ciudad, String categoria) throws ParseException {
        int id=0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
           
           
        try {
            tx = session.beginTransaction();
            Usuario usuario = new Usuario(nombre, contraseña, new SimpleDateFormat("d MMM yyyy").parse(fecha), Ciudad);
            
            
            Query query = session.createQuery("FROM Rol where tipoRol=:tipoRol");
            query.setParameter("tipoRol","Moderador");
            Rol rol = (Rol) query.uniqueResult();
            Set roles = new HashSet();
            roles.add(rol);
            usuario.setRoles(roles);
            
            Query query1 = session.createQuery("FROM Categoria where tipo=:tipo");
            query1.setParameter("tipo",categoria);
            
            Categoria categoriareturn = (Categoria) query1.uniqueResult();
            
            Moderador moderador = new Moderador(categoriareturn, usuario);
             id = (Integer) session.save(usuario);
            session.save(moderador);
            
           
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }if(id != 0){
                return true;
            }else {return false;}
    }
        
        
        public Integer añadirUsuarioEstandar(String nombre, String contraseña,String fecha, String Ciudad) throws ParseException {
        int id=0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
           
        try {
            tx = session.beginTransaction();
            Query query1 = session.createQuery("FROM Usuario where nombre=:nom");
            query1.setParameter("nom",nombre);
            
            if(query1.uniqueResult() ==null){
            Usuario usuario = new Usuario(nombre, contraseña, new SimpleDateFormat("dd/MM/yyyy").parse(fecha), Ciudad);
            
            Query query = session.createQuery("FROM Rol where tipoRol=:tipoRol");
            query.setParameter("tipoRol","Estandar");
            Rol rol = (Rol) query.uniqueResult();
            Set roles = new HashSet();
            roles.add(rol);
            usuario.setRoles(roles);
             id = (Integer) session.save(usuario);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return id;
    }
        
        public Boolean actualizarContraseña(int idUsuario, String contraseñaActual, String nuevaContraseña) throws ParseException {
        boolean modificado=false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
           
        try {
            tx = session.beginTransaction();
           Query query = session.createQuery("FROM Usuario where idUsuario=:id");
            query.setParameter("id", idUsuario);
           
            Usuario usuario = (Usuario) query.uniqueResult();
           if(usuario.getContraseña().equals(contraseñaActual)){
           usuario.setContraseña(nuevaContraseña);
           modificado=true;
           }
            session.update(usuario);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return modificado;
    }
        
        public String getModeradores(){
       String lista="";
            Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            List<Moderador> moderadores = session.createQuery("FROM Moderador").list();
            String moderadorString ="";
            for(int i=0; i<moderadores.size(); i++){
                moderadorString = "";
                moderadorString+=moderadores.get(i).getUsuario().getIdUsuario();
                moderadorString+="="+moderadores.get(i).getUsuario().getNombre();
                moderadorString+="="+moderadores.get(i).getUsuario().getDireccion();
                moderadorString+="="+moderadores.get(i).getCategoria().getTipo()+":";
                lista+=moderadorString;
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }
        
        
       public boolean DeleteModerador(String idUsuario){
       boolean borrado=false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
           
            Usuario user = (Usuario) session.get(Usuario.class,Integer.valueOf(idUsuario));
            Query query = session.createQuery("FROM Moderador where usuario.idUsuario=:id");
            
            query.setParameter("id",Integer.valueOf(idUsuario));
            Moderador moderador = (Moderador) query.uniqueResult();
           
            if (user != null) {
                session.delete(moderador);
                session.delete(user);
                borrado=true;
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return borrado;
    }
        
       
        public String getTiposRestriccion(){
       String lista="";
            Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            List<String> mensaje = session.createQuery("select tipoMensaje FROM Mensajerestriccion group by tipoMensaje order by tipoMensaje ASC").list();
            for(String men:mensaje){
                lista+=men+"=";
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }

    public String getRestriccionXTipo(String tipo) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            List<String> mensaje = session.createQuery("select mensaje FROM Mensajerestriccion where tipoMensaje='" + tipo + "'").list();
            for (String men : mensaje) {
                lista += men + "=";
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }

    public boolean añadirRestriccionYTipo(String tipo, String mensaje) throws ParseException {
        int id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Mensajerestriccion mensajerestriccion = new Mensajerestriccion(tipo, mensaje);

            id = (Integer) session.save(mensajerestriccion);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        if (id != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getAnuncios(String categoria) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where a.categoria.tipo='" + categoria + "' and a.revision='No Revisado' or a.revision='Modificado'");
            q.setMaxResults(2);
            List<Anuncio> anuncios = q.list();

            for (int i = 0; i < anuncios.size(); i++) {
                lista += anuncios.get(i).getIdAnuncio() + "|";
                lista += anuncios.get(i).getTitulo() + "|";
                lista += anuncios.get(i).getDescripcion() + "|";
                lista += anuncios.get(i).getPrecio() + "|";
                lista += anuncios.get(i).getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(anuncios.get(i).getImagen()) + "|";
                lista += anuncios.get(i).getEstado() + "|";
                lista += anuncios.get(i).getRevision() + "|";
                lista += anuncios.get(i).getUsuario().getIdUsuario() + "|";
                lista += anuncios.get(i).getUsuario().getNombre() + "|";
                lista += anuncios.get(i).getCategoria().getTipo();
                anuncios.get(i).setRevision("En Revision");
                lista += ":";
                session.update(anuncios.get(i));
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }

    public String isAdminXID(int id) {

        String tipo = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Query query = session.createQuery("FROM Usuario where idUsuario=:id");
            query.setParameter("id", id);
           
            Usuario usuario = (Usuario) query.uniqueResult();
            for(Object roles:usuario.getRoles()){
                Rol rol = (Rol) roles;
               
                if(rol.getTipoRol().equals("Administrador")){
                    tipo="Administrador";
                }
                if(rol.getTipoRol().equals("Moderador")){
                    tipo="Moderador";
                }
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return tipo;

    }
    
    
    public boolean revisarAnunciosCheck(String lista) throws ParseException {
        String [] listAnunciosCheck = lista.split("=");
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for(String id : listAnunciosCheck){
                Query query = session.createQuery("FROM Anuncio where idAnuncio=:id");
                query.setParameter("id", Integer.valueOf(id));
                Anuncio anuncio = (Anuncio) query.uniqueResult();
                if(!anuncio.getRevision().equals("Bloqueado")){
                     anuncio.setRevision("Revisado Check");
                }
                session.update(anuncio);
            }
             
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean volverEstadoAnunciosNoRevisadoAnuncios(String[] lista) throws ParseException {
        if(lista!=null){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for(String id : lista){
                if(!id.equals("")){
                Query query = session.createQuery("FROM Anuncio where idAnuncio=:id");
                query.setParameter("id", Integer.valueOf(id));
                Anuncio anuncio = (Anuncio) query.uniqueResult();
                if(!anuncio.getRevision().equals("Bloqueado")){
                    anuncio.setRevision("No Revisado");
                }
                session.update(anuncio);
                }
            }
             
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        }
         return true;
    }
    
    
    public boolean bloquearAnuncio(String lista) throws ParseException {
        String [] listaDatos = lista.split("=");
        /*idAnuncio=idPropietario=TipoRestriccion=DetalleRestriccion*/
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        int id=-1;
        try {
            tx = session.beginTransaction();
           
          
               
                Query query = session.createQuery("FROM Anuncio where idAnuncio=:id");
                query.setParameter("id", Integer.valueOf(lista.split("=")[0]));
                Anuncio anuncio = (Anuncio) query.uniqueResult();
                anuncio.setRevision("Bloqueado");
                session.update(anuncio);
                Mensajerestriccion  menMensajerestriccion = (Mensajerestriccion) session.createQuery("FROM Mensajerestriccion where tipoMensaje='" + lista.split("=")[2] + "' and mensaje='"+lista.split("=")[3]+"'").uniqueResult();
            
                Notificacion notificacion = new Notificacion(anuncio, menMensajerestriccion, anuncio.getUsuario(), false);
                
              id = (Integer)  session.save(notificacion);
               
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        if (id != 0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public boolean subirAnuncio(String anuncio) {
        int id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM Usuario where idUsuario=:id");
            query.setParameter("id", Integer.valueOf(anuncio.split("\\|")[6]));
            Usuario usuario = (Usuario) query.uniqueResult();

            
            Anuncio anuncioObject = new Anuncio();
            anuncioObject.setUsuario(usuario);
            anuncioObject.setTitulo(anuncio.split("\\|")[0]);
            anuncioObject.setDescripcion(anuncio.split("\\|")[1]);
            anuncioObject.setPrecio(BigDecimal.valueOf(Double.valueOf(anuncio.split("\\|")[2])));
            anuncioObject.setDireccion(anuncio.split("\\|")[3]);
            anuncioObject.setImagen(Base64.getDecoder().decode(anuncio.split("\\|")[4]));
            anuncioObject.setEstado(anuncio.split("\\|")[5]);
            anuncioObject.setRevision("No Revisado");

            Query query1 = session.createQuery("FROM Categoria where Tipo=:tipo");
            query1.setParameter("tipo", anuncio.split("\\|")[7]);
            Categoria categoria = (Categoria) query1.uniqueResult();
            anuncioObject.setCategoria(categoria);
            id = (Integer) session.save(anuncioObject);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        if (id != 0) {
            return true;
        } else {
            return false;
        }

    }
    
    public String getAnunciosAppAndroid(String categoria, int id) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where a.categoria.tipo='" + categoria + "' and a.revision!='Bloqueado' and a.revision!='Comprado' and a.usuario.id!='" + id + "'");
           List<Anuncio> anuncios = q.list();

            for (int i = 0; i < anuncios.size(); i++) {
                lista += anuncios.get(i).getIdAnuncio() + "|";
                lista += anuncios.get(i).getTitulo() + "|";
                lista += anuncios.get(i).getDescripcion() + "|";
                lista += anuncios.get(i).getPrecio() + "|";
                lista += anuncios.get(i).getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(anuncios.get(i).getImagen()) + "|";
                lista += anuncios.get(i).getEstado() + "|";
                lista += anuncios.get(i).getRevision() + "|";
                lista += anuncios.get(i).getUsuario().getIdUsuario() + "|";
                lista += anuncios.get(i).getUsuario().getNombre() + "|";
                lista += anuncios.get(i).getCategoria().getTipo();
                lista += ":";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }
    
    public String getMisAnunciosAppAndroid(int id){
    String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where a.usuario.id='" + id + "' and a.revision!='Comprado'");
           List<Anuncio> anuncios = q.list();

            for (int i = 0; i < anuncios.size(); i++) {
                
                lista += anuncios.get(i).getIdAnuncio() + "|";
                lista += anuncios.get(i).getTitulo() + "|";
                lista += anuncios.get(i).getDescripcion() + "|";
                lista += anuncios.get(i).getPrecio() + "|";
                lista += anuncios.get(i).getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(anuncios.get(i).getImagen()) + "|";
                lista += anuncios.get(i).getEstado() + "|";
                lista += anuncios.get(i).getRevision() + "|";
                lista += anuncios.get(i).getUsuario().getIdUsuario() + "|";
                lista += anuncios.get(i).getUsuario().getNombre() + "|";
                lista += anuncios.get(i).getCategoria().getTipo();
                lista += ":";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }
    
    public String getMisAnunciosBloqueados(int id){
    String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where a.usuario.id='" + id + "' and a.revision='Bloqueado'");
            List<Anuncio> anuncios = q.list();
            for (int i = 0; i < anuncios.size(); i++) {
                lista += anuncios.get(i).getIdAnuncio() + "|";
                lista += anuncios.get(i).getTitulo() + "|";
                lista += anuncios.get(i).getDescripcion() + "|";
                lista += anuncios.get(i).getPrecio() + "|";
                lista += anuncios.get(i).getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(anuncios.get(i).getImagen()) + "|";
                lista += anuncios.get(i).getEstado() + "|";
                lista += anuncios.get(i).getCategoria().getTipo()+ "|";
                Query q2 = session.createQuery("FROM Notificacion n WHERE  n.usuario.id= :idUsuario and n.anuncio.idAnuncio = :idAnuncio");
                q2.setParameter("idAnuncio", anuncios.get(i).getIdAnuncio());
                q2.setParameter("idUsuario", id);
                Notificacion notificacion = (Notificacion) q2.uniqueResult();
                notificacion.setRevisado(true);
                lista += notificacion.getMensajerestriccion().getTipoMensaje() + "|";
                lista += notificacion.getMensajerestriccion().getMensaje();
                lista += ":";
                session.save(notificacion);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }
    
    
    public String getMisVentas(int id){
    String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where a.usuario.id='" + id + "' and a.revision='Comprado'");
           List<Anuncio> anuncios = q.list();

            for (int i = 0; i < anuncios.size(); i++) {
                lista += anuncios.get(i).getIdAnuncio() + "|";
                lista += anuncios.get(i).getTitulo() + "|";
                lista += anuncios.get(i).getDescripcion() + "|";
                lista += anuncios.get(i).getPrecio() + "|";
                lista += anuncios.get(i).getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(anuncios.get(i).getImagen()) + "|";
                lista += anuncios.get(i).getEstado() + "|";
                lista += anuncios.get(i).getRevision() + "|";
                lista += anuncios.get(i).getUsuario().getIdUsuario() + "|";
                lista += anuncios.get(i).getUsuario().getNombre() + "|";
                lista += anuncios.get(i).getCategoria().getTipo();
                lista += ":";
            }
            
            Query q1 = session.createQuery("FROM Compra c WHERE c.anuncio.usuario.id='" + id + "'");
           List<Compra> compras = q1.list();
           for(Compra compra : compras){
               compra.setRevisado(true);
               session.save(compra);
           }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;

    }
    
    public String getMisFavoritos(int id){
    String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Favoritos f where f.usuario.id='" + id + "'");
           List<Favoritos> favoritos = q.list();

            for (int i = 0; i < favoritos.size(); i++) {
                lista += favoritos.get(i).getAnuncio().getIdAnuncio() + "|";
                lista += favoritos.get(i).getAnuncio().getTitulo() + "|";
                lista += favoritos.get(i).getAnuncio().getDescripcion() + "|";
                lista += favoritos.get(i).getAnuncio().getPrecio() + "|";
                lista += favoritos.get(i).getAnuncio().getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(favoritos.get(i).getAnuncio().getImagen()) + "|";
                lista += favoritos.get(i).getAnuncio().getEstado() + "|";
                lista += favoritos.get(i).getAnuncio().getRevision() + "|";
                lista += favoritos.get(i).getAnuncio().getUsuario().getIdUsuario() + "|";
                lista += favoritos.get(i).getAnuncio().getUsuario().getNombre() + "|";
                lista += favoritos.get(i).getAnuncio().getCategoria().getTipo();
                lista += ":";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;

    }

    public boolean comprarAnuncio(int idAnuncio, int idUsuario, String fecha, String precio) throws ParseException {
        int id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where idAnuncio='" + idAnuncio + "'");
            Anuncio anuncio = (Anuncio) q.uniqueResult();
            anuncio.setRevision("Comprado");

            Query q1 = session.createQuery("FROM Usuario a where idUsuario='" + idUsuario + "'");
            Usuario usuario = (Usuario) q1.uniqueResult();

            Compra compra = new Compra(anuncio, usuario, new SimpleDateFormat("dd/MM/yyyy").parse(fecha), new BigDecimal(precio), true, false);

            Query q2 = session.createQuery("DELETE FROM Oferta o WHERE o.anuncio.idAnuncio = :idAnuncio");
            q2.setParameter("idAnuncio", anuncio.getIdAnuncio());
            q2.executeUpdate();
            id = (Integer) session.save(compra);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        if (id != 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean pagoAnuncio(int idAnuncio) throws ParseException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Compra a where a.anuncio='" + idAnuncio + "'");
            Compra compra = (Compra) q.uniqueResult();
            compra.setPagado(true);
            session.save(compra);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    

    public boolean ofertaAnuncio(int idAnuncio, int idUsuario, String precio) throws ParseException{
        int id=0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Anuncio a where idAnuncio='" + idAnuncio + "'");
           Anuncio anuncio = (Anuncio) q.uniqueResult();
            
           Query q1 = session.createQuery("FROM Usuario a where idUsuario='" + idUsuario + "'");
           Usuario usuario = (Usuario) q1.uniqueResult();
            
           Oferta oferta = new Oferta(anuncio, usuario, new BigDecimal(precio), false);
           
           id = (Integer)session.save(oferta);
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        if (id != 0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    
        public String getMisComprasAppAndroid(int id) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Usuario u where u.id = :id");
            q.setParameter("id", id);
            Usuario usuario = (Usuario) q.uniqueResult();
            Set<Compra> compras = usuario.getCompras();
            for (Compra compra : compras) {
                if(compra.isPagado()){
                lista += compra.getAnuncio().getIdAnuncio() + "|";
                lista += compra.getAnuncio().getTitulo() + "|";
                lista += compra.getAnuncio().getDescripcion() + "|";
                lista += compra.getAnuncio().getPrecio() + "|";
                lista += compra.getAnuncio().getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(compra.getAnuncio().getImagen()) + "|";
                lista += compra.getAnuncio().getEstado() + "|";
                lista += compra.getAnuncio().getRevision() + "|";
                lista += compra.getAnuncio().getUsuario().getIdUsuario() + "|";
                lista += compra.getAnuncio().getUsuario().getNombre() + "|";
                lista += compra.getAnuncio().getCategoria().getTipo() + "|";
                lista += "Pagado="+ compra.getPrecioFinal()+ "|";
                lista += ":";
                }
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }
        
        public String getMisComprasPendientesAppAndroid(int id) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Usuario u where u.id = :id");
            q.setParameter("id", id);
            Usuario usuario = (Usuario) q.uniqueResult();
            Set<Compra> compras = usuario.getCompras();
            for (Compra compra : compras) {
                if(!compra.isPagado()){
                lista += compra.getAnuncio().getIdAnuncio() + "|";
                lista += compra.getAnuncio().getTitulo() + "|";
                lista += compra.getAnuncio().getDescripcion() + "|";
                lista += compra.getAnuncio().getPrecio() + "|";
                lista += compra.getAnuncio().getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(compra.getAnuncio().getImagen()) + "|";
                lista += compra.getAnuncio().getEstado() + "|";
                lista += compra.getAnuncio().getRevision() + "|";
                lista += compra.getAnuncio().getUsuario().getIdUsuario() + "|";
                lista += compra.getAnuncio().getUsuario().getNombre() + "|";
                lista += compra.getAnuncio().getCategoria().getTipo()+ "|";
                lista += "FaltaPago="+ compra.getPrecioFinal()+ "|";
                lista += ":";
                }
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }

        public String getMisNotificacionesOffline(int id) {
        String lista = "";
        boolean notificacionVenta=false;
        boolean notificacionOferta=false;
        boolean notificacionRestriccion=false;
        boolean notificacionChat=false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Compra c where c.anuncio.usuario.id = :id");
            q.setParameter("id", id);
            List<Compra> compras =  q.list();
            for (Compra compra : compras) {
                if(!compra.isRevisado()){
                    notificacionVenta=true;
                }
            }
            if(notificacionVenta){
                lista+="Articulos Vendidos=";
            }
            
            Query q1 = session.createQuery("FROM Oferta o where o.anuncio.usuario.id = :id");
            q1.setParameter("id", id);
            List<Oferta> ofertas =  q1.list();
            for (Oferta oferta : ofertas) {
                if(!oferta.isRevisado()){
                    notificacionOferta=true;
                }
            }
            if(notificacionOferta){
                lista+="Oferta=";
            }
            Query q2 = session.createQuery("FROM Notificacion n where n.usuario.id = :id and n.revisado=0");
            q2.setParameter("id", id);
            List<Notificacion> notificaciones =  q2.list();
            for (Notificacion notificacion : notificaciones) {
                if(!notificacion.isRevisado()){
                    notificacionRestriccion=true;
                }
            }
            if(notificacionRestriccion){
                lista+="Bloqueo de anuncios=";
            }
            
            //
            Query q3 = session.createQuery("FROM Chat");
         
            List<Chat> chats =  q3.list();
            for (Chat chat : chats) {
                if(chat.getUsuarioByIdPropietario().getIdUsuario()==id){
                    if(!chat.isRevisado_Propietario()){
                        notificacionChat=true;
                        break;
                    }
                }if(chat.getUsuarioByIdUuarioInteresado().getIdUsuario()==id){
                    if(!chat.isRevisado_Interesado()){
                        notificacionChat=true;
                        break;
                    }
                }
            }
            if(notificacionChat){
                lista+="chat=";
            }
            
            if(lista.equals("")){
            lista+="vacia";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }
        
    public String getMisOfertasEnviadas(int id) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Usuario u where u.id = :id");
            q.setParameter("id", id);
            Usuario usuario = (Usuario) q.uniqueResult();
            Set<Oferta> ofertas = usuario.getOfertas();
            for (Oferta oferta : ofertas) {
                lista += oferta.getAnuncio().getIdAnuncio() + "|";
                lista += oferta.getAnuncio().getTitulo() + "|";
                lista += oferta.getAnuncio().getUsuario().getNombre() + "|";
                lista += oferta.getPrecioOferta() + "|";
                lista += oferta.getAnuncio().getPrecio() + "|";
                lista += Base64.getEncoder().encodeToString(oferta.getAnuncio().getImagen()) + "|";
                lista += ":";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }
    
    
    public String getMisOfertasRecibidas(int id) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //Query q = session.createQuery("FROM Usuario u where u.id = :id ");
            Query q = session.createQuery("FROM Oferta o where o.anuncio.usuario.id = :id ORDER BY o DESC");
            q.setParameter("id", id);
             List<Oferta> ofertas =  q.list();
            for (Oferta oferta : ofertas) {
                lista += oferta.getIdOferta() + "|";
                lista += oferta.getAnuncio().getTitulo() + "|";
                lista += oferta.getUsuario().getNombre() + "|";
                lista += oferta.getPrecioOferta() + "|";
                lista += oferta.getAnuncio().getPrecio() + "|";
                lista += Base64.getEncoder().encodeToString(oferta.getAnuncio().getImagen()) + "|";
                lista += ":";
                oferta.setRevisado(true);
                session.save(oferta);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }
    
    
    public boolean denegarOferta(int idOferta) throws ParseException{
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           Query q = session.createQuery("DELETE FROM Oferta a WHERE a.idOferta = :idOferta");
            q.setParameter("idOferta", idOferta);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    
    
    //aceptarOferta
    public String aceptarOferta(int idOferta, String fecha) throws ParseException{
        String resuelto="";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           Query q = session.createQuery("FROM Oferta a WHERE a.idOferta = :idOferta");
            q.setParameter("idOferta", idOferta);
             Oferta oferta = (Oferta) q.uniqueResult();
             oferta.getAnuncio().setRevision("Comprado");
             resuelto = oferta.getUsuario().getIdUsuario()+"="+oferta.getAnuncio().getTitulo();
             Compra compra = new Compra(oferta.getAnuncio(), oferta.getUsuario() , new SimpleDateFormat("dd/MM/yyyy").parse(fecha), oferta.getPrecioOferta(), false, false);
             
            Query q1 = session.createQuery("DELETE FROM Oferta o WHERE o.anuncio.idAnuncio = :idAnuncio");
            q1.setParameter("idAnuncio", oferta.getAnuncio().getIdAnuncio());
            q1.executeUpdate();
            session.save(compra);
             
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return resuelto;
    }
    
    
    public String getAnunciosFiltro(String entrada){
    String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        String id = entrada.split("\\|")[0];
        String nombre = entrada.split("\\|")[1];
        BigDecimal min = new BigDecimal(entrada.split("\\|")[2]);
        BigDecimal max = new BigDecimal(entrada.split("\\|")[3]);
        boolean ascendente = Boolean.valueOf(entrada.split("\\|")[4]);
        String categoria = entrada.split("\\|")[5];
        List<String> listaUbicaciones = null;
       if(!entrada.split("\\|")[6].equals("vacía")){
           listaUbicaciones = new ArrayList<>();
          for (int i = 6; i < entrada.split("\\|").length; i++) {
            listaUbicaciones.add(entrada.split("\\|")[i]);
            }
       }
       
        
        if(listaUbicaciones==null){
            //System.out.println("Ubicaciones vacías");
        }else{
            for(String ubi : listaUbicaciones){
                //System.out.println("Ubicacion: "+ubi);
            }
        }
        try {
            tx = session.beginTransaction();
            String command="FROM Anuncio a WHERE a.titulo LIKE '%"+nombre+"%'";
            if (min.compareTo(BigDecimal.ZERO) != 0 && max.compareTo(BigDecimal.ZERO) != 0) {
                command += " and a.precio <= " + max + " and a.precio >= " + min;
            }
            command += " and a.categoria.tipo='" + categoria + "'";
            if(listaUbicaciones !=null){
                command+=" and  a.direccion IN (:direcciones)";
            }
            command+=" and a.usuario.id!='" + id + "' and a.revision!='Bloqueado'";
            if(ascendente){
                command+=" ORDER BY a.precio ASC";
            }else command+=" ORDER BY a.precio DESC";
            
            Query q = session.createQuery(command);
             if(listaUbicaciones !=null){
             q.setParameterList("direcciones", listaUbicaciones);
            }
            List<Anuncio> anuncios = q.list();
            

            for (int i = 0; i < anuncios.size(); i++) {
                
                lista += anuncios.get(i).getIdAnuncio() + "|";
                lista += anuncios.get(i).getTitulo() + "|";
                lista += anuncios.get(i).getDescripcion() + "|";
                lista += anuncios.get(i).getPrecio() + "|";
                lista += anuncios.get(i).getDireccion() + "|";
                lista += Base64.getEncoder().encodeToString(anuncios.get(i).getImagen()) + "|";
                lista += anuncios.get(i).getEstado() + "|";
                lista += anuncios.get(i).getRevision() + "|";
                lista += anuncios.get(i).getUsuario().getIdUsuario() + "|";
                lista += anuncios.get(i).getUsuario().getNombre() + "|";
                lista += anuncios.get(i).getCategoria().getTipo();
                lista += ":";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return lista;
    }
    
    
    public boolean eliminarAnuncio(int idAnuncio) throws ParseException{
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           Query q = session.createQuery("DELETE FROM Anuncio a WHERE a.idAnuncio = :idAnuncio");
            q.setParameter("idAnuncio", idAnuncio);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean añadirFavorito(int idAnuncio, int idUsuario) throws ParseException{
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Usuario u where u.id = :id");
            q.setParameter("id", idUsuario);
            Usuario usuario = (Usuario) q.uniqueResult();
            
            Query q1 = session.createQuery("FROM Anuncio a WHERE a.idAnuncio = :idAnuncio");
            q1.setParameter("idAnuncio", idAnuncio);
            Anuncio anuncio = (Anuncio) q1.uniqueResult();
            
            
            Query q2 = session.createQuery("FROM Favoritos f WHERE  f.usuario.id= :idUsuario and f.anuncio.idAnuncio = :idAnuncio");
            q2.setParameter("idAnuncio", idAnuncio);
            q2.setParameter("idUsuario", idUsuario);
            if(q2.uniqueResult() == null){
              Favoritos favorito = new Favoritos(anuncio, usuario);
            session.save(favorito);
            }
            
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean eliminarFavorito(int idAnuncio, int idUsuario) throws ParseException{
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           Query q = session.createQuery("DELETE FROM Favoritos f WHERE  f.usuario.id= :idUsuario and f.anuncio.idAnuncio = :idAnuncio");
           q.setParameter("idUsuario", idUsuario);
            q.setParameter("idAnuncio", idAnuncio);
            q.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean modificarAnuncio(String anuncioC) throws ParseException {
       
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
                Query query = session.createQuery("FROM Anuncio where idAnuncio=:id");
                query.setParameter("id", Integer.valueOf(anuncioC.split("\\|")[0]));
                Anuncio anuncio = (Anuncio) query.uniqueResult();
                anuncio.setTitulo(anuncioC.split("\\|")[1]);
                anuncio.setDescripcion(anuncioC.split("\\|")[2]);
                anuncio.setPrecio(new BigDecimal(anuncioC.split("\\|")[3]));
                anuncio.setDireccion(anuncioC.split("\\|")[4]);
                anuncio.setImagen(Base64.getDecoder().decode(anuncioC.split("\\|")[5]));
                anuncio.setEstado(anuncioC.split("\\|")[6]);
                Query query1 = session.createQuery("FROM Categoria where Tipo=:tipo");
                query1.setParameter("tipo", anuncioC.split("\\|")[8]);
                Categoria categoria = (Categoria) query1.uniqueResult();
                anuncio.setCategoria(categoria);
                anuncio.setRevision("No Revisado");
                
                 Query q2 = session.createQuery("DELETE FROM Notificacion n WHERE n.anuncio.idAnuncio = :idAnuncio");
                
                q2.setParameter("idAnuncio", anuncio.getIdAnuncio());
                q2.executeUpdate();
                session.update(anuncio);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
         return true;
    }
    
    public boolean valorarUsuario(int idUsuario, int rating) throws ParseException{
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           
            Query query = session.createQuery("FROM Usuario where idUsuario=:id");
            query.setParameter("id", idUsuario);
           
            Usuario usuario = (Usuario) query.uniqueResult();
            Valoracion valoracion = new Valoracion(usuario, rating);
            session.save(valoracion);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
    
    
    public String getMisChat(int idUsuario) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Chat ch where ch.usuarioByIdUuarioInteresado.idUsuario = :id OR ch.usuarioByIdPropietario.idUsuario = :id1");
            q.setParameter("id", idUsuario);
             q.setParameter("id1", idUsuario);
            List<Chat> chats = q.list();
           for(Chat chat : chats){
               lista+=chat.getAnuncio().getIdAnuncio() +"|";
               lista+=chat.getAnuncio().getTitulo()+"|";
               lista+=chat.getAnuncio().getUsuario().getNombre()+"|";
               lista+=chat.getIdChat()+"|";
               lista+=chat.getAnuncio().getUsuario().getIdUsuario()+"|";
               lista+=Base64.getEncoder().encodeToString(chat.getAnuncio().getImagen())+"|";
               lista+=chat.getUsuarioByIdUuarioInteresado().getIdUsuario()+"|";
               
               
               if(chat.getAnuncio().getUsuario().getIdUsuario()==idUsuario){
                   if(chat.isRevisado_Propietario()){
                       lista+="Si";
                   }else{
                       lista+="No";
                   }
                   chat.setRevisado_Propietario(true);
               }else {
                    if(chat.isRevisado_Interesado()){
                       lista+="Si";
                   }else{
                       lista+="No";
                   }
                   chat.setRevisado_Interesado(true);
               }
               lista+=":";
           }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }
    
    
    
     public String getMensajesChat(int idChat) {
        String lista = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("FROM Chat ch where ch.idChat = :id");
            q.setParameter("id", idChat);
            Chat chat = (Chat) q.uniqueResult();
            Set<Mensaje> mensajes  = chat.getMensajes();
           
            for(Mensaje mensaje : mensajes){
                lista += mensaje.getIdMensaje()+"|";
                lista += mensaje.getChat().getIdChat()+"|";
                lista += mensaje.getUsuarioByIdRemitente().getIdUsuario()+"|";
                lista += mensaje.getUsuarioByIdDestinatario().getIdUsuario() +"|";
                lista+=mensaje.getMensaje();
                lista+=":";
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }
     
     public boolean addMensajeChat(int idChat, int idRemitente, int idDestinatario, String mensajeText) throws ParseException{
         int id=0;
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           
             Query q = session.createQuery("FROM Chat ch where ch.idChat = :id");
            q.setParameter("id", idChat);
            Chat chat = (Chat) q.uniqueResult();
           
            Query query = session.createQuery("FROM Usuario where idUsuario=:id");
            query.setParameter("id", idRemitente);
            Usuario usuarioRemitente = (Usuario) query.uniqueResult();
            
            Query query1 = session.createQuery("FROM Usuario where idUsuario=:id");
            query1.setParameter("id", idDestinatario);
            Usuario usuarioDestinatario = (Usuario) query1.uniqueResult();
            
            Mensaje mensaje = new Mensaje(chat, usuarioRemitente, usuarioDestinatario, mensajeText, Calendar.getInstance().getTime());
             id = (Integer)session.save(mensaje);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
       if (id != 0) {
            return true;
        } else {
            return false;
        }
    }
     
     public Integer crearChat(int idAnuncio, int idInteresado) throws ParseException{
         int id=0;
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            Query q2 = session.createQuery("FROM Chat ch where ch.usuarioByIdUuarioInteresado.idUsuario = :id and ch.anuncio.idAnuncio = :idAnuncio");
            q2.setParameter("id", idInteresado);
            q2.setParameter("idAnuncio", idAnuncio);
            
            if(q2.uniqueResult() == null){
             Query q = session.createQuery("FROM Anuncio a WHERE a.idAnuncio = :idAnuncio");
            q.setParameter("idAnuncio", idAnuncio);
            Anuncio anuncio = (Anuncio) q.uniqueResult();
            
            Query query = session.createQuery("FROM Usuario where idUsuario=:id");
            query.setParameter("id", idInteresado);
            Usuario usuarioRemitente = (Usuario) query.uniqueResult();
            
            Chat chat = new Chat(anuncio, usuarioRemitente,  anuncio.getUsuario(), Calendar.getInstance().getTime());
            chat.setRevisado_Interesado(true);
            id = (Integer)session.save(chat);
            }else{
            Chat chatExiste = (Chat) q2.uniqueResult();
            id=chatExiste.getIdChat();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return id;
    }
     
     
     public boolean mensajeNoleido(int idChat, int idDestinatario) throws ParseException{
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           Query q = session.createQuery("FROM Chat ch where ch.idChat = :id");
            q.setParameter("id", idChat);
            Chat chat = (Chat) q.uniqueResult();
            if(chat.getAnuncio().getUsuario().getIdUsuario()==idDestinatario){
                   chat.setRevisado_Propietario(false);
               }else {
                   chat.setRevisado_Interesado(false);
               }
            session.save(chat);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return true;
    }
     
        public static void main(String[] args) throws ParseException {
        }

}
