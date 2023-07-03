/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Utils.CardListAnuncioCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
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
public class MainModeradorController implements Initializable {

    @FXML
    private MFXButton idButtonPerfil;
    @FXML
    private Label EtiquetaContraseña;
    @FXML
    private Text idNombreText;
    @FXML
    private Label idLabelCabecera;
    @FXML
    private MFXLegacyListView<String> idLegacyListAnuncio;
    @FXML
    private ImageView idLogout;
    @FXML
    private MFXButton idButtonRecargar;
    static String valor="sin inicializar";
    
    static Selling4Us app;
    static Cliente client;
    @FXML
    private Pane idPaneNoAnuncios;
    @FXML
    private MFXCheckbox idCheckBox;
    
    
    public void setApp(Selling4Us app) {
        this.app = app;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idLegacyListAnuncio.getItems().clear();
        idLegacyListAnuncio.setCellFactory(param -> new CardListAnuncioCell());
        if(valor.equals("inicializado")){
            try {
                idButtonRecargar.setDisable(true);
                 idCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    idButtonRecargar.setDisable(!newValue); 
                 });
                recargarLista();
            } catch (InterruptedException ex) {
                Logger.getLogger(MainModeradorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    public void valorController() throws InterruptedException{
         client = app.getCliente();
         valor="inicializado";
    }
    
    public synchronized void añadirAnuncio(String anuncio) {
        Platform.runLater(() -> {
            idLegacyListAnuncio.setVisible(true);
            idPaneNoAnuncios.setVisible(false);
            idLegacyListAnuncio.getItems().add(anuncio);//cambiar por hilos que se comuniquen entre sí
           client.listaEnviarCierre +=anuncio.split("\\|")[0]+"=";
            idCheckBox.setVisible(true);
            idButtonRecargar.setVisible(true);
        });
      
    }
    
    
    public void recargarLista() throws InterruptedException {
        client = app.getCliente();
        idLegacyListAnuncio.getItems().clear();
        idLegacyListAnuncio.setCellFactory(param -> new CardListAnuncioCell());
        idLegacyListAnuncio.setStyle("-fx-background-color: transparent;");
        client.getComunicationManager().setMainModerador(this);
        idPaneNoAnuncios.setVisible(true);
        
        String[] listaAnuncios;
        try {
            listaAnuncios = getAnuncios(Cliente.CategoriaModerador);
            
            if (!listaAnuncios[0].equals("")) {
                if (listaAnuncios.length != 0) {
                    idLegacyListAnuncio.setVisible(true);
                    idPaneNoAnuncios.setVisible(false);
                    idCheckBox.setVisible(true);
                    idButtonRecargar.setVisible(true);
                    for (String anuncio : listaAnuncios) {
                        idLegacyListAnuncio.getItems().add(anuncio);
                        client.listaEnviarCierre +=anuncio.split("\\|")[0]+"=";
                    }
                }
            } else {
                idLegacyListAnuncio.setVisible(false);
                idPaneNoAnuncios.setVisible(true);
                idCheckBox.setVisible(false);
                idButtonRecargar.setVisible(false);
            }

           
          
        } catch (IOException ex) {
            Logger.getLogger(MainModeradorController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainModeradorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   

    @FXML
    private void OnMouseClickedButtonPerfil(MouseEvent event) {
    }


    @FXML
    private void OnMouseClickedLogout(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
       
        if (mostrarVentanaConfirmacion() == 1) {
            Parent root = FXMLLoader.load(getClass().getResource("..\\view\\Login.fxml"));
            client.atras(devolverAnunciosCierre());
            app.changeScene(root);
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
        dialog.showAndWait();
        return confirmacion.estado;
    }

    @FXML
    private void OnMouseClickedRecargar(MouseEvent event) throws InterruptedException {
        revisarAnunciosCheck();
        recargarLista();
        idCheckBox.setSelected(false);
         
    }
    
    void revisarAnunciosCheck() throws InterruptedException{
        String lista = "";
        
        for(int i=0; i<idLegacyListAnuncio.getItems().size(); i++){
            if(!idLegacyListAnuncio.getChildrenUnmodifiable().get(i).isDisabled()){
                 lista+=idLegacyListAnuncio.getItems().get(i).split("\\|")[0]+"=";
            }
        }
        client.getComunicationManager().enviarMensaje(client.UserCon+ "ANUNCIO:revisarAnunciosCheck:"+lista);
         if(client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")){
         }
         idLegacyListAnuncio.getItems().clear();
          client.listaEnviarCierre ="";
    }
    
    String devolverAnunciosCierre() throws InterruptedException{
        String lista = "";
        
        for(int i=0; i<idLegacyListAnuncio.getItems().size(); i++){
            if(!idLegacyListAnuncio.getChildrenUnmodifiable().get(i).isDisabled()){
                 lista+=idLegacyListAnuncio.getItems().get(i).split("\\|")[0]+"=";
            }
        }
         idLegacyListAnuncio.getItems().clear();
        if(lista.equals("")){
            return null;
        }else return lista;
    }
    
    @SuppressWarnings("empty-statement")
    public String[] getAnuncios(String categoria) throws IOException, ClassNotFoundException, InterruptedException {
       String lista[] = null;
        client.getComunicationManager().enviarMensaje(client.UserCon+ "ANUNCIO:getAnuncios:"+categoria);
       if(client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")){
            lista = client.getComunicationManager().leerMensaje().split(":");
            String[] newArray = new String[lista.length - 2];
            System.arraycopy(lista, 2, newArray, 0, newArray.length);
            
            if(newArray.length!=0){
                 lista=newArray;
            }else{
                lista[0] = "";
            }
       }
        return lista;
    }
}
