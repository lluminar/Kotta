package net.lidia.iessochoa.kotta.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartituraDaoImpl implements PartituraDao {
    private static final String TAG = PartituraDaoImpl.class.getName();
    private ArrayList<Partitura> partituras = new ArrayList<>();
    DatabaseReference appDatabaseReference;

    @Override
    public Query AllPartituras() {
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }
}
