/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.Cliente;
import view.Selling4Us;

/**
 * FXML Controller class
 *
 * @author Antonio David
 */
public class CardListModeradorController implements Initializable {

    @FXML
    private Label idNombreModerador;
    @FXML
    private Label idLocalidadModerador;
    @FXML
    private Label idCategoriaModerador;
    @FXML
    private MFXButton idButtonEliminar;
    
    private int idModerador;
    @FXML
    private Pane idPaneUsuarioEliminado;
    @FXML
    private Label idLabelNombre;
    @FXML
    private Label idLabelLocalidad;
    @FXML
    private Label idLabelCategoria;
    
    static Selling4Us app;
     static Cliente client;
    
    public void setApp(Selling4Us app) {
        this.app = app;
    }
    
   
  
    public void valorController(){
         client = app.getCliente();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    
    
    public void configurateModerador(String moderador){
    client = app.getCliente();
    idPaneUsuarioEliminado.setVisible(false);
    idModerador=Integer.valueOf(moderador.split("=")[0]);
    idNombreModerador.setText(moderador.split("=")[1]);
    idLocalidadModerador.setText(moderador.split("=")[2]);
    idCategoriaModerador.setText(moderador.split("=")[3]);
        
    }

    @FXML
    private void OnMouseClicked_ButtonEliminar(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
        System.out.println(idModerador);
        
        
        
        if (mostrarVentanaConfirmacion() == 1) {
            deleteModerador(idModerador + "");
            idPaneUsuarioEliminado.setVisible(true);
            idButtonEliminar.setVisible(false);
            idLabelCategoria.setVisible(false);
            idLabelLocalidad.setVisible(false);
            idLabelNombre.setVisible(false);
        }
    }
    
    int mostrarVentanaConfirmacion() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Confirmacion.fxml"));
        Parent root = null;

        root = loader.load();

        ConfirmacionController confirmacion = loader.getController();

        Scene scene = new Scene(root);
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initOwner(new Stage());
        dialog.setScene(scene);
        dialog.showAndWait();//Muestra la ventana y espera hasta que se cierre
      
        return confirmacion.estado;
    }
    
    public boolean deleteModerador(String idModerador) throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
       boolean eliminado = false;
       client.getComunicationManager().enviarMensaje(client.UserCon+ "MODERADOR:deleteModerador:"+idModerador);
         if (client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
                    eliminado=true;
                   
                }
        return eliminado;
    }
    
}
