package com.example.proyectomovilfinal.paginas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.DatosUsuario;
import com.example.proyectomovilfinal.data.TipoUsuario;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UsuariosAdapter extends FirestoreRecyclerAdapter<DatosUsuario, UsuariosAdapter.viewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UsuariosAdapter(@NonNull FirestoreRecyclerOptions<DatosUsuario> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position , @NonNull DatosUsuario usuarios) {

        TipoUsuario tipo = Util.getTipoFromValor(usuarios.getTipo());

        holder.textViewCorreo.setText(usuarios.getEmail());
        holder.textViewTipoUsuario.setText(String.valueOf(tipo));

        if(tipo == TipoUsuario.ADMINISTRADOR || tipo == TipoUsuario.ANALISTA) {
            holder.mbtnAscender.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_usuarios, viewGroup, false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewCorreo;
        TextView textViewTipoUsuario;
        Button mbtnAscender;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCorreo = itemView.findViewById(R.id.correo);
            textViewTipoUsuario = itemView.findViewById(R.id.tipoUsuario);
            mbtnAscender = itemView.findViewById(R.id.btnAscender);

        }
    }
}
