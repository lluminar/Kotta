package net.lidia.iessochoa.kotta.ui.home;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.FirebaseContract;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.model.PartituraDao;
import net.lidia.iessochoa.kotta.model.PartituraDaoImpl;
import net.lidia.iessochoa.kotta.ui.AddActivity;
import net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment;
import net.lidia.iessochoa.kotta.ui.PDFReader;
import net.lidia.iessochoa.kotta.ui.PrincipalActivity;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;
import net.lidia.iessochoa.kotta.ui.profile.ProfileFragment;

import static android.content.Context.SEARCH_SERVICE;
import static net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment.EXTRA_DATOS_RESULTADO;

public class HomeFragment extends Fragment {

    public final static String EXTRA_PDF = "partituraPdf";

    private PartituraAdapter adapter;
    private AppCompatActivity activity;
    private RecyclerView rvPartituras;
    private PartituraDao partituraDaoImpl;
    private StorageReference mStorageReference;
    private FirebaseFirestore mDatabaseReference;
    private Query query;
    private boolean sentido = true;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        rvPartituras = root.findViewById(R.id.rvPartituras);
        bottomAppBar = root.findViewById(R.id.bottomAppBar);
        setHasOptionsMenu(true);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseFirestore.getInstance();
        fabAdd = root.findViewById(R.id.fabAdd);
        partituraDaoImpl = new PartituraDaoImpl();
        rvPartituras.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Handle navigation icon press
        bottomAppBar.setNavigationOnClickListener(v -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetNavigationFragment.newInstance();
            bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
        });

        query = partituraDaoImpl.AllPartituras();
        createAdapter(query);

        adapter.setOnCLickElementoListener((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            Intent intent = new Intent(getActivity(), PDFReader.class);
            intent.putExtra(EXTRA_PDF, partitura.getPdf());
            startActivity(intent);

            FirebaseStorage storageRef = FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Intent intent1 = new Intent(getActivity(), PDFReader.class);
                intent1.putExtra(EXTRA_PDF, uri.toString());
                startActivity(intent1);
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        });

        adapter.setListenerDownload((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            FirebaseStorage storageRef = FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadPDF(partitura.getName(), ".pdf", Environment.getExternalStorageDirectory().getPath(), uri.toString());
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        });

        adapter.setListenerOptions((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            deletePartitura(partitura,snapshot.getId());
        });
    }

    public void deletePartitura(final Partitura partitura, final  String documentId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.Aviso);
        dialog.setMessage(R.string.avisoBorrar);

        //En caso de que acepte borramos la partitura
        dialog.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            // Qué hacemos en caso ok
            StorageReference sRef = mStorageReference.child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());
            sRef.delete()
                    .addOnSuccessListener(taskSnapshot -> {
                        mDatabaseReference.collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS).document(documentId).delete();
                        Intent intent = new Intent(getContext(), PrincipalActivity.class);
                        startActivity(intent);
                        Toast.makeText(getContext(), "La partitura se ha borrado correctamente",Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(exception -> Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show());
        });
        //Si cancela no borramos el pokemon
        dialog.setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
            // Qué hacemos en caso cancel
        });
        dialog.show();
    }

    public void downloadPDF(String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getContext(), destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
    }

    public void createAdapter(Query query) {
        FirestoreRecyclerOptions<Partitura> options = new FirestoreRecyclerOptions.Builder<Partitura>()
                //consulta y clase en la que se guarda los datos
                .setQuery(query, Partitura.class).setLifecycleOwner(this).build();
        //si el usuario ya habia seleccionado otra conferencia, paramos las escucha
        if (adapter != null) adapter.stopListening();
        //Creamos el adaptador
        System.out.println("Creamos adaptador");
        adapter = new PartituraAdapter(options, getContext());

        System.out.println("adaptador creado");
        adapter.notifyDataSetChanged();

        //asignamos el adaptador
        rvPartituras.setAdapter(adapter);
        System.out.println("Envia adapter");
        //Podemos reaccionar ante cambios en la query.
        adapter.startListening();
        // Nosotros, lo que necesitamos es mover el scroll del recyclerView al inicio para ver el mensaje nuevo
        adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull
                    DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                rvPartituras.smoothScrollToPosition(0);
                rvPartituras.setAdapter(adapter);
            }

            @Override
            public void onDataChanged() {
                rvPartituras.smoothScrollToPosition(0);
                rvPartituras.setAdapter(adapter);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bottom_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ordenar: // Al dar en sentido, cambiar sentido.
                if (sentido) {
                    item.setIcon(R.drawable.avd_anim_up_down);
                    query = partituraDaoImpl.changeOrder();
                    createAdapter(query);
                }
                else {
                    item.setIcon(R.drawable.avd_anim_down_up);
                    query = partituraDaoImpl.AllPartituras();
                    createAdapter(query);
                }
                Drawable icon = item.getIcon();
                sentido = !sentido;

                if (icon.getClass() == AnimatedVectorDrawable.class)
                    ((AnimatedVectorDrawable) icon).start();
                return true;
            default: return false;
        }
    }
}