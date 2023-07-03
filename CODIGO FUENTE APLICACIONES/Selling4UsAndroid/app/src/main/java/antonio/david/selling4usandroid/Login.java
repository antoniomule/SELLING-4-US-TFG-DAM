package antonio.david.selling4usandroid;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import antonio.david.selling4usandroid.Fragments.RegistroFragment;
import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.Utils.SHA;

public class Login extends AppCompatActivity  implements LocationListener {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    public static int IDusuarioClient;
    public static String NombreusuarioClient;
    public static String tipoUsuario;
    public static String CategoriaModerador;
    private LocationManager locationManager;
    private String direccionText;
    private ComunicationManager comunication;
    private TextView regitrarse;
    private boolean errorLogin=false;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        regitrarse = findViewById(R.id.regitrarse);
        regitrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RegistroFragment registroFragment = new RegistroFragment();
                fragmentTransaction.replace(android.R.id.content, registroFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        obtenerUbicacion();

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString("ip", null);
        String port = sharedPreferences.getString("puerto", null);
        if(ip!=null){
            try {
                comunication = new ComunicationManager(ip, port);
                comunication.conectar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{

            showIPPortDialog();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();


                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginTask().execute(username, SHA.generate512(password));
                }
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            if (username.isEmpty() || password.isEmpty()) {
                return "Por favor, completa todos los campos";
            } else {
                try {

                    if(errorLogin){
                        comunication.enviarMensaje("CL:" + "USUARIO:Login:" + username + "=" + password);
                        String entrada = comunication.leerMensaje();
                        if (entrada.split(":")[1].equals("Correcto")) {
                            String[] lista = comunication.leerMensaje().split(":")[2].split("=");
                            IDusuarioClient = (Integer.parseInt(lista[0]));
                            NombreusuarioClient = (lista[1]);
                            tipoUsuario = lista[2];
                            if (tipoUsuario.equals("Moderador")) {
                                CategoriaModerador = lista[3];
                            }

                            return "Correcto";
                        } else {
                            return "LOGIN INCORRECTO";
                        }
                    }else {
                        comunication.enviarMensaje("CL:" + "CONEXION:peticion:" + null);
                        String recibo = comunication.leerMensaje();
                        if (recibo.split(":")[2].equals("ComenzamosConexion")) {
                            comunication.enviarMensaje("CL:" + "USUARIO:Login:" + username + "=" + password);
                            String entrada = comunication.leerMensaje();
                            if (entrada.split(":")[1].equals("Correcto")) {
                                String[] lista = comunication.leerMensaje().split(":")[2].split("=");
                                IDusuarioClient = (Integer.parseInt(lista[0]));
                                NombreusuarioClient = (lista[1]);
                                tipoUsuario = lista[2];
                                if (tipoUsuario.equals("Moderador")) {
                                    CategoriaModerador = lista[3];
                                }

                                return "Correcto";
                            } else {
                                return "LOGIN INCORRECTO";
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Correcto")) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("idUsuario", IDusuarioClient);
                intent.putExtra("NombreUsuario", NombreusuarioClient);
                intent.putExtra("localizacion", direccionText);
                startActivity(intent);
            } else {
                errorLogin=true;
                Toast.makeText(Login.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }









    //---Ubicacion----//
    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                direccionText=city;

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
    public void onProviderDisabled(String provider) {}


    private void showIPPortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_ip_port, null);
        final EditText editTextIP = dialogView.findViewById(R.id.editTextIP);
        final EditText editTextPort = dialogView.findViewById(R.id.editTextPort);

        builder.setView(dialogView)
                .setTitle("Ingrese IP y Puerto")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ip = editTextIP.getText().toString();
                        String port = editTextPort.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ip", ip);
                        editor.putString("puerto", port);
                        editor.apply();
                        try {
                            comunication = new ComunicationManager(ip, port);
                            comunication.conectar();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
