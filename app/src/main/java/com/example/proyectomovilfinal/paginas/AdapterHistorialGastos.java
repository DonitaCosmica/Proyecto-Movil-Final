package com.example.proyectomovilfinal.paginas;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.data.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Reemplazar la implementacion para usar nuestro propio tipo de dato.
 */
public class AdapterHistorialGastos extends RecyclerView.Adapter<AdapterHistorialGastos.ViewHolder> {

    private final List<DummyItem> mValues;

    public AdapterHistorialGastos(List<DummyItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarjeta_con_icono, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.mItem = mValues.get(position);

        holder.mNumeroTarjeta.setText(mValues.get(position).id);
        holder.mTituloTarjeta.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public TextView mTituloTarjeta;
        public TextView mNumeroTarjeta;
        public DummyItem mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            Log.i("AdapterHistorial", String.valueOf(view == null));
            mTituloTarjeta = (TextView) view.findViewById(R.id.titulo_tarjeta);
            mNumeroTarjeta = (TextView) view.findViewById(R.id.texto_final_tarjeta);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mTituloTarjeta.getText() + "'";
        }
    }
}