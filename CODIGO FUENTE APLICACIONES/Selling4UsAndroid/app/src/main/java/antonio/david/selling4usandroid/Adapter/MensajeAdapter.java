package antonio.david.selling4usandroid.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import antonio.david.selling4usandroid.Fragments.ChatFragment;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private String[] mensajeList;
    private Context context;
    private int iDusuarioClient;

    MainActivity mainActivity;
    private ChatFragment chatFragment;


    public MensajeAdapter(Context context, String[] mensajes, int iDusuarioClient, MainActivity main, ChatFragment chatFragment) {
        this.context = context;
        this.mensajeList = mensajes;
        this.iDusuarioClient=iDusuarioClient;
        this.mainActivity=main;
         mainActivity.setMensajeAdapter(this);
         this.chatFragment=chatFragment;
    }







    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensaje, parent, false);

        return new MensajeViewHolder(view);
    }

    public void addMensaje(String nuevoMensaje) {
        ArrayList<String> mensajeArrayList = new ArrayList<>(Arrays.asList(mensajeList));
        mensajeArrayList.add(nuevoMensaje);
        mensajeList = mensajeArrayList.toArray(new String[0]);
        notifyItemInserted(mensajeList.length-1);
        chatFragment.scrollRecicler();
    }


    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        String mensaje = mensajeList[position];
        holder.idMensaje=Integer.parseInt(mensaje.split("\\|")[0]);
        holder.idChat=Integer.parseInt(mensaje.split("\\|")[1]);
        holder.idRemitente=Integer.parseInt(mensaje.split("\\|")[2]);
        holder.idDestinatario=Integer.parseInt(mensaje.split("\\|")[3]);
        holder.mensajeTextView.setText(mensaje.split("\\|")[4]);
        if (iDusuarioClient == holder.idRemitente) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.END;
            holder.mensajeTextView.setLayoutParams(layoutParams);
        }else{

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.START;
            holder.mensajeTextView.setLayoutParams(layoutParams);
        }
    }


    @Override
    public int getItemCount() {
        return mensajeList.length;
    }

    public class MensajeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int idMensaje;
        int idChat;
        int idRemitente;
        int idDestinatario;
        TextView mensajeTextView;
        LinearLayout linearLayout;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            mensajeTextView = itemView.findViewById(R.id.text_mensaje_chat);
            linearLayout = itemView.findViewById(R.id.cardview_mensaje);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


        }
    }
}

