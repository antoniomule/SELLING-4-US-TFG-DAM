package antonio.david.selling4usandroid.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.util.Objects;

import antonio.david.selling4usandroid.Adapter.AnuncioAdapter;
import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;
import antonio.david.selling4usandroid.Adapter.RestriccionAdapter;

public class MisAnunciosFragment extends Fragment {
    private ComunicationManager comunicationManager;
    private RecyclerView recyclerView;

    private ImageView loadingImageView;
    private RequestManager glide;
    private AnuncioAdapter anuncioAdapter;
    private RestriccionAdapter restriccionAdapter;
    private TextView misAnunciosText;
    private TextView anuncioBloqueadosText;
    private TextView resolucion;

    public int IDusuarioClient;
    public MainActivity mainActivity;

    public MisAnunciosFragment(int IDusuarioClient, MainActivity mainActivity) {
        this.IDusuarioClient = IDusuarioClient;
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mainActivity=mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_anuncios, container, false);

        loadingImageView = view.findViewById(R.id.loading_image);
        glide = Glide.with(this);

        // Carga el GIF en el ImageView
        glide.load(R.raw.progress)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(loadingImageView);


        resolucion = view.findViewById(R.id.resolucionMisAnuncios);
        misAnunciosText = view.findViewById(R.id.misAnunciosText);
        anuncioBloqueadosText = view.findViewById(R.id.misBloqueados);

        misAnunciosText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingImageView.setVisibility(View.VISIBLE);
                resolucion.setText("MIS ANUNCIOS");
                new GetMisAnunciosTask().execute();
            }
        });

        anuncioBloqueadosText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingImageView.setVisibility(View.VISIBLE);
                resolucion.setText("ANUNCIOS BLOQUEADOS");
                new GetMisAnunciosBloqueadosTask().execute();
            }
        });


        recyclerView = view.findViewById(R.id.recycler_MisAnuncios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        new GetMisAnunciosTask().execute();

        return view;
    }

    private class GetMisAnunciosTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            try {
                return getMisAnunciosAppAndroid();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String[] lista) {
            if (!Objects.equals(lista[0], "")) {
                anuncioAdapter = new AnuncioAdapter(lista, getFragmentManager(), IDusuarioClient, mainActivity);
                anuncioAdapter.setMio(true);
                recyclerView.setAdapter(anuncioAdapter);
                loadingImageView.setVisibility(View.GONE);
            } else {
                loadingImageView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No tienes anuncios subidos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String[] getMisAnunciosAppAndroid() throws InterruptedException {
        String lista[] = null;
        comunicationManager.enviarMensaje("CL:" + "ANUNCIO:getMisAnunciosAppAndroid:" + IDusuarioClient);
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

    private String[] getMisAnunciosBloqueados() throws InterruptedException {
        String lista[] = null;
        comunicationManager.enviarMensaje("CL:" + "ANUNCIO:getMisAnunciosBloqueados:" + IDusuarioClient);
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

    private class GetMisAnunciosBloqueadosTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            try {
                return getMisAnunciosBloqueados();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String[] lista) {
            if (!Objects.equals(lista[0], "")) {
                restriccionAdapter = new RestriccionAdapter(lista, getFragmentManager(), IDusuarioClient, mainActivity);
                recyclerView.setAdapter(restriccionAdapter);
                loadingImageView.setVisibility(View.GONE);
            } else {
                loadingImageView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No hay anuncios para esta categoría", Toast.LENGTH_SHORT).show();
            }
        }
    }




}
