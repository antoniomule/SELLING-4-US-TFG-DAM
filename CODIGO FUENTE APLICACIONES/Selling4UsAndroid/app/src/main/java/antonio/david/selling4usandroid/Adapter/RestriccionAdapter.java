package antonio.david.selling4usandroid.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Base64;

import antonio.david.selling4usandroid.Fragments.NewAnuncioFragment;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;

public class RestriccionAdapter extends RecyclerView.Adapter<RestriccionAdapter.RestriccionViewHolder> {

    private String[] restriccionList;
    private int IDUsuarioClient;
    private FragmentManager fragmentManager;
    private MainActivity mainActivity;

    public RestriccionAdapter(String[] restricciones, FragmentManager fragmentManager, int IDusuarioClient, MainActivity mainActivity) {
        this.IDUsuarioClient = IDusuarioClient;
        this.restriccionList = restricciones;
        this.fragmentManager = fragmentManager;
        this.mainActivity=mainActivity;
    }

    @NonNull
    @Override
    public RestriccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensaje_restriccion, parent, false);
        return new RestriccionViewHolder(view);
    }
    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RestriccionViewHolder holder, int position) {
        String restriccion = restriccionList[position];

        holder.idAnuncio = Integer.parseInt(restriccion.split("\\|")[0]);
        holder.text_nombre_anuncio.setText(restriccion.split("\\|")[1]);
        holder.descripcion_anuncio = restriccion.split("\\|")[2];
        holder.precio = restriccion.split("\\|")[3];
        holder.direccion = restriccion.split("\\|")[4];
        holder.image_anuncio.setImageBitmap(byteArrayToBitmap(Base64.getDecoder().decode(restriccion.split("\\|")[5])));
        holder.estado =  restriccion.split("\\|")[6];
        holder.categoria = restriccion.split("\\|")[7];
        holder.text_tipo_restriccion.setText(restriccion.split("\\|")[8]);
        holder.text_detalle_restriccion.setText(restriccion.split("\\|")[9]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnuncioFragment newAnuncioFragment;
                try {
                    newAnuncioFragment = new NewAnuncioFragment(IDUsuarioClient, true, holder.idAnuncio+"",  holder.text_nombre_anuncio.getText().toString(),  holder.descripcion_anuncio,  holder.precio, holder.direccion, Base64.getDecoder().decode(restriccion.split("\\|")[5]), holder.estado, holder.categoria, mainActivity);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, newAnuncioFragment);
                fragmentTransaction.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return restriccionList.length;
    }

    public class RestriccionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int idAnuncio;
        TextView text_nombre_anuncio;
        String descripcion_anuncio;
        String precio;
        String estado;
        String categoria;
        String direccion;
        TextView text_tipo_restriccion;
        TextView text_detalle_restriccion;
        ImageView image_anuncio;

        public RestriccionViewHolder(@NonNull View itemView) {
            super(itemView);
            text_nombre_anuncio = itemView.findViewById(R.id.text_nombre_anuncio);
            text_tipo_restriccion = itemView.findViewById(R.id.text_tipo_restriccion);
            text_detalle_restriccion = itemView.findViewById(R.id.text_detalle_restriccion);
            image_anuncio = itemView.findViewById(R.id.image_anuncio);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String restriccion = restriccionList[position];
            }
        }
    }
}

