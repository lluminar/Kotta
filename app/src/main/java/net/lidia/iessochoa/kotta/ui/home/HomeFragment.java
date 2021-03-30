package net.lidia.iessochoa.kotta.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.ui.AddActivity;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;


import static android.app.Activity.RESULT_CANCELED;

public class HomeFragment extends Fragment {
    public final static int OPTION_REQUEST_NUEVA = 0;

    private HomeViewModel homeViewModel;
    private PartituraAdapter adapter;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabAdd;
    private RecyclerView rvPartituras;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        rvPartituras = root.findViewById(R.id.rvPartituras);
        bottomAppBar = root.findViewById(R.id.bottomAppBar);
        fabAdd = root.findViewById(R.id.fabAdd);
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

        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
            /*intent.putExtra(
                    AddActivity.EXTRA_PARTITURA, new Partitura(Calendar.getInstance().getTime(),
                    null,null,null,null,null));
            startActivityForResult(intent,OPTION_REQUEST_NUEVA);*/
        });

        /*rvPartituras.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPartituras.setAdapter(adapter);

        homeViewModel.getAllPartituras().observe(getViewLifecycleOwner(),partituras ->
                adapter.setListaPartituras(partituras));*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            // De lo contrario, recogemos el resultado de la segunda actividad.
            switch (requestCode) {
                case OPTION_REQUEST_NUEVA:
                    Partitura partitura = data.getExtras().getParcelable(AddActivity.EXTRA_PARTITURA_RESULT);
                    //homeViewModel.insert(partitura);
                    break;
            }
        }
    }
}