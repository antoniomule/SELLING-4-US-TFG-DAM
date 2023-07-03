/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;


import controller.CardListAnuncioController;
import controller.CardListModeradorController;
import controller.DetallesAnuncioController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Antonio David
 */
public class CardListAnuncioCell extends ListCell<String>{
    public static int idAnuncio;
    public CardListAnuncioCell(){
        super();
        setOnMouseClicked(event -> {
            if (!isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetallesAnuncio.fxml"));
                Parent root = null;
               
                try {
                    root = loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(CardListAnuncioCell.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                DetallesAnuncioController controller = loader.getController();
                try {
                    controller.iniciarValores(getItem());
                } catch (IOException ex) {
                    Logger.getLogger(CardListAnuncioCell.class.getName()).log(Level.SEVERE, null, ex);
                }
                Scene scene = new Scene(root);
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(new Stage());
                dialog.setScene(scene);
                dialog.showAndWait();
                if(controller.isBloqueado()){
                    
                    this.setDisable(true);
                    this.setStyle("-fx-background-color: #d2d2d2;");
                }
            }
        });
    }
    @Override
        public void updateItem(String anuncio,boolean empty){
            super.updateItem(anuncio, empty);
            
            if(empty || anuncio == null){
            this.setText(null);
            this.setGraphic(null);
            }
            if(anuncio != null && !empty){
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/view/CardListAnuncio.fxml"));
                    idAnuncio = Integer.valueOf(anuncio.split("\\|")[0]);
                    Pane pane = loader.load();
                    this.setDisable(false);
                    CardListAnuncioController cardListAnuncioController = loader.getController();
                   
                    cardListAnuncioController.configurateAnucio(anuncio);
                    this.setGraphic(pane);
                     setBackground(null);
                     setStyle("-fx-background-color: transparent;");
                     this.setStyle("-fx-padding: 10px;");
                } catch (IllegalStateException e){
                }catch (IOException ex) {
                    ex.printStackTrace();
                
            }
        }
}

    public int getIdAnuncio() {
        return idAnuncio;
    }

}
