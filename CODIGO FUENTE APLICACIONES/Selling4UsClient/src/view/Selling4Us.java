/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.CardListAnuncioController;
import controller.CardListModeradorController;
import controller.DetallesAnuncioController;
import controller.LoginController;
import controller.MainController;
import controller.MainModeradorController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/**
 *
 * @author Antonio David
 */
public class Selling4Us extends Application{
    Cliente cliente;
    
    private static double xOffset=0.0;
    private static double yOffset=0.0;
    private static Stage newStage;
    private static Scene newScene;

    public Cliente getCliente() {
        return cliente;
    }

    
    public static void move(Parent root, Stage stage){
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX()-xOffset);
            stage.setY(event.getScreenY()-yOffset);
        });
    }

    public static void changeScene(Parent root) {
        Scene scene = new Scene(root);
        move(root, newStage);
        newStage.setScene(scene);
        newStage.show();
    }
    
    
    @Override
    public void start(Stage stage) throws Exception {
        cliente = new Cliente();
        
        //Cargar MainController
        FXMLLoader loaderMain = new FXMLLoader(getClass().getResource("Main.fxml"));
        loaderMain.load();
        MainController mainController = loaderMain.getController();
        mainController.setApp(this);
        mainController.valorController();
        
        //Cargar MainModeradorController
        FXMLLoader loaderMainModerador = new FXMLLoader(getClass().getResource("MainModerador.fxml"));
        loaderMainModerador.load();
        MainModeradorController mainModeradorController = loaderMainModerador.getController();
        mainModeradorController.setApp(this);
        mainModeradorController.valorController();
       
        //Cargar CarListAnuncio Controller
        FXMLLoader loaderCardAnuncio = new FXMLLoader(getClass().getResource("CardListAnuncio.fxml"));
        loaderCardAnuncio.load();
        CardListAnuncioController cardListAnuncioController = loaderCardAnuncio.getController();
       cardListAnuncioController.configurateAnucio("");
        
        
        //Cargar CardListModerador Controller
        FXMLLoader loaderCardListModeradorController = new FXMLLoader(getClass().getResource("CardListModerador.fxml"));
        loaderCardListModeradorController.load();
        CardListModeradorController cardListModeradorController = loaderCardListModeradorController.getController();
        cardListModeradorController.setApp(this);
        cardListModeradorController.valorController();
        
        
        //Cargar DetallesAnuncio Controller
        
        FXMLLoader loaderDetallesAnuncioController = new FXMLLoader(getClass().getResource("DetallesAnuncio.fxml"));
        loaderDetallesAnuncioController.load();
        DetallesAnuncioController detallesAnuncioController = loaderDetallesAnuncioController.getController();
        detallesAnuncioController.setApp(this);
        detallesAnuncioController.valorController();
        
        //Cargar LoginController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setApp(this);
       
        
        this.newStage = stage;
        
        ;
         move(root, newStage);
        newScene = new Scene(root);
        newStage.setTitle("Selling 4 Us");
        newStage.setScene(newScene);
        newStage.show();
        
        newStage.setOnCloseRequest(event -> {
            try {
                cliente.cierro();
            } catch (IOException ex) {
                Logger.getLogger(Selling4Us.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Selling4Us.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Selling4Us.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        });
        cliente.start();
          
        Platform.runLater(() -> {
            try {
                loginController.conectar();
            } catch (IOException ex) {
                Logger.getLogger(Selling4Us.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Selling4Us.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
      
}
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        launch(args);
    }
}
