/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import controller.MainModeradorController;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import view.Cliente;
import view.Selling4Us;

/**
 *
 * @author Antonio David
 */
public class ComunicationManager {
    private Coleccion buffered;
    private MainModeradorController mainModerador;

    public ComunicationManager(PrintWriter out, BufferedReader in) throws IOException {
        mainModerador = new MainModeradorController();
        buffered = new Coleccion(out, in);
        
    }

    public void setMainModerador(MainModeradorController aThis) {
        this.mainModerador = aThis;
    }

    public void enviarMensaje(String mensaje) throws InterruptedException{
        buffered.añadoMensajeBufferSalida(mensaje);
    }
    
    public String leerMensaje() throws InterruptedException{
        return buffered.retiroMensajeEntrada();
    }
    
    public void detenerHilos(){
        buffered.detenerHilos();
    }

    class Envio extends Thread {

        private PrintWriter out;
       
        public Envio(PrintWriter out) throws IOException {
            this.out = out;
        }

        @Override
        public synchronized void run() {
            try {
                while(true){
                    synchronized (this) {
                         wait();
                    }
                    out.println(buffered.retiroMensajeBufferSalida());
                }
                
            } catch (InterruptedException ex) {
                 Thread.currentThread().interrupt();
            }
        }
    }

    class Recibo extends Thread {
        private BufferedReader in;
      
        public Recibo(BufferedReader in) throws IOException {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (!inputLine.split(":")[1].equals("NOTIFICACION")) {
                        buffered.añadoMensajeBufferEntrada(inputLine);
                    } else {
                        System.out.println("NOTIFICACION NUEVO ANUNCIO");
                       
                        String[] listaAnuncios = inputLine.split(":");
                        
                        String[] newArray = new String[listaAnuncios.length - 2];
                        System.arraycopy(listaAnuncios, 2, newArray, 0, newArray.length);

                        if (newArray.length != 0) {
                            listaAnuncios = newArray;
                             for (int i=0; i<listaAnuncios.length; i++){
                                 mainModerador.añadirAnuncio(listaAnuncios[i]);
                             }
                        } else {
                            listaAnuncios[0] = "";
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Recibo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                 Thread.currentThread().interrupt();
            }
        }
    }
    
    class Coleccion {
        List<String> entrada = new LinkedList<>();
        List<String> salida = new LinkedList<>();
        Recibo hiloEntrada;
        Envio hiloSalida ;

        public Coleccion(PrintWriter out, BufferedReader in) throws IOException {
            hiloEntrada = new Recibo(in);
            hiloSalida = new Envio(out);
            hiloEntrada.start();
            hiloSalida.start();
        }

        
        public synchronized void añadoMensajeBufferSalida(String mensaje) throws InterruptedException {
            salida.add(mensaje);
            synchronized (hiloSalida) {
                hiloSalida.notify();
            }
        }
    
        public synchronized String retiroMensajeBufferSalida() throws InterruptedException {
            String cadena = salida.get(salida.size() - 1);
            salida.remove(salida.size() - 1);
            return cadena;
        }
        
        public synchronized void añadoMensajeBufferEntrada(String mensaje) throws InterruptedException {
                entrada.add(mensaje);
                notify();
        }
    
        public synchronized String retiroMensajeEntrada() throws InterruptedException {
            if(entrada.isEmpty()){
                wait();
            }
            String cadena = entrada.get(0);
            entrada.remove(0);
            return cadena;
        }
        
        public void detenerHilos() {
            if (hiloEntrada.isAlive()) {
                 hiloEntrada.interrupt();
            }
            if(hiloSalida.isAlive()){
                hiloSalida.interrupt();
            }
        }
    }
}
