package antonio.david.selling4usandroid.Fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;
import antonio.david.selling4usandroid.Utils.SHA;

public class RegistroFragment extends Fragment implements LocationListener {


    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private ComunicationManager comunicationManager;
    EditText etUsername;
    EditText etPassword;
    EditText etPasswordRepeat;
    EditText Fecha;
    EditText Ubi;
    Button btnLogin;
    Button btnFecha;
    Button btnUbi;
    private LocationManager locationManager;
    boolean errorRegistro=false;


    public RegistroFragment() {
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registro_fragment, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etPasswordRepeat = view.findViewById(R.id.etPasswordRepeat);
        Fecha = view.findViewById(R.id.fechaText);

        Ubi = view.findViewById(R.id.direccion);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnFecha = view.findViewById(R.id.btnDatePicker);
        btnUbi = view.findViewById(R.id.btndireccion);
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String passwordRepeat = etPasswordRepeat.getText().toString();
                if (username.contains(":") || username.contains("=") || username.contains("|") || password.contains(":") || password.contains("=") || password.contains("|") || !password.equals(passwordRepeat)) {
                    Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        hacerRegistro(username.trim(), password.trim(), Fecha.getText().toString(), Ubi.getText().toString());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
        btnUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerUbicacion();
            }
        });
        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int initialYear = calendar.get(Calendar.YEAR);
                int initialMonth = calendar.get(Calendar.MONTH);
                int initialDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dia=dayOfMonth+"";
                        if(dia.length()==1){
                            dia=0+""+dia;
                        }
                        String mes=(monthOfYear + 1)+"";
                        if(mes.length()==1){
                            mes=0+""+mes;
                        }
                        String selectedDate = dia + "/" + mes + "/" + year;
                        Fecha.setText(selectedDate);
                    }
                }, initialYear, initialMonth, initialDay);

                datePickerDialog.show();
            }
        });

        return view;
    }

    private void hacerRegistro(String username, String password, String fecha, String ciudad) throws InterruptedException {


       if(errorRegistro){
           comunicationManager.enviarMensaje("CL:USUARIO:anadirUsuarioEstandar:" + username + "|" + SHA.generate512(password) + "|" + fecha + "|" + ciudad);
           String[] cad = comunicationManager.leerMensaje().split(":");
           if (cad[1].equals("Correcto")) {
               Intent intent = new Intent(getContext(), MainActivity.class);
               intent.putExtra("idUsuario", Integer.parseInt(cad[2]));
               intent.putExtra("NombreUsuario", username);
               intent.putExtra("localizacion", ciudad);
               startActivity(intent);
           }else {
               errorRegistro=true;
           }
       }else {
           comunicationManager.enviarMensaje("CL:" + "CONEXION:peticion:" + null);
           String recibo = comunicationManager.leerMensaje();
           if (recibo.split(":")[2].equals("ComenzamosConexion")) {
               comunicationManager.enviarMensaje("CL:USUARIO:anadirUsuarioEstandar:" + username + "|" + SHA.generate512(password) + "|" + fecha + "|" + ciudad);
               String[] cad = comunicationManager.leerMensaje().split(":");
               if (cad[1].equals("Correcto")) {
                   Intent intent = new Intent(getContext(), MainActivity.class);
                   intent.putExtra("idUsuario", Integer.parseInt(cad[2]));
                   intent.putExtra("NombreUsuario", username);
                   intent.putExtra("localizacion", ciudad);
                   startActivity(intent);
               } else {
                   Toast.makeText(getActivity(), "Error al registrar prueba de nuevo", Toast.LENGTH_SHORT).show();
                    errorRegistro = true;
               }
           }
       }
    }




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
                Ubi.setText(city);
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
}
