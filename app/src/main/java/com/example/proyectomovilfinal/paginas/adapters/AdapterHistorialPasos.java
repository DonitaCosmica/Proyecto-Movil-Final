package com.example.proyectomovilfinal.paginas.adapters;

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
import com.example.proyectomovilfinal.data.Pasos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * {@link RecyclerView.Adapter} que muestra una lista de {@link Pasos}.
 */
public class AdapterHistorialPasos extends FirestoreRecyclerAdapter<Pasos, AdapterHistorialPasos.ViewHolder> {

    /**
     * Crear un nuevo RecyclerView que escucha a un Query de Firestore.  Vea {@link
     * FirestoreRecyclerOptions} para opciones de configuracion.
     *
     * @param options
     */
    public AdapterHistorialPasos(@NonNull FirestoreRecyclerOptions<Pasos> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Pasos pasos) {
        holder.bind(pasos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_pasos, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mTituloTarjeta;
        private final TextView mTextoFinalTarjeta;
        private final ImageView mIconoTarjeta;

        public ViewHolder(View view) {
            super(view);

            mTituloTarjeta = view.findViewById(R.id.titulo_tarjeta);
            mTextoFinalTarjeta = view.findViewById(R.id.texto_final_tarjeta);
            mIconoTarjeta = view.findViewById(R.id.icono_tarjeta);
        }

        public void bind(final Pasos pasos) {

            int colorTxtContenido = ContextCompat.getColor(itemView.getContext(), R.color.blue_gray_50);

            String fechaConFormato = Util.fFecha.format(pasos.getFecha());
            String cantidadPasos = Util.fCantidad.format(pasos.getCantidad());

            mTituloTarjeta.setText(fechaConFormato);
            mTextoFinalTarjeta.setText(cantidadPasos);

            mTituloTarjeta.setTextColor(colorTxtContenido);
            mTextoFinalTarjeta.setTextColor(colorTxtContenido);

            Drawable unwrappedDrawable = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.ic_baseline_accessibility_new_24);

            if (unwrappedDrawable != null) {
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);

                DrawableCompat.setTint(wrappedDrawable, colorTxtContenido);
                mIconoTarjeta.setImageDrawable(wrappedDrawable);
            }

        }
    }
}