package net.lidia.iessochoa.kotta.ui;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.FirebaseContract;
import net.lidia.iessochoa.kotta.model.Partitura;

public class AddActivity extends AppCompatActivity {
    private final int PICK_PDF_FILE = 2;
    private static final int MY_PERMISSIONS = 100;

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private FirebaseFirestore mDatabaseReference;

    private Partitura partitura;

    private EditText etName;
    private EditText etInstrument;
    private EditText etAuthor;
    private ImageView ivPDF;
    private AutoCompleteTextView actvCategoria;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView tvProgress;

    private Uri uriPDF = null;
    private String[] categorias;
    private EditText[] datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etName);
        etInstrument = findViewById(R.id.etInstrument);
        etAuthor = findViewById(R.id.etAuthor);
        actvCategoria = findViewById(R.id.fedCategoria);
        ivPDF = findViewById(R.id.ivPdf);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        toolbar = findViewById(R.id.toolbarAdd);

        setSupportActionBar(toolbar);
        categorias = getResources().getStringArray(R.array.category);
        //Se crean los menús para los spinners
        ArrayAdapter<String> adaptador1;
        AutoCompleteTextView actv;
        adaptador1 = new ArrayAdapter<>(this,R.layout.list_item_sp, categorias);
        actv = findViewById(R.id.fedCategoria);
        actv.setAdapter(adaptador1);

        ivPDF.setOnClickListener(v -> {
            getPDF();
        });

        datos = new EditText[] {
                etName,
                etInstrument,
                etAuthor,
        };

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Función del menu, esto añade items a la action bar si está presente.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_partitura, menu);
        return true;
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_FILE);
    }

    /**
     * This method is uploading the file
     * @param data
     */
    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        String url = sRef.getDownloadUrl().toString();
        sRef.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    partitura = new Partitura(
                        mAuth.getCurrentUser().getEmail(),
                        etName.getText().toString(),
                        etInstrument.getText().toString(),
                        etAuthor.getText().toString(),
                        actvCategoria.getText().toString(),
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()
                    );
                    mDatabaseReference.collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS).document().set(partitura);
                    ocultarTeclado();
                    Toast.makeText(this, "File Uploaded Successfully",Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show())
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    int progreso = (int) progress;
                    tvProgress.setText(progreso + "% Uploading...");
                    progressBar.setProgress(progreso);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            switch (requestCode) {
                case PICK_PDF_FILE:
                    uriPDF = data.getData();
                    break;
                default:
                    Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void sendAlert(int advice) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(R.string.Aviso);// titulo y mensaje
        dialogo.setMessage(advice);
        dialogo.setPositiveButton(android.R.string.ok, (dialogInterface, i) ->
                dialogInterface.dismiss());
        dialogo.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Validación de datos
                for (EditText campo : datos) {
                    if (campo.getText().length() < 1 || actvCategoria.getText().toString().equals("")) {
                        sendAlert(R.string.vacio);
                        break;
                    }
                    if (uriPDF == null) {
                        sendAlert(R.string.pdfNull);
                        break;
                    } else {
                        uploadFile(uriPDF);
                        finish();
                        break;
                    }
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Permite ocultar el teclado
     */
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etAuthor.getWindowToken(), 0);
        }
    }
}