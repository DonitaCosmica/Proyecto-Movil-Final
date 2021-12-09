package com.example.proyectomovilfinal.paginas.adapters;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.Gasto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * {@link RecyclerView.Adapter} que muestra una lista de {@link Gasto}.
 */
public class AdapterHistorialGastos extends FirestoreRecyclerAdapter<Gasto, AdapterHistorialGastos.ViewHolder> {

    private OnItemClickListener mListener;

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
                    .inflate(R.layout.tarjeta_gasto, parent, false);
        return new ViewHolder(view);
    }

    public void eliminarGasto(int posicion) {
        getSnapshots().getSnapshot(posicion).getReference().delete();
        notifyItemRemoved(posicion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mTituloTarjeta;
        private final TextView mSubtituloTarjeta;
        private final TextView mDescripcionTarjeta;
        private final TextView mTextoFinalTarjeta;
        private final ImageView mIconoTarjeta;

        public ViewHolder(View view) {
            super(view);

            mTituloTarjeta = view.findViewById(R.id.titulo_tarjeta);
            mSubtituloTarjeta = view.findViewById(R.id.subtitulo_tarjeta);
            mDescripcionTarjeta = view.findViewById(R.id.descripcion_tarjeta);
            mTextoFinalTarjeta = view.findViewById(R.id.texto_final_tarjeta);
            mIconoTarjeta = view.findViewById(R.id.icono_tarjeta);

            view.setOnClickListener(itemView -> {
                int posicion = getAbsoluteAdapterPosition();

                if (posicion != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onItemClick(getSnapshots().getSnapshot(posicion), posicion);
                }
            });
        }

        public void bind(final Gasto gasto) {

            Resources res = itemView.getResources();

            String fechaConFormato = Util.fFechaHora.format(gasto.getFecha());
            String cantidadConFormato = Util.fDinero.format(gasto.getCantidad());

            mTituloTarjeta.setText("Subcategoria");
            mSubtituloTarjeta.setText(fechaConFormato);
            mDescripcionTarjeta.setText(gasto.getDescripcion());
            mTextoFinalTarjeta.setText(cantidadConFormato);

            Drawable unwrappedDrawable = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.ic_baseline_attach_money_24);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);

            int color = 0;
            switch (gasto.getTipo()) {
                case NECESARIO:
                    color = R.color.orange_500;
                    break;
                case ENTRETENIMIENTO:
                    color = R.color.green_800;
                    break;
                case EXTRA:
                    color = R.color.blue_500;
                    break;
                default:
                    color = R.color.black;
            }

            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(itemView.getContext(), color));
            mIconoTarjeta.setImageDrawable(wrappedDrawable);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documento, int posicion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}