/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import view.Cliente;

/**
 * FXML Controller class
 *
 * @author Antonio David
 */
public class ConfirmacionController implements Initializable {

    @FXML
    private MFXButton idButtonSi;
    @FXML
    private MFXButton idButtonNo;
    @FXML
    private FontAwesomeIconView InClose;
    
    private static final int WAITING = 0;
    private static final int CONFIRMO = 1;
    private static final int DENIEGO = 2;
    public int estado;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        estado=WAITING;
    }    

    @FXML
    private void OnMouseClicked_SI(MouseEvent event) {
        
       estado=CONFIRMO;
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
       
        
    }

    @FXML
    private void OnMouseClicked_NO(MouseEvent event) {
        estado = DENIEGO;
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void handelClose(MouseEvent event) {
        estado=DENIEGO;
         Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    
    
}
