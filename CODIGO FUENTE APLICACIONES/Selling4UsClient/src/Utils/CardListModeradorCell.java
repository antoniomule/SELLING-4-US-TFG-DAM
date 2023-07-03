/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;


import controller.CardListModeradorController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 *
 * @author Antonio David
 */
public class CardListModeradorCell extends ListCell<String>{
    public CardListModeradorCell(){
        super();
        
        setOnMouseClicked(event -> {
            if (!isEmpty()) {
                
                /*FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/DetallesMensaje.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(CorreoCell.class.getName()).log(Level.SEVERE, null, ex);
                }
                DetallesMensajeController controller = loader.getController();
                controller.configurateDetalles(getItem());
                Scene scene = new Scene(root);
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.initOwner(new Stage());
                dialog.setScene(scene);
                dialog.showAndWait();*/
            }
        });
    }
    @Override
        public void updateItem(String moderador,boolean empty){
            super.updateItem(moderador, empty);
            
            if(empty || moderador == null){
            this.setText(null);
            this.setGraphic(null);
            }
            if(moderador != null && !empty){
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/view/CardListModerador.fxml"));
                    
                    Pane pane = loader.load();
                    
                    CardListModeradorController cardListModeradorController = loader.getController();
                   
                    cardListModeradorController.configurateModerador(moderador);
                    this.setGraphic(pane);
                     setBackground(null);
                     setStyle("-fx-background-color: transparent;");
                    /* if (isSelected()) {
                        setTextFill(Color.web("#F08080"));// F08080
                    }*/
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        
        
}
