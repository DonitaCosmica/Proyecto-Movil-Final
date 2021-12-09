package com.example.proyectomovilfinal.paginas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyectomovilfinal.R;
import com.example.proyectomovilfinal.Util;
import com.example.proyectomovilfinal.data.Subcategoria;
import com.example.proyectomovilfinal.data.TipoGasto;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Crea un dialogo para agregar una nueva categoria.
 */
public class AgregarCategoriaFragment extends DialogFragment {

    private static final String TAG = "AgregarCategoriaFragment";

    private EditText mEditNombreCategoria;

    private String mIdUsuario;
    private TipoGasto mTipoGasto;

    private FirebaseFirestore mFirestore;

    public AgregarCategoriaFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AgregarCategoriaFragment.
     */
    public static AgregarCategoriaFragment newInstance(String idUsuario, int tipoDeSubcategoria) {
        AgregarCategoriaFragment fragment = new AgregarCategoriaFragment();
        Bundle args = new Bundle();
        args.putString(Util.KEY_ARG_ID_USUARIO, idUsuario);
        args.putInt(Util.TIPO_CATEGORIA, tipoDeSubcategoria);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_agregar_categoria, null);

        // Obtiene el id del gasto de los argumentos recibidos.
        if (getArguments() != null) {
            mIdUsuario = getArguments().getString(Util.KEY_ARG_ID_USUARIO);
            mTipoGasto = Util.getTipoDeGasto(getArguments().getInt(Util.TIPO_CATEGORIA));
        }

        mFirestore = FirebaseFirestore.getInstance();

        mEditNombreCategoria = dialogView.findViewById(R.id.txt_edit_nueva_categoria);

        builder.setView(dialogView).setMessage(getString(R.string.dialog_agregar_subcategoria));

        Button btnAgregarCategoria = dialogView.findViewById(R.id.btn_crear_categoria);
        btnAgregarCategoria.setOnClickListener(view -> agregarCategoriaEnFirestore());

        Button btnCancelarCategoria = dialogView.findViewById(R.id.btn_cancelar_categoria);
        btnCancelarCategoria.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    private void agregarCategoriaEnFirestore() {

        String nombreSubcategoria = mEditNombreCategoria.getText().toString();

        if (nombreSubcategoria.length() <= 0) {
            mEditNombreCategoria.setError(getString(R.string.err_subcategoria_vacia));
            return;
        }

        if (nombreSubcategoria.length() > 40) {
            mEditNombreCategoria.setError(getString(R.string.err_subcategoria_extensa));
            return;
        }

        Subcategoria nuevaCategoria = new Subcategoria(
            mIdUsuario,
            nombreSubcategoria,
            mTipoGasto
        );

        mFirestore.collection(Subcategoria.NOMBRE_COLECCION_FIRESTORE)
            .add(nuevaCategoria.asDoc())
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    dismiss();
                }
            });
    }
}