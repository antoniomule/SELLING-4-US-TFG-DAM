package antonio.david.selling4usandroid.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;

public class NewAnuncioFragment extends Fragment implements LocationListener {

    private Button obtenerUbicacionButton;
    private LocationManager locationManager;
    private boolean modificar = false;
    private TextView direccionText;

    private EditText tex_titulo;
    private EditText text_descripcion;
    private EditText text_precio;
    private EditText text_estado;
    private Button btnCapture;
    private Button btnSubirAnucio;
    private Spinner comboBoxCategorias;
    private String categoriaSeleccionada;
    private ImageView imageView;
    private ComunicationManager comunicationManager =  new ComunicationManager();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;


    public static int IDusuarioClient;
    public static String NombreusuarioClient;

    //Utilizado si es para modificar anuncio
    private String idAnuncio;
    private String titulo;
    private String descripcion;
    private String precio;
    private String direccion;
    private byte[] imagen;
    private String estado;
    private String categoria;
    //-------------------------------//
    private MainActivity mainActivity;
    public NewAnuncioFragment(int IDusuarioClient, String NombreusuarioClient, MainActivity mainActivity) throws IOException {
        this.IDusuarioClient = IDusuarioClient;
        this.NombreusuarioClient = NombreusuarioClient;
        this.mainActivity=mainActivity;
    }

    //Reutilizo la clase para modificar los anuncios
    public NewAnuncioFragment(int IDusuarioClient, boolean modificar, String id, String titulo, String descripcion, String precio, String direccion, byte[] imagen, String estado, String categoria, MainActivity mainActivity) throws IOException {
        this.IDusuarioClient=IDusuarioClient;
        this.modificar = modificar;
        this.idAnuncio=id;
        this.titulo=titulo;
        this.descripcion=descripcion;
        this.precio=precio;
        this.direccion=direccion;
        this.imagen=imagen;
        this.estado=estado;
        this.categoria=categoria;
        this.mainActivity=mainActivity;
    }

    public NewAnuncioFragment() throws IOException {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_anuncio, container, false);
        tex_titulo = view.findViewById(R.id.text_titulo);
        text_descripcion = view.findViewById(R.id.text_descripcion);
        text_precio = view.findViewById(R.id.text_precio);
        text_estado = view.findViewById(R.id.text_estado);


        //---Ubicacion----//
        direccionText = view.findViewById(R.id.text_direccion);
        obtenerUbicacionButton = view.findViewById(R.id.btn_obtener_ubicacion);
        obtenerUbicacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerUbicacion();
            }
        });
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        //---------------//
        //--Abrir Camara Imagen--//
        btnCapture = view.findViewById(R.id.btn_subir_imagen);
        imageView = view.findViewById(R.id.image_anuncio);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    return;
                }dispatchTakePictureIntent();
            }
        });
        //---------------//

        comboBoxCategorias = view.findViewById(R.id.combobox_categorias);
        ArrayAdapter<String> adapter = null;
        try {
            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getCategorias());
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboBoxCategorias.setAdapter(adapter);

        comboBoxCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Maneja el caso en el que no se haya seleccionado ninguna opción
            }
        });
        btnSubirAnucio = view.findViewById(R.id.btn_subir_anuncio);
        btnSubirAnucio.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache(true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                if(modificar){
                    String anuncio = null;
                    if (tex_titulo.getText().toString().equals("") || tex_titulo.getText().toString().contains("=") || tex_titulo.getText().toString().contains("|") || tex_titulo.getText().toString().contains(":") || text_descripcion.getText().toString().equals("") ||  text_descripcion.getText().toString().contains("=") || text_descripcion.getText().toString().contains("|") || text_descripcion.getText().toString().contains(":") || text_precio.getText().toString().equals("") || text_precio.getText().toString().contains("=") || text_precio.getText().toString().contains("|") || text_precio.getText().toString().contains(":") || direccionText.getText().equals("") || text_estado.getText().toString().equals("") || text_estado.getText().toString().contains("=") || text_estado.getText().toString().contains("|") || text_estado.getText().toString().contains(":")) {
                        Toast.makeText(getContext(), "Revisa los valores", Toast.LENGTH_SHORT).show();
                    }else {
                        anuncio = idAnuncio +"|"+ tex_titulo.getText() + "|" + text_descripcion.getText() + "|" + text_precio.getText() + "|" + direccionText.getText() + "|" + Base64.getEncoder().encodeToString(byteArray) + "|" + text_estado.getText() + "|" + IDusuarioClient + "|" + categoriaSeleccionada;

                        new ModificarAnuncioTask().execute(anuncio);

                    }
                }else {
                    String anuncio = null;
                    if (tex_titulo.getText().toString().equals("") || tex_titulo.getText().toString().contains("=") || tex_titulo.getText().toString().contains("|") || tex_titulo.getText().toString().contains(":") || text_descripcion.getText().toString().equals("") ||  text_descripcion.getText().toString().contains("=") || text_descripcion.getText().toString().contains("|") || text_descripcion.getText().toString().contains(":") || text_precio.getText().toString().equals("") || text_precio.getText().toString().contains("=") || text_precio.getText().toString().contains("|") || text_precio.getText().toString().contains(":") || direccionText.getText().equals("") || text_estado.getText().toString().equals("") || text_estado.getText().toString().contains("=") || text_estado.getText().toString().contains("|") || text_estado.getText().toString().contains(":")) {
                        Toast.makeText(getContext(), "Revisa los valores", Toast.LENGTH_SHORT).show();
                    }else {
                        anuncio = tex_titulo.getText() + "|" + text_descripcion.getText() + "|" + text_precio.getText() + "|" + direccionText.getText() + "|" + Base64.getEncoder().encodeToString(byteArray) + "|" + text_estado.getText() + "|" + IDusuarioClient + "|" + categoriaSeleccionada;
                        new EnviarAnuncioTask().execute(anuncio);
                    }
                }
            }
        });


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("idUsuario", IDusuarioClient);
                    intent.putExtra("NombreUsuario", NombreusuarioClient);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        if(modificar){
            direccionText.setText(direccion);
            tex_titulo.setText(titulo);
            text_descripcion.setText(descripcion);
            text_precio.setText(precio);
            text_estado.setText(estado);
            comboBoxCategorias.setSelection(adapter.getPosition(categoria));
            imageView.setImageBitmap(byteArrayToBitmap(imagen));
            btnSubirAnucio.setText("MODIFICAR ANUNCIO");
        }
        return view;
    }
    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    //---Ubicacion----//
    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                Toast.makeText(requireContext(), "Ciudad actual: " + city, Toast.LENGTH_SHORT).show();
                direccionText.setText(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationManager.removeUpdates(this);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    //-----Ubi------//

    //-----Foto-----//
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cambiar getActivity por fraqgment

        //if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
       // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if(imageBitmap!=null){
                int width = imageBitmap.getWidth();
                int height = imageBitmap.getHeight();
                float scaleFactor = Math.min((float) 1024 / width, (float) 1024 / height);
                int newWidth = Math.round(scaleFactor * width);
                int newHeight = Math.round(scaleFactor * height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, true);

                imageView.setImageBitmap(resizedBitmap);
            }

        }else{
            Toast.makeText(getContext(), "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
        }
    }
    //-----------//


    private class EnviarAnuncioTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... anuncio) {
            String resultado = "";
            try {
                comunicationManager.enviarMensaje("CL:ANUNCIO:subirAnuncio:" + anuncio[0]);
                resultado = comunicationManager.leerMensaje();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return resultado;
        }


        protected void onPostExecute(String mensaje) {
            if (mensaje.split(":")[1].equals("Correcto")) {
                Toast.makeText(getContext(), "Anuncio subido correctamente", Toast.LENGTH_SHORT).show();

                MisAnunciosFragment misAnunciosFragment = new MisAnunciosFragment(IDusuarioClient, mainActivity);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, misAnunciosFragment);
                fragmentTransaction.commit();
            } else {
                Toast.makeText(getContext(), "Anuncio no subido correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ModificarAnuncioTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... anuncio) {
            String resultado = "";
            try {
                comunicationManager.enviarMensaje("CL:ANUNCIO:modificarAnuncio:" + anuncio[0]);
                resultado = comunicationManager.leerMensaje();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return resultado;
        }


        protected void onPostExecute(String mensaje) {
            if (mensaje.split(":")[1].equals("Correcto")) {
                Toast.makeText(getContext(), "Anuncio actualizado correctamente", Toast.LENGTH_SHORT).show();

                MisAnunciosFragment misAnunciosFragment = new MisAnunciosFragment(IDusuarioClient, mainActivity);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, misAnunciosFragment);
                fragmentTransaction.commit();

            } else {
                Toast.makeText(getContext(), "Anuncio no actualizado correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public String[] getCategorias() throws IOException, ClassNotFoundException, InterruptedException {

        String lista[] = null;

        comunicationManager.enviarMensaje("CL:"+ "CATEGORIAS:GetCategorias:null");

        String first = comunicationManager.leerMensaje().split(":")[1];
        if(first.equals("Correcto")){
            System.out.println("sdas");
            lista = comunicationManager.leerMensaje().split(":")[2].split("=");
        }
        return lista;
    }
}