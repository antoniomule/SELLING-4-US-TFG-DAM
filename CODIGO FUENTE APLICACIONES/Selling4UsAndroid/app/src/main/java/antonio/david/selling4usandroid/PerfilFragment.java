package antonio.david.selling4usandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Objects;

import antonio.david.selling4usandroid.Adapter.AnuncioAdapter;
import antonio.david.selling4usandroid.Adapter.CategoriaAdapter;
import antonio.david.selling4usandroid.Utils.ComunicationManager;

public class PerfilFragment extends Fragment{

    private ComunicationManager comunicationManager;
    private RecyclerView recyclerView;
    public int IDusuarioClient;
    private AnuncioAdapter anuncioAdapter;
    private CategoriaAdapter categoriaAdapter;
    private String categoriaSeleccionada;
    private ImageView loadingImageView;
    private Button filterButton;
    private Button resetearButton;
    private RequestManager glide;
    private String direccionText;
    SharedPreferences sharedPreferences;
    String[] listaReturn = null;
    String[] categoriasSeek = null;
    private MainActivity mainActivity;

    public PerfilFragment(int IDusuarioClient, String direccionText, MainActivity mainActivity) {
        try {
            comunicationManager = new ComunicationManager();
            this.IDusuarioClient = IDusuarioClient;
            this.direccionText=direccionText;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mainActivity=mainActivity;
    }

    public PerfilFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        String[] listaCategorias;
        try {
            listaCategorias = getCategorias();
            categoriasSeek = listaCategorias;
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        loadingImageView = view.findViewById(R.id.loading_imagePerfil);
        sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String listaReturnString = sharedPreferences.getString("listaReturn", null);
        if (listaReturnString != null) {
            listaReturn = listaReturnString.split(":");

            for(String anuncio : listaReturn){
                System.out.println("anunciooo: "+anuncio);
            }
            recyclerView = view.findViewById(R.id.recycler_anuncios);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            anuncioAdapter = new AnuncioAdapter(listaReturn, getFragmentManager(), IDusuarioClient, mainActivity);
            recyclerView.setAdapter(anuncioAdapter);
        }else{
            recyclerView = view.findViewById(R.id.recycler_categorias);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            categoriaAdapter = new CategoriaAdapter(getActivity(), listaCategorias, this);
            recyclerView.setAdapter(categoriaAdapter);
        }

        filterButton = view.findViewById(R.id.filter_button);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltroDialogPopUp filtroDialogPopUp = new FiltroDialogPopUp(direccionText, categoriasSeek,PerfilFragment.this, IDusuarioClient);
                filtroDialogPopUp.show(getChildFragmentManager(), "FILTRADO DE BÚSQUEDA");
            }
        });

        resetearButton = view.findViewById(R.id.reset_button);
        resetearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("listaReturn");
                editor.apply();
                recyclerView.setVisibility(View.VISIBLE);
                categoriaAdapter = new CategoriaAdapter(getActivity(), listaCategorias, PerfilFragment.this);
                recyclerView.setAdapter(categoriaAdapter);

            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("categoriaSeleccionada", categoriaSeleccionada);
    }

    public void mostrarAnunciosPorCategoria(String categoria) {
        categoriaSeleccionada = categoria;
        recyclerView.setVisibility(View.GONE);

        glide = Glide.with(this);
        glide.load(R.raw.progress)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(loadingImageView);
        loadingImageView.setVisibility(View.VISIBLE);
        new GetAnunciosAsyncTask().execute(categoria);
    }

    public String[] getAnunciosAppAndroid(String categoria) throws IOException, ClassNotFoundException, InterruptedException {
        String lista[] = null;
        comunicationManager.enviarMensaje("CL:ANUNCIO:getAnunciosAppAndroid:" + categoria+"|"+IDusuarioClient);
        if (comunicationManager.leerMensaje().split(":")[1].equals("Correcto")) {
            lista = comunicationManager.leerMensaje().split(":");
            String[] newArray = new String[lista.length - 2];
            System.arraycopy(lista, 2, newArray, 0, newArray.length);

            if (newArray.length != 0) {
                lista = newArray;
            } else {
                lista= new String[]{""};
            }
        }
        return lista;
    }

    public String[] getCategorias() throws IOException, ClassNotFoundException, InterruptedException {
        String lista[] = null;
        comunicationManager.enviarMensaje("CL:CATEGORIAS:GetCategorias:null");

        String first = comunicationManager.leerMensaje().split(":")[1];
        if (first.equals("Correcto")) {
            lista = comunicationManager.leerMensaje().split(":")[2].split("=");
        }
        return lista;
    }






    private class GetAnunciosAsyncTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String categoria = params[0];
            try {
                return getAnunciosAppAndroid(categoria);
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] lista) {
            loadingImageView.setVisibility(View.GONE);

            if (!Objects.equals(lista[0], "")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                String listaReturnString ="";
                for(String anuncio : lista){
                    listaReturnString+=anuncio+":";

                }
                editor.putString("listaReturn", listaReturnString);
                editor.apply();

                recyclerView = getView().findViewById(R.id.recycler_anuncios);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                anuncioAdapter = new AnuncioAdapter(lista, getFragmentManager(), IDusuarioClient, mainActivity);
                recyclerView.setAdapter(anuncioAdapter);
            } if(lista.equals("")) {
                Toast.makeText(getActivity(), "No hay anuncios para esta categoría", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void ListaFiltrada(String[] lista){
        loadingImageView.setVisibility(View.GONE);

        if (!Objects.equals(lista[0], "")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String listaReturnString ="";
            for(String anuncio : lista){
                listaReturnString+=anuncio+":";

            }
            editor.putString("listaReturn", listaReturnString);
            editor.apply();

            recyclerView.setVisibility(View.GONE);
            recyclerView = getView().findViewById(R.id.recycler_anuncios);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            anuncioAdapter = new AnuncioAdapter(lista, getFragmentManager(), IDusuarioClient, mainActivity);
            recyclerView.setAdapter(anuncioAdapter);
        } else{
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No hay anuncios para este filtro", Toast.LENGTH_SHORT).show();
        }
    }
}
