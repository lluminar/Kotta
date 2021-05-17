package net.lidia.iessochoa.kotta.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class PartituraDaoImpl implements PartituraDao {
    private static final String TAG = PartituraDaoImpl.class.getName();
    private ArrayList<Partitura> partituras = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    public Query AllPartituras() {
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }

    @Override
    public Query getOwnPartituras() {
        mAuth = FirebaseAuth.getInstance();
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                .whereEqualTo(FirebaseContract.PartituraEntry.USER, mAuth.getCurrentUser().getEmail())
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }

    @Override
    public Query getByCategory(String category) {
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                .whereEqualTo(FirebaseContract.PartituraEntry.CATEGORY, category)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }

    @Override
    public Query searchByName(String name) {
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                .whereEqualTo(FirebaseContract.PartituraEntry.NAME, name)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }
}
