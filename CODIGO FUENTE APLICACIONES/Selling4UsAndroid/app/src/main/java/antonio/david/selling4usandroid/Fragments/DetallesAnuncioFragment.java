package antonio.david.selling4usandroid.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.PayPalActivity;
import antonio.david.selling4usandroid.R;

public class DetallesAnuncioFragment  extends Fragment implements OnMapReadyCallback{
    ComunicationManager comunicationManager = new ComunicationManager();
    private int idAnuncio;
    private String titulo;
    private String detalles;
    private String precio;
    private String ciudad;

    private byte[] imagenCadena;
    private ImageView imageView;

    private String estado;
    private int idPropietario;
    private String nombrePropietario;
    private String categoria;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView priceTextView;
    private TextView statusTextView;
    private TextView ownerTextView;
    private TextView categoryTextView;
    private MapView mapView;
    private GoogleMap googleMap;
    private String faltaPago;

    public  int IDusuarioClient;
    private Button boton_compra;
    private Button boton_oferta;
    private Button boton_pagar;
    private Button boton_chat;
    private TextView resolucionPago;
    static boolean  neceistaPagoOferta = false;
    private static final int PAYPAL_REQUEST_CODE = 123;

    private MainActivity mainActivity;

    public DetallesAnuncioFragment(int idUser, int idAnuncio, String titulo, String detalles, String precio, String ciudad, byte[] imageView, String estado, int idPropietario, String nombrePropietario, String categoria, String faltaPago, MainActivity mainActivity) throws IOException {
        this.IDusuarioClient=idUser;
        this.idAnuncio = idAnuncio;
        this.titulo = titulo;
        this.detalles = detalles;
        this.precio = precio;
        this.ciudad = ciudad;
        this.imagenCadena = imageView;
        this.estado = estado;
        this.idPropietario = idPropietario;
        this.nombrePropietario = nombrePropietario;
        this.categoria = categoria;
        this.faltaPago = faltaPago;
        this.mainActivity=mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_anuncio, container, false);

        // Asignar los IDs a cada elemento del layout
        titleTextView = view.findViewById(R.id.titleTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        priceTextView = view.findViewById(R.id.priceTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        ownerTextView = view.findViewById(R.id.ownerTextView);
        categoryTextView = view.findViewById(R.id.categoryTextView);
        mapView = view.findViewById(R.id.mapView);


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        imageView = view.findViewById(R.id.image_anuncio);
        boton_compra = view.findViewById(R.id.compra_button);
        boton_oferta = view.findViewById(R.id.oferta_button);
        boton_pagar = view.findViewById(R.id.pagar_button);
        boton_chat = view.findViewById(R.id.compra_abrirChat);
        resolucionPago = view.findViewById(R.id.resolucionPago);

        boton_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hacerCompra();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        boton_oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hacerOferta();
            }
        });
        boton_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    hacerCompra();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        boton_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirChat();
            }
        });

        if(IDusuarioClient==idPropietario){
            boton_compra.setVisibility(View.GONE);
            boton_oferta.setVisibility(View.GONE);
            boton_chat.setVisibility(View.GONE);
        }

        if(faltaPago.split("=")[0].equals("FaltaPago")){
            boton_compra.setVisibility(View.GONE);
            boton_oferta.setVisibility(View.GONE);
            boton_chat.setVisibility(View.GONE);
            boton_pagar.setVisibility(View.VISIBLE);
            neceistaPagoOferta=true;
            boton_pagar.setText("PAGAR "+faltaPago.split("=")[1]+"/"+precio+"€");
        }

        if(faltaPago.split("=")[0].equals("Pagado")){
            boton_compra.setVisibility(View.GONE);
            boton_oferta.setVisibility(View.GONE);
            boton_pagar.setVisibility(View.GONE);
            boton_chat.setVisibility(View.GONE);
            resolucionPago.setVisibility(View.VISIBLE);
            resolucionPago.setText("Pagaste "+faltaPago.split("=")[1]+" € por este artículo");
        }

        // Configurar los valores en los elementos del layout
        titleTextView.setText(titulo);
        descriptionTextView.setText(detalles);
        priceTextView.setText(precio);
        statusTextView.setText(estado);
        ownerTextView.setText("Propietario: "+nombrePropietario);
        categoryTextView.setText(categoria);
        imageView.setImageBitmap(byteArrayToBitmap(imagenCadena));

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(ciudad, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    // Mover y añadir un marcador en las coordenadas
                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
                    googleMap.addMarker(new MarkerOptions().position(location));
                }
            });
        }
        return view;
    }

    private void abrirChat() {
        try {
            comunicationManager.enviarMensaje("CL:CHAT:crearChat:" + idAnuncio + "|" + IDusuarioClient + "|" +idPropietario+ "|"+titulo);
            String[] cad = comunicationManager.leerMensaje().split(":");
            if (cad[1].equals("Correcto")) {
                ChatFragment chatFragment = new ChatFragment(IDusuarioClient, Integer.parseInt(cad[2]), idPropietario, mainActivity, IDusuarioClient);
                FragmentManager fragmentManager = getFragmentManager();


                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, chatFragment);
                fragmentTransaction.commit();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    void hacerOferta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Hacer oferta");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_hacer_oferta, null);
        builder.setView(dialogView);

        EditText editTextOferta = dialogView.findViewById(R.id.editTextOferta);
        TextView textViewPrecio = dialogView.findViewById(R.id.textViewPrecio);
        textViewPrecio.setText("/" + precio + "€");

        builder.setPositiveButton("Enviar oferta", (dialog, which) -> {
            String ofertaString = editTextOferta.getText().toString();
            if (!ofertaString.isEmpty()) {
                double oferta = Double.parseDouble(ofertaString);
                if (oferta > 0 && oferta < Double.parseDouble(precio)) {
                    try {
                        comunicationManager.enviarMensaje("CL:ANUNCIO:ofertaAnuncio:" + idAnuncio + "|" + IDusuarioClient + "|" + oferta+"|"+idPropietario+"|"+titulo);
                        String first = comunicationManager.leerMensaje().split(":")[1];
                        if (first.equals("Correcto")) {
                            Toast.makeText(getContext(), "Oferta enviada", Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(getContext(), "La oferta debe ser mayor a 0€ y menor al precio del producto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Ingresa un valor para la oferta", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if(neceistaPagoOferta){
                    try {
                        comunicationManager.enviarMensaje("CL:ANUNCIO:pagoAnuncio:" + idAnuncio);
                        String first = comunicationManager.leerMensaje().split(":")[1];
                        if (first.equals("Correcto")) {
                            MisComprasFragment MisComprasFragment = new MisComprasFragment(IDusuarioClient, mainActivity);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_content, MisComprasFragment);
                            fragmentTransaction.commit();
                            mainActivity.eliminarAnuncioListaBuffer(idAnuncio);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    try {
                        comunicationManager.enviarMensaje("CL:ANUNCIO:comprarAnuncio:" + idAnuncio + "|" + IDusuarioClient + "|" + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()) + "|" + precio + "|" + idPropietario + "|" + titulo);
                        String first = comunicationManager.leerMensaje().split(":")[1];
                        if (first.equals("Correcto")) {
                            MisComprasFragment MisComprasFragment = new MisComprasFragment(IDusuarioClient, mainActivity);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_content, MisComprasFragment);
                            fragmentTransaction.commit();
                            mainActivity.eliminarAnuncioListaBuffer(idAnuncio);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Valorar al vendedor");
        builder.setMessage("¿Quieres valorar a "+nombrePropietario+"?");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        RatingBar ratingBar = new RatingBar(getContext());
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ratingBar.setLayoutParams(layoutParams);

        layout.addView(ratingBar);
        builder.setView(layout);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float rating = ratingBar.getRating();
                try {
                    comunicationManager.enviarMensaje("CL:USUARIO:valorarUsuario:" + idPropietario + "|" + rating);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    void hacerCompra() throws InterruptedException {
        if(neceistaPagoOferta){
            Intent intent = new Intent(getContext(), PayPalActivity.class);
            intent.putExtra("precio", faltaPago.split("=")[1]);
            intent.putExtra("titulo", titulo);
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirmar compra");
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirmar_compra, null);
            builder.setView(dialogView);

            TextView textViewConfirmation = dialogView.findViewById(R.id.textViewConfirmation);
            textViewConfirmation.setText("¿Estás seguro de que quieres comprar este producto por " + precio + "€?");
            builder.setPositiveButton("Sí", (dialog, which) -> {
                Intent intent = new Intent(getContext(), PayPalActivity.class);
                intent.putExtra("precio", precio);
                intent.putExtra("titulo", titulo);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            });
            builder.setNegativeButton("No", (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
