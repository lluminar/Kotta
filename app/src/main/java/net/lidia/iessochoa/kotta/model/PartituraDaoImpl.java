package net.lidia.iessochoa.kotta.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartituraDaoImpl implements PartituraDao {
    private static final String TAG = PartituraDaoImpl.class.getName();
    private ArrayList<Partitura> partituras = new ArrayList<>();
    DatabaseReference appDatabaseReference;

    @Override
    public ArrayList<Partitura> AllPartituras() {
        appDatabaseReference = FirebaseDatabase.getInstance().getReference();
        appDatabaseReference.child(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS);
        //final Query fetchAppInfoQuery = appDatabaseReference.orderByKey();
        appDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*GenericTypeIndicator <List<Partitura>> t = new GenericTypeIndicator<List<Partitura>>() {};
                partituras = dataSnapshot.getValue(t);
                mListener.onDataLoaded(partituras);*/
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Partitura partitura = new Partitura();
                        String nombre = String.valueOf(dsp.child("nombre").getValue());
                        partitura.setNombre(nombre);
                        partitura.setCategoria(String.valueOf(dsp.child(FirebaseContract.PartituraEntry.CATEGORY).getValue()));
                        partitura.setInstrumento(String.valueOf(dsp.child(FirebaseContract.PartituraEntry.INSTRUMENT).getValue()));
                        partitura.setAutor(String.valueOf(dsp.child(FirebaseContract.PartituraEntry.AUTHOR).getValue()));
                        partitura.setPdf(String.valueOf(dsp.child(FirebaseContract.PartituraEntry.PDF).getValue()));
                        partitura.setUser(String.valueOf(dsp.child(FirebaseContract.PartituraEntry.USER).getValue()));
                        //partitura.setUser(dsp.child(FirebaseContract.PartituraEntry.USER).getValue().toString());
                        partituras.add(partitura);
                        //in.add(String.valueOf(dsp.getValue())); //add result into array list
                        partituras.add(partitura);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
        return partituras;
    }
}
