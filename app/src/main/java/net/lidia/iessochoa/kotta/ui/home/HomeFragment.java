package net.lidia.iessochoa.kotta.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.model.PartituraDao;
import net.lidia.iessochoa.kotta.model.PartituraDaoImpl;
import net.lidia.iessochoa.kotta.ui.AddActivity;
import net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;


import static android.app.Activity.RESULT_CANCELED;
import static net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment.EXTRA_DATOS_RESULTADO;

public class HomeFragment extends Fragment {

    private String result;
    private PartituraAdapter adapter;
    private AppCompatActivity activity;
    private RecyclerView rvPartituras;
    private PartituraDao partituraDaoImpl;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        rvPartituras = root.findViewById(R.id.rvPartituras);
        bottomAppBar = root.findViewById(R.id.bottomAppBar);
        fabAdd = root.findViewById(R.id.fabAdd);
        partituraDaoImpl = new PartituraDaoImpl();
        rvPartituras.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        //Handle navigation icon press
        bottomAppBar.setNavigationOnClickListener(v -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetNavigationFragment.newInstance();
            bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
        });

        createAdapter();

        adapter.setOnCLickElementoListener((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            String id = snapshot.getId();
        });
    }

    private void createAdapter() {
        Query query = partituraDaoImpl.AllPartituras();
        if (result!=null) {
            switch (result) {
                case "Rock":
                    query = partituraDaoImpl.getByCategory("Rock");
                    break;
                case "Pop":
                    query = partituraDaoImpl.getByCategory("Pop");
                    break;
                case "3":
                    query = partituraDaoImpl.getByCategory("Clásica");
                    break;
                case "fre":
                    query = partituraDaoImpl.getByCategory("Videojuegos");
                    break;
                case "5":
                    query = partituraDaoImpl.getByCategory("Película");
                    break;
                case "6":
                    query = partituraDaoImpl.getByCategory("Baladas");
                    break;
            }
        }
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
        // hacerlo en el evento onStar, como indica la documentación
        adapter.startListening();
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
        rvPartituras.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.bottom_app_bar, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            // De lo contrario, recogemos el resultado de la segunda actividad.
            Bundle bundle = getArguments();
            if (bundle != null)
                result = bundle.getString(EXTRA_DATOS_RESULTADO);
        }
    }
}