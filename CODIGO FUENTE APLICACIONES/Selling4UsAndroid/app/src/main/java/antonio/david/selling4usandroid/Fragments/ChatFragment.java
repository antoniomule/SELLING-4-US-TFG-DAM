package antonio.david.selling4usandroid.Fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Objects;

import antonio.david.selling4usandroid.Adapter.MensajeAdapter;
import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;

public class ChatFragment extends Fragment{

    private RecyclerView recyclerView;
    private MensajeAdapter mensajeAdapter;
    private EditText editText;
    private Button sendButton;

    private ComunicationManager comunicationManager;
    private int iDusuarioClient;
    private int idChat;
    private int idDestinatario;
    private int idInteresado;
    private MainActivity main;
    private boolean propietario=false;

    public ChatFragment(int iDusuarioClient, int idChat, int idDestinatario,MainActivity main, int idInteresado) {
        this.iDusuarioClient=iDusuarioClient;
        this.idChat=idChat;
        this.idDestinatario=idDestinatario;
        this.idInteresado=idInteresado;
        this.main=main;

    }

    public void setPropietario(boolean propietario) {
        this.propietario = propietario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_chat, container, false);
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recyclerView = view.findViewById(R.id.recycler_mensaje);
        editText = view.findViewById(R.id.editText);
        sendButton = view.findViewById(R.id.sendButton);
        if (Build.VERSION.SDK_INT >= 11) {
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(
                                        recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }, 100);
                    }
                }
            });
        }


        editText.setText("");
        new getMensajesChatTask().execute();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString().trim();
                if (!message.isEmpty()) {
                    String nuevoMensaje;
                    if(propietario){
                        nuevoMensaje = idChat+"|"+iDusuarioClient+"|"+idInteresado+"|"+editText.getText().toString().trim();
                    }else{
                        nuevoMensaje = idChat+"|"+iDusuarioClient+"|"+idDestinatario+"|"+editText.getText().toString().trim();
                    }
                    new addMensajeChatTask().execute(nuevoMensaje);

                }
            }
        });

        return view;
    }

    public void scrollRecicler(){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount() - 1);
            }
        }, 100);

    }

    private class getMensajesChatTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            try {

                return getMensajesChat();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String[] lista) {
            if (!Objects.equals(lista[0], "")) {
                lanzar(lista);
               // loadingImageView.setVisibility(View.GONE);
            } else {
                //loadingImageView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No tienes ninguna compra registrada", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void lanzar(String[] lista){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mensajeAdapter = new MensajeAdapter(getActivity(), lista, iDusuarioClient, main, this);
        recyclerView.setAdapter(mensajeAdapter);
        scrollRecicler();
    }

    private String[] getMensajesChat() throws InterruptedException {
        String lista[] = null;
        comunicationManager.enviarMensaje("CL:" + "CHAT:getMensajesChat:" + idChat);
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

    private class addMensajeChatTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                String nuevoMensaje = params[0];
                return addMensajeChat(nuevoMensaje);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String mensajeAñadido) {
            if (!mensajeAñadido.equals("")) {
                mensajeAdapter.addMensaje(-1+"|"+mensajeAñadido);
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount() - 1);
                    }
                }, 100);

                editText.setText("");

            } else {
                Toast.makeText(getActivity(), "No tienes ninguna compra registrada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String addMensajeChat(String nuevoMensaje) throws InterruptedException {
        comunicationManager.enviarMensaje("CL:" + "CHAT:addMensajeChat:" + nuevoMensaje);
        if (comunicationManager.leerMensaje().split(":")[1].equals("Correcto")) {
            return nuevoMensaje;
        }else return "";

    }
}
