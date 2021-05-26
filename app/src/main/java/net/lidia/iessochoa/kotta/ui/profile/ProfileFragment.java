package net.lidia.iessochoa.kotta.ui.profile;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import net.lidia.iessochoa.kotta.ui.home.Filters;
import net.lidia.iessochoa.kotta.ui.home.HomeFragment;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static net.lidia.iessochoa.kotta.ui.home.HomeFragment.EXTRA_PDF;
/**
 * @author Lidia Martínez Torregrosa
 */
public class ProfileFragment extends Fragment {

    private AppCompatActivity activity;
    private PartituraAdapter adapter;
    private PartituraDao partituraDaoImpl;
    private RecyclerView rvPartituras;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference mStorageReference;
    private FirebaseFirestore mDatabaseReference;

    private ImageView ivAuthorGoogle;
    private TextView tvName;
    private TextView tvEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseFirestore.getInstance();
        ivAuthorGoogle = root.findViewById(R.id.ivUser);
        tvEmail = root.findViewById(R.id.tvEmail);
        tvName = root.findViewById(R.id.tvName);
        partituraDaoImpl= new PartituraDaoImpl();
        rvPartituras = root.findViewById(R.id.rvPartituras);
        rvPartituras.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get data of user
        tvName.setText(currentUser.getDisplayName());
        tvEmail.setText(currentUser.getEmail());
        if (currentUser.getPhotoUrl() != null)
            Glide.with(this).load(currentUser.getPhotoUrl()).into(ivAuthorGoogle);

        createAdapter();

        /**
         * When clicks one item open PDFViewer
         */
        adapter.setOnCLickElementoListener((snapshot, position) -> {
            Partitura partitura = snapshot.toObject(Partitura.class);
            Intent intent = new Intent(getActivity(), PDFReader.class);
            intent.putExtra(EXTRA_PDF,partitura.getPdf());
            startActivity(intent);

            FirebaseStorage storageRef =FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Intent intent1 = new Intent(getActivity(), PDFReader.class);
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
            FirebaseStorage storageRef = FirebaseStorage.getInstance();
            StorageReference pathReference = storageRef.getReference()
                    .child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());

            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadPDF(partitura.getName(), getString(R.string.extension),
                        Environment.getExternalStorageDirectory().getPath(), uri.toString());
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

    /**
     * Method to delete score
     * @param partitura: The score to delete
     * @param documentId: position of the score
     */
    public void deletePartitura(final Partitura partitura, final  String documentId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.Aviso);
        dialog.setMessage(R.string.avisoBorrar);

        dialog.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            // if ok
                StorageReference sRef = mStorageReference.child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + partitura.getPdf());
                sRef.delete()
                        .addOnSuccessListener(taskSnapshot -> {
                            mDatabaseReference.collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS).document(documentId).delete();
                            Intent intent = new Intent(getContext(), PrincipalActivity.class);
                            startActivity(intent);
                            Toast.makeText(getContext(), getString(R.string.borradoCorrecto),Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(exception -> Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show());
        });
        //If cancel don't delete
        dialog.setNegativeButton(android.R.string.no, (dialogInterface, i) -> {

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
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getContext(), destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
    }

    /**
     * Method to create adapter on recyclerView
     */
    private void createAdapter() {
        Query query = partituraDaoImpl.getOwnPartituras();
        FirestoreRecyclerOptions<Partitura> options = new FirestoreRecyclerOptions.Builder<Partitura>()
                //query and class in which the data is saved
                .setQuery(query, Partitura.class).setLifecycleOwner(this).build();
        if (adapter != null) adapter.stopListening();
        //Create adapter
        adapter = new PartituraAdapter(options, getContext());
        //assign the adapter
        rvPartituras.setAdapter(adapter);
        //we began to listen.
        adapter.startListening();
        // Move the scroll of the recyclerView to the beginning to see the new message
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
}