package antonio.david.selling4usandroid;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import java.io.IOException;

import antonio.david.selling4usandroid.Utils.ComunicationManager;

public class FiltroDialogPopUp extends DialogFragment implements MapaDialogPopUp.setUbicacionesToFragment{


    TextView titleTextView;
    ImageButton closeButton;
    EditText productNameEditText;

    TextView minValueTextView;
    TextView maxValueTextView;
    ToggleButton ascendingButton;
    TextView ordenTextView;
    Spinner categorySpinner;
    Button openMapButton;
    Button filterButton;
    String direccionText;
    String[] categorias;

    String categoriaSeleccionada;
    String listadeUbicaciones = "vacía";

    private PerfilFragment perfilFragment;

    private ComunicationManager comunicationManager;

    private int IDUsuarioClient;
    {
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FiltroDialogPopUp(String direccionText, String[] categorias, PerfilFragment perfilFragment, int IDUsuarioClient) {
        this.direccionText = direccionText;
        this.categorias=categorias;
        this.perfilFragment=perfilFragment;
        this.IDUsuarioClient = IDUsuarioClient;
    }




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_filtrado, null);
        builder.setView(view);

         titleTextView = view.findViewById(R.id.popup_title_textview);
         closeButton = view.findViewById(R.id.close_button);
         productNameEditText = view.findViewById(R.id.product_name_edittext);


         minValueTextView = view.findViewById(R.id.min);
         maxValueTextView = view.findViewById(R.id.max);
         ascendingButton = view.findViewById(R.id.ascending_button);
         ordenTextView = view.findViewById(R.id.text_ordenadorpor);
         categorySpinner = view.findViewById(R.id.category_spinner);
         openMapButton = view.findViewById(R.id.open_map_button);
         filterButton = view.findViewById(R.id.filter_button);

        ascendingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getActivity(), "Orden ascendente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Orden descendente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        titleTextView.setText("FILTRADO DE BÚSQUEDA");

        ArrayAdapter<String> adapter = null;

            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Maneja el caso en el que no se haya seleccionado ninguna opción
            }
        });

        // Configurar el botón de cierre
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapaDialogPopUp mapaPopUp = new MapaDialogPopUp(direccionText, (MapaDialogPopUp.setUbicacionesToFragment) FiltroDialogPopUp.this);
                mapaPopUp.show(getChildFragmentManager(), "Filtrado por Ubicacion");
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameEditText.getText().toString().trim();
                String minValue = minValueTextView.getText().toString().trim();
                String maxValue = maxValueTextView.getText().toString().trim();
                if(minValue.equals("") && maxValue.equals("")){
                    minValue="0";
                    maxValue="0";
                }
                /*if(listadeUbicaciones==null){
                    listadeUbicaciones="ubicacionesVacías";

                }*/

                if (!productName.isEmpty()) {
                    try {
                        double min = Double.parseDouble(minValue);
                        double max = Double.parseDouble(maxValue);


                        if (min <= max) {

                            String envio =  IDUsuarioClient+"|"+productName+"|"+minValue+"|"+maxValue+"|"+ascendingButton.isChecked()+"|"+categoriaSeleccionada+"|"+listadeUbicaciones;
                            System.out.println("envio:" +envio);

                            AnunciosFiltroTask task = new AnunciosFiltroTask();
                            task.execute(envio);

                        } else {
                            Toast.makeText(getActivity(), "El valor mínimo debe ser menor o igual al valor máximo", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Los valores de min y max deben ser numéricos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

    @Override
    public void setUbicacionesToFragment(String[] ubi) {
        listadeUbicaciones="";
        for(String ubicacion : ubi){
            listadeUbicaciones+=ubicacion+"|";
            System.out.println(ubicacion+"|");
        }

    }




    private class AnunciosFiltroTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String envio = params[0];
            String[] lista = null;

            try {
                comunicationManager.enviarMensaje("CL:ANUNCIO:getAnunciosFiltro:" + envio);
                if (comunicationManager.leerMensaje().split(":")[1].equals("Correcto")) {
                    lista = comunicationManager.leerMensaje().split(":");
                    String[] newArray = new String[lista.length - 2];
                    System.arraycopy(lista, 2, newArray, 0, newArray.length);

                    if (newArray.length != 0) {
                        lista = newArray;
                    } else {
                        lista = new String[]{""};
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("surmano la lista: "+lista.toString());
            for(String anuncio : lista){
                System.out.println(anuncio);

            }
            return lista;
        }

        @Override
        protected void onPostExecute(String[] lista) {
            dismiss();

            // Llama al método ListaFiltrada de PerfilFragment
            if (perfilFragment != null) {
                perfilFragment.ListaFiltrada(lista);
            }

        }
    }


}
