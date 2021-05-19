package net.lidia.iessochoa.kotta.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.FirebaseContract;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.model.PartituraDao;
import net.lidia.iessochoa.kotta.model.PartituraDaoImpl;
import net.lidia.iessochoa.kotta.ui.PDFReader;
import net.lidia.iessochoa.kotta.ui.PrincipalActivity;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;

import static net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment.EXTRA_DATOS_RESULTADO;
import static net.lidia.iessochoa.kotta.ui.home.HomeFragment.EXTRA_PDF;

public class Filters extends AppCompatActivity {
    private PartituraAdapter adapter;
    private RecyclerView rvPartituras;
    private String result;
    private Toolbar toolbar;
    private PartituraDao partituraDaoImpl;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        rvPartituras = findViewById(R.id.rvFilters);
        rvPartituras.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbarAdd);

        partituraDaoImpl = new PartituraDaoImpl();
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });
        Bundle datosRecuperados = this.getIntent().getExtras();
        if (datosRecuperados != null) {
            System.out.println(datosRecuperados);
            result = datosRecuperados.getString(EXTRA_DATOS_RESULTADO);
            System.out.println("resultado:" + result);
        }

        if (result != null) {
            switch (result) {
                case "Rock":
                    query = partituraDaoImpl.getByCategory(getString(R.string.Rock));
                    break;
                case "Pop":
                    query = partituraDaoImpl.getByCategory(getString(R.string.Pop));
                    break;
                case "Clásica":
                    query = partituraDaoImpl.getByCategory(getString(R.string.Clasica));
                    break;
                case "Videojuegos":
                    query = partituraDaoImpl.getByCategory(getString(R.string.Videojuegos));
                    break;
                case "Películas":
                    query = partituraDaoImpl.getByCategory(getString(R.string.Peliculas));
                    break;
                case "Baladas":
                    query = partituraDaoImpl.getByCategory(getString(R.string.Baladas));
                    break;
                default:

            }
        }
        createAdapter(query);

        adapter.setOnCLickElementoListener((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            Intent intent = new Intent(this, PDFReader.class);
            intent.putExtra(EXTRA_PDF,partitura.getPdf());
            startActivity(intent);

            FirebaseStorage storageRef =FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Intent intent1 = new Intent(this, PDFReader.class);
                intent1.putExtra(EXTRA_PDF,uri.toString());
                startActivity(intent1);
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        });

        adapter.setListenerDownload((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            FirebaseStorage storageRef =FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.downloadPDF(partitura.getName(),".pdf", Environment.getExternalStorageDirectory().getPath(),uri.toString());
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        });
    }

    public void createAdapter(Query query) {
        System.out.println(result);

        FirestoreRecyclerOptions<Partitura> options = new FirestoreRecyclerOptions.Builder<Partitura>()
                //consulta y clase en la que se guarda los datos
                .setQuery(query, Partitura.class).setLifecycleOwner(this).build();
        //si el usuario ya habia seleccionado otra conferencia, paramos las escucha
        if (adapter != null) adapter.stopListening();
        //Creamos el adaptador
        System.out.println("Creamos adaptador");
        adapter = new PartituraAdapter(options, this);

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

}