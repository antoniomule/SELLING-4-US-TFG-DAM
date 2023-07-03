package antonio.david.selling4usandroid.Utils;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import antonio.david.selling4usandroid.MainActivity;

/**
 *
 * @author Antonio David
 */
public class ComunicationManager {
    private static Coleccion buffered;
    private static Socket socket;
    private static String ip;
    private static String port;

    private static MainActivity mainActivity;


    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public ComunicationManager(String ip, String port) throws IOException {
        this.ip=ip;
        this.port=port;
    }


    public ComunicationManager() throws IOException {
        this.ip=ip;
        this.port=port;
    }
    public void conectar() {
        new ConnectTask().execute();
    }
    private class ConnectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket(ip, Integer.parseInt(port));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                buffered = new Coleccion((new PrintWriter(socket.getOutputStream(), true)), new BufferedReader(new InputStreamReader(socket.getInputStream())));
                } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    class Envio extends Thread{

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
                        System.out.println("NOTIFICACION");
                        String finalInputLine = inputLine;
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mainActivity.mostrarNotificacion(finalInputLine.split(":")[2], finalInputLine.split(":")[3]);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Recibo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                 Thread.currentThread().interrupt();
            }
        }
    }
    
    class Coleccion{
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
