package net.lidia.iessochoa.kotta.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * This class implements PartituraDao to give functionality to the methods to perform the queries
 * @author Lidia Martínez Torregrosa
 */
public class PartituraDaoImpl implements PartituraDao {
    private FirebaseAuth mAuth;

    /**
     * Method that returns query to get all scores
     * @return: The query to get all scores
     */
    @Override
    public Query AllPartituras() {
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }

    /**
     * Method that returns query to get scores uplouded by registered user
     * @return: Query to get own scores
     */
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

    /**
     * Method that return query to get scores that belonging to a category
     * @param category: The category to search
     * @return: The query to get scores belonging to a category
     */
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

    /**
     * Method that returns query to get score that has the same name that user search
     * @param name: The name of score to search
     * @return: The query to get scores with the name to search
     */
    @Override
    public Query searchByName(String name) {
        String nombre = name.toUpperCase();
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                .whereEqualTo(FirebaseContract.PartituraEntry.NAME, nombre)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.DESCENDING);
        return query;
    }

    /**
     * Method to get query to change order from descendent to ascendent
     * @return: The query
     */
    @Override
    public Query changeOrder() {
        Query query = FirebaseFirestore.getInstance()
                //coleccion conferencias
                .collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS)
                //obtenemos la lista ordenada por fecha
                .orderBy(FirebaseContract.PartituraEntry.DATE, Query.Direction.ASCENDING);
        return query;
    }
}
