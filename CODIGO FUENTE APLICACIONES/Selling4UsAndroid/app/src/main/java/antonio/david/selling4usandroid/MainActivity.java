package antonio.david.selling4usandroid;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.Stack;
import java.util.TreeMap;

import antonio.david.selling4usandroid.Adapter.MensajeAdapter;
import antonio.david.selling4usandroid.Fragments.MiPerfilModificarFragment;
import antonio.david.selling4usandroid.Fragments.MisAnunciosFragment;
import antonio.david.selling4usandroid.Fragments.MisChatsListFragment;
import antonio.david.selling4usandroid.Fragments.MisComprasFragment;
import antonio.david.selling4usandroid.Fragments.MisFavoritosFragment;
import antonio.david.selling4usandroid.Fragments.MisOfertasFragment;
import antonio.david.selling4usandroid.Fragments.MisVentasFragment;
import antonio.david.selling4usandroid.Fragments.NewAnuncioFragment;
import antonio.david.selling4usandroid.Utils.ComunicationManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    public static int IDusuarioClient;
    public static String NombreusuarioClient;
    public static String direccionText;
    public static TreeMap<Integer, Boolean> estoyEnChat = new TreeMap<Integer, Boolean>();
    public MensajeAdapter mensajeAdapter;



    public  void setMensajeAdapter(MensajeAdapter mensajeAdapter) {
        this.mensajeAdapter = mensajeAdapter;
    }

    private Stack<Fragment> fragmentStack;
    SharedPreferences sharedPreferences;
    ComunicationManager comunicationManager = null;

    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        comunicationManager.setMainActivity(this);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentStack = new Stack<>();


        Intent intent = getIntent();
        IDusuarioClient = intent.getIntExtra("idUsuario", 0);
        NombreusuarioClient = intent.getStringExtra("NombreusuarioClient");
        direccionText = intent.getStringExtra("localizacion");

        displayFragment(new PerfilFragment(IDusuarioClient, direccionText, this));
        navigationView.setCheckedItem(R.id.menu_profile);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    abrirNewAnuncio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sharedPreferences = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("listaReturn");
        editor.apply();
        new VerNotificacionesOfflineTask().execute();

        menu = navigationView.getMenu();
    }

    private void abrirNewAnuncio() throws IOException {
        displayFragment(new NewAnuncioFragment(IDusuarioClient, NombreusuarioClient, this));
    }

    public void eliminarAnuncioListaBuffer(int id) {
        String[] listaReturn = null;
        String nueva="";
        sharedPreferences = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String listaReturnString = sharedPreferences.getString("listaReturn", null);
        if (listaReturnString != null) {
            listaReturn = listaReturnString.split(":");
            for(String anuncio : listaReturn){
                if(!anuncio.split("\\|")[0].equals(id+"")){
                    nueva+=anuncio+":";
                }
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("listaReturn", nueva);
            editor.apply();
        }
    }


    public void mostrarNotificacion(String tipo, String contenido) throws InterruptedException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tipo);
        boolean pop=true;
        switch (tipo){
            case "OFERTA":
                builder.setMessage("Has recibido una oferta de "+contenido.split("=")[0]+" por tu artículo "+contenido.split("=")[1]);
                MenuItem menuItemOfertas1 = menu.findItem(R.id.menu_ofertas);
                menuItemOfertas1.setIcon(R.drawable.alert);
                break;
            case "COMPRA":
                builder.setMessage("Has vendido tu anuncio "+contenido);
                MenuItem menuItemVentas1 = menu.findItem(R.id.menu_ventas);
                menuItemVentas1.setIcon(R.drawable.alert);
                break;
            case "OFERTA ACEPTADA":
                builder.setMessage(contenido);
                MenuItem menuItemCompras = menu.findItem(R.id.menu_compras);
                menuItemCompras.setIcon(R.drawable.alert);
                break;
            case "CHAT":
               int id=0;
                try{
                    id = Integer.parseInt(contenido.split("\\|")[0]);
                }catch (NumberFormatException n){}
                if(estoyEnChat.containsKey(id)) {
                    mensajeAdapter.addMensaje(-1 + "|" + contenido.split("\\|")[0] + "|" + contenido.split("\\|")[1] + "|" + contenido.split("\\|")[2] + "|" + contenido.split("\\|")[3]);
                    pop = false;
                }else{
                    if(!contenido.equals("Tienes un nuevo chat que atender para tu anuncio")){
                        new EnviarMensajeNoLeidoTask().execute("CL:CHAT:mensajeNoleido:"+contenido.split("\\|")[0]+"|"+contenido.split("\\|")[2]);
                        builder.setMessage("Tienes un nuevo mensaje que revisar!" );
                        MenuItem menuItemChat1 = menu.findItem(R.id.menu_chats);
                        menuItemChat1.setIcon(R.drawable.alert);
                    }
                    builder.setMessage(contenido);
                    MenuItem menuItemChat1 = menu.findItem(R.id.menu_chats);
                    menuItemChat1.setIcon(R.drawable.alert);
                }
                break;
            case "BIENVENIDO":
                if(!contenido.equals("vacia")){
                    String muestro = "Nuevos movimientos en: \n";
                    String[] tipos = contenido.split("=");
                    for(String tipoNotificacion : tipos){
                        muestro+="- "+tipoNotificacion+"\n";
                    }
                    muestro += "Por favor, revise sus notificaciones";
                    builder.setMessage(muestro);
                    if(contenido.contains("Articulos Vendidos")){
                        MenuItem menuItemVentas = menu.findItem(R.id.menu_ventas);
                        menuItemVentas.setIcon(R.drawable.alert);
                    }
                    if(contenido.contains("Oferta")){
                        MenuItem menuItemOfertas = menu.findItem(R.id.menu_ofertas);
                        menuItemOfertas.setIcon(R.drawable.alert);
                    }
                    if(contenido.contains("chat")){
                        MenuItem menuItemChat = menu.findItem(R.id.menu_chats);
                        menuItemChat.setIcon(R.drawable.alert);
                    }
                    if(contenido.contains("Bloqueo de anuncios")){
                        MenuItem menuItemAnuncios = menu.findItem(R.id.menu_anuncios);
                        menuItemAnuncios.setIcon(R.drawable.alert);
                    }
                }else{
                    builder.setMessage("No tienes nada pendiente, distruta de tus compras!");
                }
                break;
            case "BLOQUEO DE ANUNCIO":
                MenuItem menuItemAnuncios = menu.findItem(R.id.menu_anuncios);
                menuItemAnuncios.setIcon(R.drawable.alert);
                builder.setMessage("Se ha bloqueado tu anuncio "+contenido.split("=")[2]+"\n Dentro de la normativa "+contenido.split("=")[0]+": "+contenido.split("=")[1]);
                break;
        }
        builder.setPositiveButton("Aceptar", null);
        if(pop){
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        if (R.id.menu_profile == id) {
            displayFragment(new PerfilFragment(IDusuarioClient, direccionText, this));
        }
        if (R.id.menu_anuncios == id) {
            displayFragment(new MisAnunciosFragment(IDusuarioClient, this));
            item.setIcon(null);
        }
        if (R.id.menu_ventas == id) {
            displayFragment(new MisVentasFragment(IDusuarioClient, this));
            item.setIcon(null);
        }
        if (R.id.menu_ofertas == id) {
            displayFragment(new MisOfertasFragment(IDusuarioClient));
            item.setIcon(null);
        }
        if (R.id.menu_chats == id) {
            FloatingActionButton fabAdd = findViewById(R.id.fab_add);
            fabAdd.setVisibility(View.GONE);
            displayFragment(new MisChatsListFragment(IDusuarioClient, this));
            item.setIcon(null);
        }
        if(R.id.menu_compras == id){
            displayFragment(new MisComprasFragment(IDusuarioClient, this));
            item.setIcon(null);
        }
        if(R.id.menu_favoritos == id){
            displayFragment(new MisFavoritosFragment(IDusuarioClient, this));
        }
        if(R.id.menu_miperfil == id){
            displayFragment(new MiPerfilModificarFragment(NombreusuarioClient, IDusuarioClient));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    private void displayFragment(Fragment fragment) {
        fragmentStack.push(fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        displayFragment(new PerfilFragment(IDusuarioClient, direccionText, this));
        estoyEnChat.clear();
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        System.out.println("cierrooooooo");
        try {
            comunicationManager.enviarMensaje("CL:" + "USUARIO:CerrarConexionMovil:" + IDusuarioClient);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }


    private class VerNotificacionesOfflineTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            String[] lista = null;
            try {
                comunicationManager.enviarMensaje("CL:" + "USUARIO:getMisNotificacionesOffline:" + IDusuarioClient);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return lista;
        }

        @Override
        protected void onPostExecute(String[] result) {
        }
    }

    private class EnviarMensajeNoLeidoTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String mensaje = params[0];
            try {
                comunicationManager.enviarMensaje(mensaje);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

}
