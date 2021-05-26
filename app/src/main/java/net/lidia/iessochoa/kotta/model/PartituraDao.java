package net.lidia.iessochoa.kotta.model;

import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * This class create CRUD methods to give functionality in PartituraImpl
 * @author Lidia Martínez Torregrosa
 */
public interface PartituraDao {
    Query AllPartituras();
    Query getOwnPartituras();
    Query getByCategory(String category);
    Query searchByName(String name);
    Query changeOrder();
}
