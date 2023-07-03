/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernateModel;

import Utils.HibernateUtil;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.SHA;

/**
 *
 * @author Antonio David
 */
public class Main {
    public static void main(String[] args) {
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
   
        try{
            tx = session.beginTransaction();
          
          
            Usuario usuario = new Usuario("lola", "lola", new java.util.Date("30/07/1998"), "chiclana de la frontera");
            
            
            
            
            
            
            session.save(usuario);
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) tx.rollback();
                e.printStackTrace();
        }finally {
            session.close();
        } 
        
        
    }
    
}
