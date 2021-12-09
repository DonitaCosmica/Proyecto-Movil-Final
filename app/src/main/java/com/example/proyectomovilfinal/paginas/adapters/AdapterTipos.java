package com.example.proyectomovilfinal.paginas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.TipoGasto;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Reemplazar la implementacion para usar nuestro propio tipo de dato.
 */
public class AdapterTipos extends RecyclerView.Adapter<AdapterTipos.ViewHolder> {

    private final OnItemClickListener mItemClickListener;

    private final TipoGasto[] mTiposDeGasto;

    /**
     * Crea un Adapter para un RecyclerView que muestra los tipos de gastos
     * disponibles para crear un nuevo gasto.
     *
     * @param tiposDeGasto Lista de tipos de gastos que pueden crearse.
     */
    public AdapterTipos(TipoGasto[] tiposDeGasto, OnItemClickListener clickListener) {
        mTiposDeGasto = tiposDeGasto;
        mItemClickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mTiposDeGasto[position]);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tarjeta_con_icono, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() { return mTiposDeGasto.length; }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TipoGasto mTipoDeGasto;
        private final TextView mTxtTituloTarjeta;

        public ViewHolder(View view) {
            super(view);
            mTxtTituloTarjeta = view.findViewById(R.id.titulo_tarjeta);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posicion = getAbsoluteAdapterPosition();
                    if (posicion != RecyclerView.NO_POSITION && mItemClickListener != null) {
                        mItemClickListener.onItemClick(mTipoDeGasto, posicion);
                    }
                }
            });
        }

        public void bind(final TipoGasto tipoDeGasto) {
//            Resources res = itemView.getResources();

            mTipoDeGasto = tipoDeGasto;
            mTxtTituloTarjeta.setText(tipoDeGasto.toString());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TipoGasto tipoGasto, int posicion);
    }
}