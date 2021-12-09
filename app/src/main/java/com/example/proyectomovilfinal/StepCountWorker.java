package com.example.proyectomovilfinal;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.proyectomovilfinal.data.Pasos;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class StepCountWorker extends Worker {

    private static final String TAG = "StepCountWorker";

    private final Context mContext;
    private final FitnessOptions mFitnessOptions;

    private Pasos mRegistroDePasos;
    private boolean mSuccess = true;
    private int mStepCount = -1;

    public StepCountWorker(
        @NonNull Context context,
        @NonNull WorkerParameters params
    )
    {
        super(context, params);
        mContext = context;

        mFitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();
    }

    @NonNull
    @Override
    public Result doWork() {

        String idUsuario = getInputData().getString(Util.KEY_ARG_ID_USUARIO);

        GoogleSignInAccount googleAccount = GoogleSignIn
                .getAccountForExtension(mContext, mFitnessOptions);

        // Si no hay un ID de usuario, no se puede completar el trabajo.
        if (idUsuario == null || idUsuario.isEmpty()) {
            Log.w(TAG, "No se pudo obtener el ID del usuario.");
            return Result.failure();
        }

        Fitness.getHistoryClient(mContext, googleAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {

                    if (dataSet.isEmpty()) {
                        mStepCount = 0;
                    }
                    else {
                        mStepCount = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    }

                    Log.i(TAG, "Pasos totales el dia de hoy: " + mStepCount);

                    updateStepCount(mStepCount, idUsuario);
                })
                .addOnFailureListener(e -> {
                    mSuccess = false;
                    Log.w(TAG, "No se pudo obtener el total de pasos.", e);
                });

        return mSuccess ? Result.success() : Result.failure();
    }

    /**
     * Obtiene la cuenta de pasos del sensor STEP_COUNTER y crea un
     * nuevo registro de pasos con ella.
     *
     * @param idUsuario el ID del usuario autenticado, asociado con el registro de pasos.
     * @return el registro de pasos, con la fecha actual, el usuario autenticado y la cantidad de pasos.
     */
    private void updateStepCount(int stepCount, String idUsuario) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Date hoy = Util.obtenerFechaActual();

        firestore.collection(Pasos.NOMBRE_COLECCION_FIRESTORE)
                .whereEqualTo(Pasos.CAMPO_ID_USUARIO, idUsuario)
                .whereGreaterThanOrEqualTo(Pasos.CAMPO_FECHA,hoy)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Aun no hay pasos registrados para el dia de hoy.
                    if (queryDocumentSnapshots.isEmpty()) {
                        mRegistroDePasos = new Pasos(idUsuario, new Date(), stepCount);

                        firestore.collection(Pasos.NOMBRE_COLECCION_FIRESTORE)
                                .add(mRegistroDePasos.asDoc())
                                .addOnSuccessListener(documentReference -> {
                                    mSuccess = true;
                                    Log.i(TAG, "Nuevo registro de pasos creado.");
                                })
                                .addOnFailureListener(e -> {
                                    mSuccess = false;
                                    Log.w(TAG, "Error creando registro de pasos.", e);
                                });
                    }
                    else {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String idRegistro = doc.getId();
                        mRegistroDePasos = Pasos.fromDoc(doc);
                        mRegistroDePasos.setCantidad(stepCount);

                        firestore.collection(Pasos.NOMBRE_COLECCION_FIRESTORE).document(idRegistro)
                                .set(mRegistroDePasos.asDoc())
                                .addOnSuccessListener(documentReference -> {
                                    mSuccess = true;
                                    Log.i(TAG, "Pasos actualizados.");
                                })
                                .addOnFailureListener(e -> {
                                    mSuccess = false;
                                    Log.w(TAG, "Error actualizando pasos.", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    mSuccess = false;
                    Log.w(TAG, "Error obteniendo registro de pasos", e);
                });
    }
}
