package net.lidia.iessochoa.kotta.model;

import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public interface PartituraDao {
    Query AllPartituras();
    Query getOwnPartituras();
    Query getByCategory(String category);
    Query searchByName(String name);
}
