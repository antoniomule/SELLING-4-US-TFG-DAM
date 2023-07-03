/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Utils.CardListModeradorCell;
import Utils.SHA;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyListView;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
public class MainController implements Initializable {

    @FXML
    private Label EtiquetaContraseña;
    @FXML
    private FontAwesomeIconView InClose;
    @FXML
    private Text idNombreText;
    @FXML
    private MFXButton idButtonPerfil;
    @FXML
    private MFXButton idButtonAñadirModerador;
    @FXML
    private MFXButton idAñadirRestriccion;
    @FXML
    private Label idLabelCabecera;
    @FXML
    private Pane idPaneAñadirModerador;
    @FXML
    private ComboBox<String> idComboBoxCategorías;
    @FXML
    private PasswordField idLabelPassword;
    @FXML
    private MFXButton idAñadirRestriccionTotal;
   
    @FXML
    private Pane idPanePerfil;
    @FXML
    private Pane idPaneRestricciones;
    @FXML
    private TextField idTextFieldNombre;
    @FXML
    private Pane idGenerarContraseña;
    @FXML
    private MFXButton idAñadirRestriccionTotal1;
    @FXML
    private MFXDatePicker idDatePicker;
    @FXML
    private ComboBox<String> idComboBoxCiudades;
    String [] categorias;
    @FXML
    private MFXButton idButtonListarModeradores;
    @FXML
    private Pane idPaneListaModeradores;
    @FXML
    public MFXLegacyListView<String> idLegacyListView;
    @FXML
    private MFXLegacyListView<String> idListViewRestriccion;
    @FXML
    private ComboBox<String> idComboBoxTipos;
    @FXML
    private Label idLabelCabecera1;
    @FXML
    private Label idLabelCabecera11;
    @FXML
    private TextField idTextFieldTipo;
    @FXML
    private Label idLabelCabecera12;
    @FXML
    private TextField idTextFieldRestriccion;
    @FXML
    private Label idLabelCabecera121;
    @FXML
    private Label idLabelDebesDarValor;
    @FXML
    private ImageView idLogout;
  
    static Selling4Us app;
    static Cliente client;
    
    public void setApp(Selling4Us app) {
        this.app = app;
    }
    
  
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idNombreText.setText(Cliente.NombreusuarioClient);
    }
    
    public void valorController(){
         client = app.getCliente();
    }

    @FXML
    private void handelClose(MouseEvent event) {
        if (event.getSource() == InClose) {
            System.exit(0);
        }
    }

    @FXML
    private void OnMouseClickedButtonPerfil(MouseEvent event) {
        idPanePerfil.setVisible(true);
        idPaneRestricciones.setVisible(false);
        idPaneAñadirModerador.setVisible(false);
        idPaneListaModeradores.setVisible(false);
        idLabelCabecera.setText("Perfil");

    }
    

    @FXML
    private void OnMouseClickedButtonAñadirModerador(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
        idPanePerfil.setVisible(false);
        idPaneRestricciones.setVisible(false);
        idPaneAñadirModerador.setVisible(true);
        idPaneListaModeradores.setVisible(false);
        idLabelCabecera.setText("Añadir Moderador");

        categorias = getCategorias();
            
        Arrays.sort(cities);
        Platform.runLater(() -> {
        idComboBoxCategorías.getItems().clear();
        idComboBoxCategorías.getItems().addAll(categorias);

        idComboBoxCiudades.getItems().clear();
        idComboBoxCiudades.getItems().addAll(cities);
        });

    }

    @FXML
    private void OnMouseClickedAñadirRestricción(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
        idPanePerfil.setVisible(false);
        idPaneRestricciones.setVisible(true);
        idPaneAñadirModerador.setVisible(false);
        idPaneListaModeradores.setVisible(false);
        idLabelCabecera.setText("Añadir Restricción");
        
        
        
        //idComboBoxTipos.getItems().clear();
        idComboBoxTipos.getItems().addAll(getTiposRestriccion());
        idTextFieldTipo.setText("");
        idTextFieldRestriccion.setText("");
        idListViewRestriccion.setVisible(false);
        idListViewRestriccion.getItems().clear();
         idListViewRestriccion.setBackground(null);
        idListViewRestriccion.setStyle("-fx-background-color: transparent;");
        
        
         idComboBoxTipos.valueProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                             String newValue
                    ) {
                        idListViewRestriccion.getItems().clear();
                        
                        try {
                            idListViewRestriccion.getItems().addAll(getRestriccionXTipo(idComboBoxTipos.getValue()));
                        } catch (IOException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        idListViewRestriccion.setVisible(true);
                        idTextFieldTipo.setText(idComboBoxTipos.getValue());
                    }
                }
                );
        
        
    }

    @FXML
    private void OnMouseClickedAñadirModerador(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
        if (mostrarVentanaConfirmacion() == 1) {
        AñadirModerador(idTextFieldNombre.getText(), idLabelPassword.getText(), idDatePicker.getText(), idComboBoxCiudades.getValue(), idComboBoxCategorías.getValue());
        idTextFieldNombre.setText("");
        idLabelPassword.setText("");
        idDatePicker.setText("");
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

    @FXML
    private void OnMouseClickedButtonListarModeradores(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
         client = app.getCliente();
        idPanePerfil.setVisible(false);
        idPaneRestricciones.setVisible(false);
        idPaneAñadirModerador.setVisible(false);
        idPaneListaModeradores.setVisible(true);
        idLabelCabecera.setText("Listado de Moderadores");

        recargarLista();

    }

    
    
     public void recargarLista() throws IOException, ClassNotFoundException, InterruptedException {
          client = app.getCliente();
         idLegacyListView.getItems().clear();
         
         String[] listaModeradores = getModeradores();
         System.out.println(listaModeradores.length);
         for(String moderador:listaModeradores){
              System.out.println(moderador);
             idLegacyListView.getItems().add(moderador);
         
         }
         
        
        idLegacyListView.setCellFactory(param -> new CardListModeradorCell());
        
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, null, null);
        Background background = new Background(backgroundFill);
        
      
    
    }

    @FXML
    private void OnMouseClickedGenerarContraseña(MouseEvent event) {
        idLabelPassword.setText(new StringBuilder(idTextFieldNombre.getText()).reverse().toString());

    }

    String[] cities = {
        "Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga",
        "Murcia", "Palma de Mallorca", "Las Palmas de Gran Canaria", "Bilbao",
        "Alicante", "Córdoba", "Valladolid", "Vigo", "Gijón", "L'Hospitalet de Llobregat",
        "La Coruña", "Granada", "Vitoria-Gasteiz", "Elche", "Santa Cruz de Tenerife",
        "Oviedo", "Badalona", "Cartagena", "Terrassa", "Jerez de la Frontera",
        "Sabadell", "Móstoles", "Alcalá de Henares", "Pamplona", "Fuenlabrada",
        "Almería", "San Sebastián", "Leganés", "Santander", "Castellón de la Plana",
        "Burgos", "Albacete", "Getafe", "Salamanca", "Huelva", "Logroño", "Badajoz",
        "San Cristóbal de La Laguna", "León", "Cádiz", "Tarragona", "Lleida", "Marbella",
        "Mataró", "Dos Hermanas", "Jaén", "Algeciras", "Torrejón de Ardoz",
        "Ourense", "Alcobendas", "Reus", "Avilés", "Elda", "Manresa", "Mérida",
        "Benidorm", "Puertollano", "Mijas", "Vilanova i la Geltrú", "Pontevedra",
        "Santiago de Compostela", "La Línea de la Concepción", "Rivas-Vaciamadrid",
        "Girona", "Mollet del Vallès", "Pinto", "Paterna", "Siero", "Sant Boi de Llobregat",
        "Viladecans", "Molina de Segura", "Eibar", "Níjar", "Linares", "Santa Lucía de Tirajana"
    };

    @FXML
    private void handleSelectTipo(ActionEvent event) throws IOException, ClassNotFoundException {
        
    }

    @FXML
    private void OnMouseClickedAñadirRestriccion(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
         client = app.getCliente();
        if(idTextFieldRestriccion.getText().equals("") || idTextFieldTipo.getText().equals("")){
            idLabelDebesDarValor.setVisible(true);
        }
        else{
            if(mostrarVentanaConfirmacion() == 1) {
               añadirRestriccion(idTextFieldTipo.getText(), idTextFieldRestriccion.getText());
               idListViewRestriccion.getItems().clear();
                idListViewRestriccion.getItems().addAll(getRestriccionXTipo(idComboBoxTipos.getValue()));
            }
        }

    }

    
    
    @FXML
    private void OnMouseClickTipos(MouseEvent event) throws IOException, ClassNotFoundException {
        
        
    }

    @FXML
    private void OnMouseClickedLogout(MouseEvent event) throws IOException, ClassNotFoundException, InterruptedException {
         client = app.getCliente();
        if (mostrarVentanaConfirmacion() == 1) {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            client.atras(null);
            app.changeScene(root);
        }
    }
    
    
    public String[] getCategorias() throws IOException, ClassNotFoundException, InterruptedException {
         client = app.getCliente();
       String lista[] = null;
       
       client.getComunicationManager().enviarMensaje(client.UserCon+ "CATEGORIAS:GetCategorias:null");
      
       String first = client.getComunicationManager().leerMensaje().split(":")[1];
       if(first.equals("Correcto")){
           System.out.println("sdas");
            lista = client.getComunicationManager().leerMensaje().split(":")[2].split("=");
       }
        return lista;
    }
    
    public String[] getModeradores() throws IOException, ClassNotFoundException, InterruptedException {
         client = app.getCliente();
       String lista[] = null;
        client.getComunicationManager().enviarMensaje(client.UserCon+ "MODERADOR:GetModeradores:null");
       
        if ( client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
            //lista =  client.getComunicationManager().leerMensaje().split(":")[2].split(":");
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
    
     
    
     public boolean AñadirModerador(String nombre, String contraseña,String fecha, String Ciudad, String categoria) throws IOException, ClassNotFoundException, InterruptedException {
          client = app.getCliente();
         boolean creado =false;
          client.getComunicationManager().enviarMensaje(client.UserCon+ "MODERADOR:AnadirModerador:"+nombre+"="+SHA.generate512(contraseña)+"="+fecha+"="+Ciudad+"="+categoria);
               if (client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
                    creado=true;
                }
         return creado;
     }
     
     public String[] getTiposRestriccion() throws IOException, ClassNotFoundException, InterruptedException {
        //client = app.getCliente();
         String lista[] = null;
       client.getComunicationManager().enviarMensaje(client.UserCon+ "RESTRICCION:getTiposRestriccion:null");
        if (client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
            lista = client.getComunicationManager().leerMensaje().split(":")[2].split("=");
        }
        
        return lista;
    }
     
     public String[] getRestriccionXTipo(String tipo) throws IOException, ClassNotFoundException, InterruptedException {
         client = app.getCliente();
         String lista[] = null;
        client.getComunicationManager().enviarMensaje(client.UserCon + "RESTRICCION:getRestriccionXTipo:" + tipo);
        if (client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
            lista = client.getComunicationManager().leerMensaje().split(":")[2].split("=");
        }

        return lista;
    }
     
     public boolean añadirRestriccion(String tipo, String mensaje) throws IOException, ClassNotFoundException, InterruptedException {
        
          client = app.getCliente();boolean creado = false;
        client.getComunicationManager().enviarMensaje(client.UserCon + "RESTRICCION:anadirRestriccion:" + tipo + "=" + mensaje);
        if (client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
            creado = true;

        }
        return creado;
    }

   

}
