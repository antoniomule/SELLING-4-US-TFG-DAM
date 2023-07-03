/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Antonio David
 */
public class CardListAnuncioController implements Initializable {

    @FXML
    private ImageView idImagen;
    @FXML
    private Label idTitulo;
    @FXML
    private Label idDescripcion;
    @FXML
    private Label idPrecio;
    private String Direccion;
    private String Estado;
    
    int idDueño=-1;
    private String nombreDueño;
    int idAnuncio=-1;
    String Categoria;
    @FXML
    private Label idRevision;
    @FXML
    
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       idImagen.setPreserveRatio(true);
    }    

    public int getIdAnuncio() {
        return idAnuncio;
    }
    
    
    public void configurateAnucio(String anuncio) {
        if (!anuncio.equals("")) {
            idAnuncio = Integer.valueOf(anuncio.split("\\|")[0]);
            idTitulo.setText(anuncio.split("\\|")[1]);
            idDescripcion.setText(anuncio.split("\\|")[2]);
            idPrecio.setText(anuncio.split("\\|")[3]);
            Direccion = anuncio.split("\\|")[4];
            idImagen.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(anuncio.split("\\|")[5]))));
            Estado = anuncio.split("\\|")[6];
            idRevision.setText(anuncio.split("\\|")[7]);
            idDueño = Integer.valueOf(anuncio.split("\\|")[8]);
            nombreDueño = anuncio.split("\\|")[9];
            Categoria = anuncio.split("\\|")[10];
        }
    }
}
