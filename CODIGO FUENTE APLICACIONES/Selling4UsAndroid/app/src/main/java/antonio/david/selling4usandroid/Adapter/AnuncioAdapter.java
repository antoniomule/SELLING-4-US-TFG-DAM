package antonio.david.selling4usandroid.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import antonio.david.selling4usandroid.Utils.ComunicationManager;
import antonio.david.selling4usandroid.Fragments.DetallesAnuncioFragment;
import antonio.david.selling4usandroid.Fragments.MisAnunciosFragment;
import antonio.david.selling4usandroid.Fragments.MisFavoritosFragment;
import antonio.david.selling4usandroid.MainActivity;
import antonio.david.selling4usandroid.R;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private String[] anuncioList;
    private FragmentManager fragmentManager;
    private int IDusuarioClient;
    private ComunicationManager comunicationManager;
    private int IDAnuncio;
    private boolean isFavorito;
    private boolean isMio;
    private boolean isVendido;

    private MainActivity mainActivity;
    public AnuncioAdapter(String[] anuncios, FragmentManager fragmentManager, int IDusuarioClient, MainActivity main) {
        this.anuncioList = anuncios;
        this.fragmentManager = fragmentManager;
        this.IDusuarioClient = IDusuarioClient;
        this.isFavorito=false;
        this.isMio=false;
        this.isVendido=false;
        try {
            comunicationManager = new ComunicationManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.mainActivity=main;
    }

    public boolean isFavorito() {
        return isFavorito;
    }

    public void setFavorito(boolean favorito) {
        isFavorito = favorito;
    }

    public boolean isMio() {
        return isMio;
    }

    public void setMio(boolean mio) {
        isMio = mio;
    }

    public boolean isVendido() {
        return isVendido;
    }

    public void setVendido(boolean vendido) {
        isVendido = vendido;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_anuncio, parent, false);
        return new AnuncioViewHolder(view);
    }
    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        String anuncio = anuncioList[position];
        holder.idAnuncio = Integer.parseInt(anuncio.split("\\|")[0]);
        IDAnuncio = holder.idAnuncio;
        holder.nombreTextView.setText(anuncio.split("\\|")[1]);
        holder.detallesTextView.setText(anuncio.split("\\|")[2]);
        holder.precioTextView.setText(anuncio.split("\\|")[3]);
        holder.ciudad = anuncio.split("\\|")[4];
        holder.imageView.setImageBitmap(byteArrayToBitmap(Base64.getDecoder().decode(anuncio.split("\\|")[5])));
        holder.estado = anuncio.split("\\|")[6];
        holder.idPropietario = Integer.parseInt(anuncio.split("\\|")[8]);
        holder.nombrePropietario = anuncio.split("\\|")[9];
        holder.categoria = anuncio.split("\\|")[10];
        holder.faltaPago = "SinDatosPago";
        try {
            if (anuncio.split("\\|")[11] != null) {
                holder.faltaPago = anuncio.split("\\|")[11];
            }
        } catch (ArrayIndexOutOfBoundsException ex){}

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetallesAnuncioFragment detallesAnuncioFragment = null;
                try {
                    detallesAnuncioFragment = new DetallesAnuncioFragment(IDusuarioClient, holder.idAnuncio, holder.nombreTextView.getText().toString(), holder.detallesTextView.getText().toString(), holder.precioTextView.getText().toString(), holder.ciudad, Base64.getDecoder().decode(anuncio.split("\\|")[5]), holder.estado, holder.idPropietario, holder.nombrePropietario, holder.categoria, holder.faltaPago, mainActivity);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, detallesAnuncioFragment);
                fragmentTransaction.commit();
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                if(isMio){
                    builder.setMessage("Eres propietario de este Anuncio, ¿Deseas eliminarlo?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EliminarAnuncioTask eliminarAnuncioTask = new EliminarAnuncioTask();
                                    eliminarAnuncioTask.setIdAnuncio(holder.idAnuncio);
                                    eliminarAnuncioTask.execute();

                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                }else if(isFavorito){
                    builder.setMessage("¿Deseas eliminarlo de favoritos?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    EliminarFavoritoTask eliminarFavoritoTask = new EliminarFavoritoTask();
                                    eliminarFavoritoTask.setIdAnuncio(holder.idAnuncio);
                                    eliminarFavoritoTask.execute();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                } else if (isVendido) {

                } else{
                    builder.setMessage("¿Deseas añadir"+holder.nombreTextView.getText()+" a favoritos?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    AñadirFavoritoTask añadirFavoritoTask = new AñadirFavoritoTask();
                                    añadirFavoritoTask.setIdAnuncio(holder.idAnuncio);
                                    añadirFavoritoTask.execute();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                }
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return anuncioList.length;
    }

    public class AnuncioViewHolder extends RecyclerView.ViewHolder{

        int idAnuncio;
        String estado;
        String ciudad;
        int idPropietario;
        String nombrePropietario;
        String categoria;

        ImageView imageView;
        TextView nombreTextView;
        TextView detallesTextView;
        TextView precioTextView;

        //Solo se utiliza cuando el propietario acepta la oferta, y el ofertante aún no ha pagado
        String faltaPago;

        public AnuncioViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_anuncio);
            nombreTextView = itemView.findViewById(R.id.text_nombre);
            detallesTextView = itemView.findViewById(R.id.text_detalles);
            precioTextView = itemView.findViewById(R.id.text_precio);
        }
    }

    private class EliminarAnuncioTask extends AsyncTask<Void, Void, Boolean> {

        int idAnuncio;
        public void setIdAnuncio(int idAnuncio) {
            this.idAnuncio = idAnuncio;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return eliminarAnuncio(idAnuncio);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if(resultado){
                MisAnunciosFragment misAnunciosFragment =  new MisAnunciosFragment(IDusuarioClient, mainActivity);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, misAnunciosFragment);
                fragmentTransaction.commit();
            }else{


            }
        }
    }

    private boolean eliminarAnuncio(int idAnuncio) throws InterruptedException {
        boolean eliminado;
        comunicationManager.enviarMensaje("CL:" + "ANUNCIO:eliminarAnuncio:" + idAnuncio);
        if (comunicationManager.leerMensaje().split(":")[1].equals("Correcto")) {
            eliminado = true;
        } else {
            eliminado = false;
        }
        return eliminado;
    }

    private class EliminarFavoritoTask extends AsyncTask<Void, Void, Boolean> {

        int idAnuncio;

        public void setIdAnuncio(int idAnuncio) {
            this.idAnuncio = idAnuncio;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return eliminarFavorito(idAnuncio);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if(resultado){
                MisFavoritosFragment misFavoritosFragment =  new MisFavoritosFragment(IDusuarioClient, mainActivity);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, misFavoritosFragment);
                fragmentTransaction.commit();
            }else{


            }
        }
    }

    private boolean eliminarFavorito( int idAnuncio) throws InterruptedException {
        boolean eliminado;
        comunicationManager.enviarMensaje("CL:" + "ANUNCIO:eliminarFavorito:" + idAnuncio+"|"+IDusuarioClient);
        if (comunicationManager.leerMensaje().split(":")[1].equals("Correcto")) {
            eliminado = true;
        } else {
            eliminado = false;
        }
        return eliminado;
    }


    private class AñadirFavoritoTask extends AsyncTask<Void, Void, Boolean> {

        int idAnuncio;

        public void setIdAnuncio(int idAnuncio) {
            this.idAnuncio = idAnuncio;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return añadirFavorito(idAnuncio);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if(resultado){
                MisFavoritosFragment misFavoritosFragment =  new MisFavoritosFragment(IDusuarioClient, mainActivity);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, misFavoritosFragment);
                fragmentTransaction.commit();
            }else{


            }
        }
    }

    private boolean añadirFavorito(int idAnuncio) throws InterruptedException {
        boolean añadido;
        comunicationManager.enviarMensaje("CL:" + "ANUNCIO:anadirFavorito:" + idAnuncio+"|"+IDusuarioClient);
        if (comunicationManager.leerMensaje().split(":")[1].equals("Correcto")) {
            añadido = true;
        } else {
            añadido = false;
        }
        return añadido;
    }

}
