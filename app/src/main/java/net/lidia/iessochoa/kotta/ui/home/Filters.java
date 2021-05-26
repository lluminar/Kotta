package net.lidia.iessochoa.kotta.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import net.lidia.iessochoa.kotta.ui.PDFReader;
import net.lidia.iessochoa.kotta.ui.PrincipalActivity;
import net.lidia.iessochoa.kotta.ui.adapters.PartituraAdapter;

import static net.lidia.iessochoa.kotta.ui.BottomSheetNavigationFragment.EXTRA_DATOS_RESULTADO;
import static net.lidia.iessochoa.kotta.ui.home.HomeFragment.EXTRA_PDF;
/**
 * @author Lidia Martínez Torregrosa
 */
public class Filters extends AppCompatActivity {
    private PartituraAdapter adapter;
    private RecyclerView rvPartituras;
    private String result;
    private Toolbar toolbar;
    private PartituraDao partituraDaoImpl;
    private Query query;
    private StorageReference mStorageReference;
    private FirebaseFirestore mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        rvPartituras = findViewById(R.id.rvFilters);
        rvPartituras.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbarAdd);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseFirestore.getInstance();
        partituraDaoImpl = new PartituraDaoImpl();
        //Go back
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });

        Bundle datosRecuperados = this.getIntent().getExtras();
        if (datosRecuperados != null) {
            result = datosRecuperados.getString(EXTRA_DATOS_RESULTADO);
            System.out.println("resultado:" + result);
        }

        //Choose what query is going to be used
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
                    query = partituraDaoImpl.searchByName(result);
            }
        }
        //Create adapter with the query
        createAdapter(query);

        /**
         * When clicks one item open PDFViewer
         */
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

        /**
         * When clicks download image download pdf
         */
        adapter.setListenerDownload((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            FirebaseStorage storageRef =FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
               downloadPDF(partitura.getName(),getString(R.string.extension),
                       Environment.getExternalStorageDirectory().getPath(),uri.toString());
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        });

        /**
         * When clicks on the bin delete score
         */
        adapter.setListenerOptions((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            deletePartitura(partitura,snapshot.getId());
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

        adapter.notifyDataSetChanged();

        //asignamos el adaptador
        rvPartituras.setAdapter(adapter);
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

    public void deletePartitura(final Partitura partitura, final  String documentId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.Aviso);
        dialog.setMessage(R.string.avisoBorrar);

        //En caso de que acepte borramos la partitura
        dialog.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            // Qué hacemos en caso ok
            StorageReference sRef = mStorageReference.child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());
            sRef.delete()
                    .addOnSuccessListener(taskSnapshot -> {
                        mDatabaseReference.collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS).document(documentId).delete();
                        Intent intent = new Intent(this, PrincipalActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, getString(R.string.borradoCorrecto),Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(exception -> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show());
        });
        //Si cancela no borramos el pokemon
        dialog.setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
            // Qué hacemos en caso cancel
        });
        dialog.show();
    }

    /**
     * Method to download pdf
     * @param fileName
     * @param fileExtension
     * @param destinationDirectory
     * @param url
     */
    public void downloadPDF(String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this, destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
    }
}