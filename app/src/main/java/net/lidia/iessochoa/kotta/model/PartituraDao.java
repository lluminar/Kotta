package net.lidia.iessochoa.kotta.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PartituraDao {

    /**
     * Insert a music sheet
     * @param partitura: Partitura to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Partitura partitura);

    /**
     * Method to get all list of music sheet
     * @return: The music sheet list
     */
    @Query("SELECT * FROM "+Partitura.TABLE_NAME+" ORDER BY "+Partitura.NOMBRE)
    LiveData<List<Partitura>> getAllPartituras();
}
