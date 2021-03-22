package net.lidia.iessochoa.kotta.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.repository.PartituraRepository;

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private PartituraRepository mRepository;
    private LiveData<List<Partitura>> mAllPartituras;

    private MutableLiveData<HashMap<String,Object>> condicionBusquedaLiveData;
    private final String NAME ="name";
    private final String DATE ="date";
    private final String CATEGORY ="categoria";
    private final String INSTRUMENT ="instrument";

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mRepository= PartituraRepository.getInstance(application);
        //Recuperamos el LiveData de todos los pokemons
        mAllPartituras = mRepository.getAllPartituras();
    }

    public LiveData<List<Partitura>> getAllPartituras() {
        return mAllPartituras;
    }

    public void insert(Partitura partitura){
        mRepository.insert(partitura);
    }
}