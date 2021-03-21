package net.lidia.iessochoa.kotta.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.model.PartituraDao;
import net.lidia.iessochoa.kotta.model.PartituraDatabase;

import java.util.List;

public class PartituraRepository {
    private static volatile PartituraRepository INSTANCE;

    private PartituraDao mPartituraDao;
    private LiveData<List<Partitura>> mAllPartituras;

    //Singleton
    public static PartituraRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (PartituraRepository.class) {
                if (INSTANCE == null)
                    INSTANCE = new PartituraRepository(application);
            }
        }
        return INSTANCE;
    }

    public PartituraRepository(Application application) {
        PartituraDatabase db = PartituraDatabase.getDatabase(application);
        mPartituraDao = db.partituraDao();
        mAllPartituras = mPartituraDao.getAllPartituras();
    }

    public LiveData<List<Partitura>> getAllPartituras(){ return mAllPartituras; }

    public void insert(Partitura partitura){
        //administramos el hilo con el Executor
        PartituraDatabase.databaseWriteExecutor.execute(() -> {
            mPartituraDao.insert(partitura);
        });
    }
}
