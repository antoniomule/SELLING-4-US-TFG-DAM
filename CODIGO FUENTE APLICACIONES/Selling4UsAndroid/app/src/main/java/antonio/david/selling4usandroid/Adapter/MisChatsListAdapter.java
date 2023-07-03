package antonio.david.selling4usandroid.Adapter;

import static antonio.david.selling4usandroid.MainActivity.estoyEnChat;

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

import java.util.Base64;

import antonio.david.selling4usandroid.Fragments.ChatFragment;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;

public class MisChatsListAdapter extends RecyclerView.Adapter<MisChatsListAdapter.MisChatsListViewHolder> {

    private String[] chatsList;
    private int IDUsuarioClient;
    private MainActivity main;
    private FragmentManager fragmentManager;
    public MisChatsListAdapter(String[] chats, int IDusuarioClient, FragmentManager fragmentManager, MainActivity main) {
        this.IDUsuarioClient = IDusuarioClient;
        this.chatsList = chats;
        this.fragmentManager=fragmentManager;
        this.main=main;
    }

    @NonNull
    @Override
    public MisChatsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new MisChatsListViewHolder(view);
    }
    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MisChatsListViewHolder holder, int position) {
        String chat = chatsList[position];

        holder.idAnuncio = Integer.parseInt(chat.split("\\|")[0]);
        holder.text_nombre_anuncio.setText(chat.split("\\|")[1]);
        holder.text_nombre_propietario.setText(chat.split("\\|")[2]);
        holder.idChat = Integer.parseInt(chat.split("\\|")[3]);
        holder.idPropietario =Integer.parseInt(chat.split("\\|")[4]);
        holder.image_anuncio.setImageBitmap(byteArrayToBitmap(Base64.getDecoder().decode(chat.split("\\|")[5])));
        holder.idInteresado=Integer.parseInt(chat.split("\\|")[6]);
        if(chat.split("\\|")[7].equals("No")){
            holder.imagenNoRevisado.setImageResource(R.drawable.alert);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment chatFragment;
                chatFragment = new ChatFragment(IDUsuarioClient, holder.idChat, holder.idPropietario, main, holder.idInteresado);
                if(IDUsuarioClient== holder.idPropietario){
                    chatFragment.setPropietario(true);
                }
                estoyEnChat.put(holder.idChat, true);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, chatFragment);
                fragmentTransaction.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatsList.length;
    }

    public class MisChatsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int idAnuncio;
        TextView text_nombre_anuncio;
        TextView text_nombre_propietario;
        int idChat;
        int idPropietario;
        int idInteresado;
        ImageView image_anuncio;
        ImageView imagenNoRevisado;

        public MisChatsListViewHolder(@NonNull View itemView) {
            super(itemView);
            text_nombre_anuncio = itemView.findViewById(R.id.text_nombre_anuncio_chat);
            text_nombre_propietario = itemView.findViewById(R.id.text_nombre_propietario);
            image_anuncio = itemView.findViewById(R.id.image_anuncio_chat);
            imagenNoRevisado = itemView.findViewById(R.id.imagenNoRevisado);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String chat = chatsList[position];
            }
        }
    }
}

