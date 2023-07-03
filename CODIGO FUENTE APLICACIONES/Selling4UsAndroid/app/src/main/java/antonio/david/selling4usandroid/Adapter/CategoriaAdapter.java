package antonio.david.selling4usandroid.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.widget.Toast;

import antonio.david.selling4usandroid.PerfilFragment;
import antonio.david.selling4usandroid.R;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private String[] categoriaList;
    private Context context;
    private PerfilFragment perfilFragment;

    public CategoriaAdapter(Context context, String[] categorias,PerfilFragment fragment) {
        this.context = context;
        this.categoriaList = categorias;
        this.perfilFragment = fragment;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        String categoria = categoriaList[position];
        holder.categoriaTextView.setText(categoria);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama al método en PerfilFragment para actualizar y mostrar los anuncios
                perfilFragment.mostrarAnunciosPorCategoria(categoria);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoriaList.length;
    }

    public class CategoriaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoriaTextView;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            categoriaTextView = itemView.findViewById(R.id.text_nombre_categoria);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String categoria = categoriaList[position];
                Toast.makeText(context, "Click en la categoría: " + categoria, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

