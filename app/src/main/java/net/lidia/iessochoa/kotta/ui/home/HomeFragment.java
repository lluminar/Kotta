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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.ui.AddActivity;
import net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;


import static android.app.Activity.RESULT_CANCELED;

public class HomeFragment extends Fragment {
    public final static int OPTION_REQUEST_NUEVA = 0;

    private HomeViewModel homeViewModel;
    private PartituraAdapter adapter;
    private AppCompatActivity activity;

    FirebaseFirestore firebaseFirestore;
    private RecyclerView rvPartituras;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        rvPartituras = root.findViewById(R.id.rvPartituras);
        bottomAppBar = root.findViewById(R.id.bottomAppBar);
        fabAdd = root.findViewById(R.id.fabAdd);
        firebaseFirestore = FirebaseFirestore.getInstance();
        /*homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        adapter = new PartituraAdapter();


        homeViewModel.getAllPartituras().observe(this.getViewLifecycleOwner(),
                partituras -> adapter.setListaPartituras(partituras));*/
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

        /*rvPartituras.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPartituras.setAdapter(adapter);

        homeViewModel.getAllPartituras().observe(getViewLifecycleOwner(),partituras ->
                adapter.setListaPartituras(partituras));*/
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
            switch (requestCode) {
                case OPTION_REQUEST_NUEVA:
                    break;
            }
        }
    }
}