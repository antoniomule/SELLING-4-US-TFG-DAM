package view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


/**
 *
 * @author Antonio David
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import Utils.ComunicationManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
//-------------
import java.io.*;
import java.util.Properties;
import javafx.scene.control.Alert;

//---------

/**
 *
 * @author Antonio David
 */
public class Cliente{

   
    //----------------------//
    private  Socket echoSocket = null;
    private   PrintWriter out = null;
    private  BufferedReader in = null;
    public String UserCon = "CL:";
    private int Port;
     //-----------------//
    
    public static int IDusuarioClient;
    public static String NombreusuarioClient;
    public static String tipoUsuario;
    public static String CategoriaModerador;
    public static String listaEnviarCierre="";
    
    
    //------------------//
    private ComunicationManager comunicationManager;
    //------------------//

    public ComunicationManager getComunicationManager() {
        return comunicationManager;
    }

    public void setEncargadoComunicacion(ComunicationManager comunicationManager) {
        this.comunicationManager = comunicationManager;
    }
   
    
    public void start() throws Exception {

        String hostSever = "";
        try {
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream("config.init"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                System.out.println("Error IOException. " + ex);
            }

            hostSever = prop.getProperty("host");
            Port = Integer.parseInt(prop.getProperty("puerto"));
            echoSocket = new Socket(hostSever, Port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            comunicationManager = new ComunicationManager(out, in);

        } catch (UnknownHostException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR CONEXION");
            alert.setHeaderText("Host Desconocido");
            alert.showAndWait();
            System.exit(0);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR CONEXION");
            alert.setHeaderText("No ha sido posbile conectar con el servidor, informe al administrador");
            alert.showAndWait();
            System.exit(0);
        }
    }

    
    public void atras(String lista) throws IOException, ClassNotFoundException, InterruptedException{
        Cliente.listaEnviarCierre="";
        if(lista != null){
            comunicationManager.enviarMensaje(UserCon+ "USUARIO:atras:"+IDusuarioClient+"|"+lista);
            System.out.println(UserCon+ "USUARIO:atras:"+IDusuarioClient+"|"+lista);
        }else
            comunicationManager.enviarMensaje(UserCon+ "USUARIO:atras:"+IDusuarioClient+"|"+null);
    }
    
    public void cierro() throws IOException, ClassNotFoundException, InterruptedException{
       if(listaEnviarCierre != null){
           System.out.println(UserCon+ "USUARIO:CerrarConexion:"+IDusuarioClient+"|"+listaEnviarCierre);
           comunicationManager.enviarMensaje(UserCon+ "USUARIO:CerrarConexion:"+IDusuarioClient+"|"+listaEnviarCierre);
       }else comunicationManager.enviarMensaje(UserCon+ "USUARIO:CerrarConexion:"+IDusuarioClient+"|"+null);
       comunicationManager.detenerHilos();
       System.exit(0);
    }
}
    

