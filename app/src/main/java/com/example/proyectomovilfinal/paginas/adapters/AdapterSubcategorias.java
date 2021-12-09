package com.example.proyectomovilfinal.paginas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.Subcategoria;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdapterSubcategorias extends FirestoreRecyclerAdapter<Subcategoria, AdapterSubcategorias.ViewHolder> {

    private OnItemClickListener mListener;

    /**
     * Crear un nuevo RecyclerView que escucha a un Query de Firestore.  Vea {@link
     * FirestoreRecyclerOptions} para opciones de configuracion.
     *
     * @param opciones Lista de subcategorias creadas por el usuario.
     */
    public AdapterSubcategorias(@NonNull FirestoreRecyclerOptions<Subcategoria> opciones) {
        super(opciones);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Subcategoria subcategoria) {
        holder.bind(subcategoria);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tarjeta_con_icono, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mTxtNombreSubcategoria;

        public ViewHolder(View view) {
            super(view);
            mTxtNombreSubcategoria = view.findViewById(R.id.titulo_tarjeta);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posicion = getAbsoluteAdapterPosition();
                    if (posicion != RecyclerView.NO_POSITION && mListener != null) {
                        mListener.onItemClick(getSnapshots().getSnapshot(posicion), posicion);
                    }
                }
            });
        }

        public void bind(final Subcategoria subcategoria) {
//            Resources res = itemView.getResources();

            mTxtNombreSubcategoria.setText(subcategoria.getNombre());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documento, int posicion);
    }

    public void setOnItemClickListener(AdapterSubcategorias.OnItemClickListener listener) {
        mListener = listener;
    }
}