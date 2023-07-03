/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import DAO.Selling4UsDAO;
import java.util.logging.Level;
import java.util.logging.Logger;
import selling4usserver.Servidor;

/**
 *
 * @author Antonio David
 */
public class AnunciosScheduler extends Thread {

        private String categoria = null;
        private Selling4UsDAO selling4UsDAO = new Selling4UsDAO();
        private Servidor.HiloCliente hiloCliente;
        private boolean running=true;
        
        public AnunciosScheduler(Servidor.HiloCliente hiloCliente){
        this.hiloCliente=hiloCliente;
        }
        
    public void parar() {
        running = false;
    }
    public void reanudar(){
        running = true;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(running){
                    String getAnuncios = null;
                    synchronized (this) {
                        getAnuncios = selling4UsDAO.getAnuncios(categoria);
                        if (!getAnuncios.equals("")) {
                            hiloCliente.añadirAnuncioCola(categoria, getAnuncios);
                            hiloCliente.avisoNuevoAnuncio(categoria);
                        }else{
                        }
                    }
                }
            }
            System.out.println("El hilo scheduler de la categoria: "+categoria+" se detiene");
        }
    }
