package net.lidia.iessochoa.kotta.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.model.PartituraDao;
import net.lidia.iessochoa.kotta.model.PartituraDaoImpl;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;

public class ProfileFragment extends Fragment {

    private AppCompatActivity activity;
    private PartituraAdapter adapter;
    private PartituraDao partituraDaoImpl;
    private RecyclerView rvPartituras;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        partituraDaoImpl= new PartituraDaoImpl();
        rvPartituras = root.findViewById(R.id.rvPartituras);
        rvPartituras.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createAdapter();
    }

    private void createAdapter() {
        Query query = partituraDaoImpl.getOwnPartituras();
        FirestoreRecyclerOptions<Partitura> options = new FirestoreRecyclerOptions.Builder<Partitura>()
                //consulta y clase en la que se guarda los datos
                .setQuery(query, Partitura.class).setLifecycleOwner(this).build();
        //si el usuario ya habia seleccionado otra conferencia, paramos las escucha
        if (adapter != null) adapter.stopListening();
        //Creamos el adaptador
        adapter = new PartituraAdapter(options, getContext());
        //asignamos el adaptador
        rvPartituras.setAdapter(adapter);
        //comenzamos a escuchar. Normalmente solo tenemos un adaptador, esto tenemos que
        adapter.startListening();
        // hacerlo en el evento onStar, como indica la documentación
        //Podemos reaccionar ante cambios en la query(se añade un mensaje).
        // Nosotros, lo que necesitamos es mover el scroll del recyclerView al inicio para ver el mensaje nuevo
        adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull
                    DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                rvPartituras.smoothScrollToPosition(0);
            }

            @Override
            public void onDataChanged() {
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        }
    }
}