package net.lidia.iessochoa.kotta.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.FirebaseContract;
import net.lidia.iessochoa.kotta.model.Partitura;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PDFReader extends AppCompatActivity {

    private TextView tvView;
    private PDFView pdfView;
    private FirebaseFirestore mDatabaseReference = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        tvView = findViewById(R.id.tvViewer);
        pdfView = findViewById(R.id.pdfViewer);

        /*Task<Uri> newTask = task.getResult().getMetadata().getReference().getDownloadUrl();
        newTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                yourUriVariable= uri;
            }
        });*/

        new RetrivePdfStream().execute("http://www.africau.edu/images/default/sample.pdf");

    }

    private void getUriFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Partitura> listaPartituras;
        listaPartituras = new ArrayList<>();
        List<String> listaUri;
        listaUri=new ArrayList<>();
        db.collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Partitura partitura = document.toObject(Partitura.class);
                            listaPartituras.add(partitura);
                            //Obtenemos el nombre para añadirlo al spinner
                            listaUri.add(partitura.getPdf());
                        }
                    } else {
                        //Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    class RetrivePdfStream extends AsyncTask<String,Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }
}