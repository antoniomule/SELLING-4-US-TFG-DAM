package antonio.david.selling4usandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import antonio.david.selling4usandroid.Utils.ComunicationManager;

public class MapaDialogPopUp extends DialogFragment implements OnMapReadyCallback, LocationListener {

    private static final int DEFAULT_RADIUS = 5000;

    private ComunicationManager comunicationManager;

    {
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String direccionText;
    private Context context;
    private GoogleMap googleMap;
    private Circle circle;
    private ArrayList<String> selectedCities = new ArrayList<>();
    private MapView mapView;
    private Geocoder geocoder;
    private List<Marker> markers = new ArrayList<>();
    private List<String> markersWithinRadius = new ArrayList<>();

    double lat = 0;
    double lon = 0;

    public interface setUbicacionesToFragment {
        void setUbicacionesToFragment(String[] markers);
    }

    private setUbicacionesToFragment UbicacionesMapa;

    public MapaDialogPopUp(String direccionText, setUbicacionesToFragment ubi) {
        this.direccionText = direccionText;
        this.UbicacionesMapa = ubi;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mapa_popup, null);
        builder.setView(view);

        context = getContext();
        mapView = view.findViewById(R.id.mapViewPopUp);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Configurar botones
        Button btnZoomIn = view.findViewById(R.id.btnZoomIn);
        Button btnZoomOut = view.findViewById(R.id.btnZoomOut);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        btnZoomIn.setOnClickListener(v -> {
            if (circle != null) {
                float radius = (float) circle.getRadius();
                circle.setRadius(radius * 0.8f);
            }
        });

        btnZoomOut.setOnClickListener(v -> {
            if (circle != null) {
                float radius = (float) circle.getRadius();
                circle.setRadius(radius * 1.25f);
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (circle != null && googleMap != null) {
                markersWithinRadius.clear();
                LatLng circleCenter = circle.getCenter();
                double circleRadius = circle.getRadius();

                for (Marker marker : markers) {
                    LatLng markerPosition = marker.getPosition();
                    double distance = SphericalUtil.computeDistanceBetween(circleCenter, markerPosition);

                    if (distance <= circleRadius) {
                        markersWithinRadius.add(marker.getTitle());
                    }
                }

                showToastWithMarkers();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addresses1 = null;
                try {
                    addresses1 = geocoder.getFromLocationName(direccionText, 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Address address = addresses1.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                lat = latitude;
                lon = longitude;
                LatLng location = new LatLng(latitude, longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title(direccionText));
                markers.add(marker);
                new GetProvinciasAsyncTask().execute(direccionText);
                CircleOptions circleOptions = new CircleOptions()
                        .center(location)
                        .radius(DEFAULT_RADIUS)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(2f)
                        .fillColor(Color.parseColor("#500084d3"));
                circle = googleMap.addCircle(circleOptions);
            }
        });

        return builder.create();
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    private class GetProvinciasAsyncTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            try {
                return getProvinciasCercanas();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] lista) {
            if (!Objects.equals(lista[0], "")) {
                for (String provincia : lista) {
                    LatLng location = new LatLng(Double.valueOf(provincia.split("=")[1]), Double.valueOf(provincia.split("=")[2]));
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title(provincia.split("=")[0]));
                    markers.add(marker);
                }
            }
            if (lista.equals("")) {
                Toast.makeText(getActivity(), "No hay provincias cerca para esta dirección", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String[] getProvinciasCercanas() throws IOException, ClassNotFoundException, InterruptedException {
        String[] lista = null;
        comunicationManager.enviarMensaje("CL:MAPA:getProvinciasCercanas:" + lat + "|" + lon);
        String first = comunicationManager.leerMensaje().split(":")[1];
        if (first.equals("Correcto")) {
            lista = comunicationManager.leerMensaje().split(":")[2].split("@");
        }
        return lista;
    }

    private void showToastWithMarkers() {
        StringBuilder sb = new StringBuilder();

        for (String markerTitle : markersWithinRadius) {
            sb.append(markerTitle).append("\n");
        }

        if (sb.length() > 0) {
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No hay marcadores dentro del radio", Toast.LENGTH_SHORT).show();
        }
        getDialog().dismiss();
        if (UbicacionesMapa != null) {
            UbicacionesMapa.setUbicacionesToFragment(markersWithinRadius.toArray(new String[0]));
        }
    }
}
