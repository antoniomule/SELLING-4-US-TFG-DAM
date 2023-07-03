package antonio.david.selling4usandroid.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class MisVentasFragment extends Fragment {
    private ComunicationManager comunicationManager;
    private RecyclerView recyclerView;

    private ImageView loadingImageView;
    private RequestManager glide;
    private AnuncioAdapter anuncioAdapter;

    public int IDusuarioClient;
    private MainActivity mainActivity;
    public MisVentasFragment(int IDusuarioClient, MainActivity mainActivity) {
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
        View view = inflater.inflate(R.layout.fragment_mis_ventas, container, false);

        loadingImageView = view.findViewById(R.id.loading_image);
        glide = Glide.with(this);
        glide.load(R.raw.progress)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(loadingImageView);



        recyclerView = view.findViewById(R.id.recycler_MisVentas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        new GetMisVentas().execute();

        return view;
    }

    private class GetMisVentas extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            try {
                return getMisVentas();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String[] lista) {
            if (!Objects.equals(lista[0], "")) {
                anuncioAdapter = new AnuncioAdapter(lista, getFragmentManager(), IDusuarioClient, mainActivity);
                anuncioAdapter.setVendido(true);
                recyclerView.setAdapter(anuncioAdapter);
                loadingImageView.setVisibility(View.GONE);
            } else {
                loadingImageView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No hay ventas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String[] getMisVentas() throws InterruptedException {
        String lista[] = null;
        comunicationManager.enviarMensaje("CL:" + "ANUNCIO:getMisVentas:" + IDusuarioClient);
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
}
