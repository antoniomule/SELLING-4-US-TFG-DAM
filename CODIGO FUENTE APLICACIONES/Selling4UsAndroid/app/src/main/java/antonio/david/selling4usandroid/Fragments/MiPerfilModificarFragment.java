package antonio.david.selling4usandroid.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

import java.io.IOException;

import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.PerfilFragment;
import antonio.david.selling4usandroid.R;
import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.Utils.SHA;

public class MiPerfilModificarFragment extends Fragment {

    private EditText etPassword;
    private EditText etPasswordRepeat;
    private TextView nombre;
    private String nombreUser;
    private int IDUsuarioClient;
    private ComunicationManager comunicationManager;

    public MiPerfilModificarFragment(String nombreUsuario, int IDUsuarioClient) {
        this.nombreUser = nombreUsuario;
        this.IDUsuarioClient = IDUsuarioClient;
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.miperfil_fragment, container, false);
        etPassword = view.findViewById(R.id.etPassword);
        etPasswordRepeat = view.findViewById(R.id.etPasswordRepeat);
        nombre = view.findViewById(R.id.nombre_usuario_miperfil);
        nombre.setText(nombreUser);
        Button btnModificar = view.findViewById(R.id.btnModificar);

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordRepeat = etPasswordRepeat.getText().toString();

                if (password.isEmpty() || passwordRepeat.isEmpty()) {
                    Toast.makeText(getActivity(), "Por favor, completa ambos campos.", Toast.LENGTH_SHORT).show();
                } else {
                    new ActualizarContraseñaTask().execute(password, passwordRepeat);
                }
            }
        });
        return view;
    }


    private class ActualizarContraseñaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String password = params[0];
            String passwordRepeat = params[1];

            try {
                comunicationManager.enviarMensaje("CL:USUARIO:actualizarContrasena:" + IDUsuarioClient + "|" + SHA.generate512(password) + "|" + SHA.generate512(passwordRepeat));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String[] cad = new String[0];
            try {
                cad = comunicationManager.leerMensaje().split(":");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return cad[1];
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Correcto")) {
                Toast.makeText(getActivity(), "Contraseña Actualizada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
