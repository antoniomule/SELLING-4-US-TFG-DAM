/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package controller;

import Utils.SHA;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import view.Cliente;
import static view.Cliente.CategoriaModerador;
import static view.Cliente.IDusuarioClient;
import static view.Cliente.NombreusuarioClient;
import static view.Cliente.tipoUsuario;
import view.Selling4Us;

/**
 * FXML Controller class, en el que implemento los manejadores del boton login, y boton cerrar
 *
 * @author antoniodavid
 */
public class LoginController implements Initializable {

    
    @FXML
    private TextField FieldUsuario;
    @FXML
    private PasswordField FieldPassword;
    @FXML
    private FontAwesomeIconView InClose;
    
    @FXML
    private Label EtiquetaVerificar;
    @FXML
    private Label EtiquetaUsuario;
    @FXML
    private Label EtiquetaContraseña;
    
    @FXML
    private MFXButton idButtonLogin;
    
    static Selling4Us app;
    static Cliente client;
    @FXML
    private ImageView idImageLoad;
    
    public void setApp(Selling4Us app) {
        this.app = app;
    }
    
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //idImageLoad.setVisible(false);
    }    

    
    
    @FXML
    private void handelClose(MouseEvent event) {
        if(event.getSource()==InClose){
            System.exit(0);
        }
    }

    @FXML
    private void HandleOnMuseClickedLoginNew(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
        FieldUsuario.setStyle("-fx-border-color: black;");
        FieldPassword.setStyle("-fx-border-color: black;");
        
        if(FieldUsuario.getText().trim().equals("")){
            if(FieldPassword.getText().trim().equals("")){
                FieldUsuario.setStyle("-fx-border-color: red;");
                FieldPassword.setStyle("-fx-border-color: red;");
            EtiquetaVerificar.setText("*Introduce los valores seleccionados");
            }else {
            FieldUsuario.setStyle("-fx-border-color: red;");
            EtiquetaVerificar.setText("*No se ha introducido Usuario.");}
        }else if(FieldPassword.getText().trim().equals("")){
            FieldPassword.setStyle("-fx-border-color: red;");
            EtiquetaVerificar.setText("*No se ha introducido contraseña");
        }else{
   
            
             Task<Boolean> task = new Task<Boolean>() {
                 boolean existe=false;
                 @Override
                 protected Boolean call() throws Exception {
                     existe=peticionLogin(FieldUsuario.getText(), FieldPassword.getText());
                 return existe;    
                 }
             };
             
             
            task.setOnSucceeded(eventTask -> {
            boolean existe = task.getValue();
                if (existe) {
                idImageLoad.setVisible(true);
                Parent root = null;
                if (client.tipoUsuario.equals("Administrador")) {
                    try {
                       
                        root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
                        
                    } catch (IOException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    try {
                       
                        root = FXMLLoader.load(getClass().getResource("/view/MainModerador.fxml"));
                         
                    } catch (IOException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                FieldUsuario.setStyle("-fx-border-color: black;");
                FieldPassword.setStyle("-fx-border-color: black;");

                app.changeScene(root);
            } else {
                EtiquetaVerificar.setText("Usuario o contraseña incorrectos");
            }
            });
            Thread thread = new Thread(task);
            thread.start();
        }
    }
    
    
     
    

    
    public void conectar() throws IOException, InterruptedException {
         client = app.getCliente();
        client.getComunicationManager().enviarMensaje(client.UserCon + "CONEXION:peticion:" + null);
        if(client.getComunicationManager().leerMensaje().split(":")[2].equals("ComenzamosConexion")){
            System.out.println("ConexionExtablecidaCorrecatmente");
        }
    }

    public boolean peticionLogin(String usuario, String contraseña) throws IOException, ClassNotFoundException, InterruptedException {
         client.getComunicationManager().enviarMensaje(client.UserCon + "USUARIO:Login:" + usuario + "=" + SHA.generate512(contraseña));
       
        boolean sino = false;
        String entrada =  client.getComunicationManager().leerMensaje();
            if(entrada.split(":")[1].equals("Correcto")){
              
                String[] lista =  client.getComunicationManager().leerMensaje().split(":")[2].split("=");
                IDusuarioClient = (Integer.parseInt(lista[0]));
                NombreusuarioClient = (lista[1]);
                tipoUsuario = lista[2];
                if (tipoUsuario.equals("Moderador")) {
                    CategoriaModerador = lista[3];
                }
                sino = true;
            }
        return sino;
    }
    
    
}
