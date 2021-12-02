package com.example.proyectomovilfinal.paginas.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.DummyContent.DummyItem;
import com.example.proyectomovilfinal.data.Gasto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Reemplazar la implementacion para usar nuestro propio tipo de dato.
 */
public class AdapterHistorialGastos extends FirestoreRecyclerAdapter<Gasto, AdapterHistorialGastos.ViewHolder> {

    /**
     * Crear un nuevo RecyclerView que escucha a un Query de Firestore.  Vea {@link
     * FirestoreRecyclerOptions} para opciones de configuracion.
     *
     * @param options
     */
    public AdapterHistorialGastos(@NonNull FirestoreRecyclerOptions<Gasto> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Gasto gasto) {
        holder.bind(gasto);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tarjeta_con_icono, parent, false);
        return new ViewHolder(view);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTituloTarjeta;
        private TextView mSubtituloTarjeta;
        private TextView mTextoFinalTarjeta;
        private ImageView mIconoTarjeta;

        public ViewHolder(View view) {
            super(view);
            mTituloTarjeta = view.findViewById(R.id.titulo_tarjeta);
            mSubtituloTarjeta = view.findViewById(R.id.subtitulo_tarjeta);
            mTextoFinalTarjeta = view.findViewById(R.id.texto_final_tarjeta);
            mIconoTarjeta = view.findViewById(R.id.icono_tarjeta);
        }

        public void bind(final Gasto gasto) {

            Resources res = itemView.getResources();

            mTituloTarjeta.setText(gasto.getDescripcion());
            mSubtituloTarjeta.setText(gasto.getFecha().toString());
            mTextoFinalTarjeta.setText("$" + gasto.getCantidad());
//            mIconoTarjeta.setImageResource(res.getDrawable(R.drawable.ic_baseline_check_24, ));
        }
    }
}