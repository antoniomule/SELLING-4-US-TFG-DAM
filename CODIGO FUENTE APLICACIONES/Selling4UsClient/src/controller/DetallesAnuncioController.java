/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONObject;
import view.Cliente;
import view.Selling4Us;

/**
 * FXML Controller class
 *
 * @author Antonio David
 */
public class DetallesAnuncioController implements Initializable {

    private int idAnuncio;
    private int idPropietario;
    @FXML
    private ImageView idImagen;
    @FXML
    private Label idTitulo;
    @FXML
    private Label idDescripcion;
    @FXML
    private Label idPrecio;
    @FXML
    private Label idRevision;
    @FXML
    private WebView idWebMapa;
    String Ciudad;
    private static final String ClaveAPI="AIzaSyCqc-QCcLCaFXzXgbU8AhpLqAbyrac0RcQ";
                                          //AIzaSyCqc-QCcLCaFXzXgbU8AhpLqAbyrac0RcQ
    double latitud;
    double longitud;
    @FXML
    private Label idCiudadText;
    @FXML
    private Label idEstado;
    @FXML
    private Label idCategoria;
    @FXML
    private Label nombreDueño;
    @FXML
    private MFXLegacyComboBox<String> idComboBoxTipos;
    @FXML
    private MFXLegacyComboBox<String> idComboBoxDetalles;
    @FXML
    private MFXButton idButtonBloquear;
    @FXML
    private Text idTextMEnsajeNotificacion;
    
     String tipoRestriccion;
     String detalleRestriccion;
     
    public  boolean bloqueado=false;

     static Selling4Us app;
     static Cliente client;
     
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    
    
    public void setApp(Selling4Us app) {
        this.app = app;
    }
    public void valorController(){
         client = app.getCliente();
    }
    
    
    public void iniciarValores(String anuncio) throws MalformedURLException, IOException {
        idAnuncio = Integer.valueOf(anuncio.split("\\|")[0]);
        idTitulo.setText(anuncio.split("\\|")[1]);
        idDescripcion.setText(anuncio.split("\\|")[2]);
        Ciudad = anuncio.split("\\|")[4];
        idPrecio.setText(anuncio.split("\\|")[3]);
        idImagen.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(anuncio.split("\\|")[5]))));
        idEstado.setText(anuncio.split("\\|")[6]);
        idRevision.setText(anuncio.split("\\|")[7]);
        idPropietario = Integer.valueOf(anuncio.split("\\|")[8]);
        nombreDueño.setText(anuncio.split("\\|")[9]);
        idCategoria.setText(anuncio.split("\\|")[10]);
        
        
        
        try {
            idComboBoxTipos.getItems().addAll(getTiposRestriccion());
            idButtonBloquear.setDisable(true);
            idComboBoxDetalles.setDisable(true);
        } catch (IOException ex) {
            Logger.getLogger(DetallesAnuncioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DetallesAnuncioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DetallesAnuncioController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      
        idComboBoxTipos.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue
            ) {
                idComboBoxDetalles.getItems().clear();

                try {
                    tipoRestriccion=idComboBoxTipos.getValue();
                    idComboBoxDetalles.getItems().addAll(getRestriccionXTipo(idComboBoxTipos.getValue()));
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }

                idComboBoxDetalles.setDisable(false);
            }
        }
        );
        String detalle;
        idComboBoxDetalles.setOnAction(event -> {
            
            detalleRestriccion = idComboBoxDetalles.getValue();

            
            if (detalleRestriccion != null) {
                idButtonBloquear.setDisable(false);
            } else {
                idButtonBloquear.setDisable(true);
            }
        });
        
        
        idCiudadText.setText(Ciudad);
        
        pedirLongitudLatitud();
        WebEngine webEngine = idWebMapa.getEngine();
        String url = "https://www.google.com/maps/embed/v1/view?zoom=10&key=" + ClaveAPI + "&center=" + latitud + "," + longitud;
        String iframeHtml = "<iframe width='230' height='130' src='" + url + "' allowfullscreen></iframe>";
        webEngine.loadContent(iframeHtml);

    }
    
    void pedirLongitudLatitud() throws UnsupportedEncodingException, MalformedURLException, IOException {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + Ciudad + "&key=" + ClaveAPI;

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();

        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject response = new JSONObject(content.toString());
            if (response.getString("status").equals("OK")) {
                JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                latitud = location.getDouble("lat");
                longitud = location.getDouble("lng");
            }
        }
    }

    
    
    public String[] getTiposRestriccion() throws IOException, ClassNotFoundException, InterruptedException {
        client = app.getCliente();
        String lista[] = null;
        client.getComunicationManager().enviarMensaje(client.UserCon + "RESTRICCION:getTiposRestriccion:null");
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

    void bloquearAnuncio() throws InterruptedException {
        client = app.getCliente();
        String datos = idAnuncio+"="+idPropietario+"="+tipoRestriccion+"="+detalleRestriccion+"="+idTitulo.getText();
        client.getComunicationManager().enviarMensaje(client.UserCon + "ANUNCIO:bloquearAnuncio:" + datos);
        if (client.getComunicationManager().leerMensaje().split(":")[1].equals("Correcto")) {
        }
    }
    
    @FXML
    private void OnMouseClickBloquear(MouseEvent event) throws InterruptedException {
        idTextMEnsajeNotificacion.setVisible(true);
        bloquearAnuncio();
        bloqueado=true;
        idComboBoxTipos.setDisable(true);
        idComboBoxDetalles.setDisable(true);
        idButtonBloquear.setDisable(true);
    }
}
