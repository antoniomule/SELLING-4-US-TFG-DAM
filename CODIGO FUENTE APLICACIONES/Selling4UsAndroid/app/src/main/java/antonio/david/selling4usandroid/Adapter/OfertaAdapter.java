package antonio.david.selling4usandroid.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.Fragments.MisOfertasFragment;
import antonio.david.selling4usandroid.R;

public class OfertaAdapter extends RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder> {

    private String[] ofertaList;
    private Context context;
    private FragmentManager fragmentManager;
    private int IDusuarioClient;
    int idOferta;

    private ComunicationManager comunicationManager;

    public OfertaAdapter(Context context, String[] categorias, FragmentManager fragmentManager, int IDusuarioClient) {
        this.context = context;
        this.ofertaList = categorias;
        this.fragmentManager = fragmentManager;
        this.IDusuarioClient=IDusuarioClient;
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @NonNull
    @Override
    public OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_oferta, parent, false);
        return new OfertaViewHolder(view);
    }
    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull OfertaViewHolder holder, int position) {
        String oferta = ofertaList[position];
        idOferta=Integer.parseInt(oferta.split("\\|")[0]);
        holder.nombreAnuncio.setText(oferta.split("\\|")[1]);
        holder.nombreUsuario.setText(oferta.split("\\|")[2]);
        holder.textPrecioOferta.setText(oferta.split("\\|")[3]+"€");
        holder.textPrecioTotal.setText("/"+oferta.split("\\|")[4]+"€");
        holder.imageView.setImageBitmap(byteArrayToBitmap(Base64.getDecoder().decode(oferta.split("\\|")[5])));
        holder.button_si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    comunicationManager.enviarMensaje("CL:ANUNCIO:aceptarOferta:" + idOferta + "|" + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                    String first = comunicationManager.leerMensaje().split(":")[1];
                    if (first.equals("Correcto")) {
                        MisOfertasFragment misOfertasFragment = new MisOfertasFragment(IDusuarioClient);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_content, misOfertasFragment);
                        fragmentTransaction.commit();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        holder.button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    comunicationManager.enviarMensaje("CL:ANUNCIO:denegarOferta:" + idOferta);
                    String first = comunicationManager.leerMensaje().split(":")[1];
                    if (first.equals("Correcto")) {
                        MisOfertasFragment misOfertasFragment = new MisOfertasFragment(IDusuarioClient);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_content, misOfertasFragment);
                        fragmentTransaction.commit();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }


    @Override
    public int getItemCount() {
        return ofertaList.length;
    }

    public class OfertaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView nombreAnuncio;
        TextView nombreUsuario;
        Button button_si;
        Button button_no;
        TextView textPrecioOferta;
        TextView textPrecioTotal;

        public OfertaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_anuncio_oferta);
            nombreAnuncio = itemView.findViewById(R.id.text_nombre_anuncio);
            nombreUsuario = itemView.findViewById(R.id.text_nombre_usuario);
            button_si = itemView.findViewById(R.id.button_si);
            button_no = itemView.findViewById(R.id.button_no);
            textPrecioOferta = itemView.findViewById(R.id.text_precio_oferta);
            textPrecioTotal = itemView.findViewById(R.id.text_precio_total);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String oferta = ofertaList[position];
             }
        }
    }
}

