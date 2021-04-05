package net.lidia.iessochoa.kotta.ui;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.FirebaseContract;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.ui.home.HomeViewModel;

public class AddActivity extends AppCompatActivity {
    private final int PICK_PDF_FILE = 2;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    private Partitura partitura;

    private EditText etName;
    private EditText etInstrument;
    private EditText etAuthor;
    private ImageView ivPDF;
    private AutoCompleteTextView actvCategoria;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private String[] categorias;
    Button button;
    private EditText[] datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS);
        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etName);
        etInstrument = findViewById(R.id.etInstrument);
        etAuthor = findViewById(R.id.etAuthor);
        actvCategoria = findViewById(R.id.fedCategoria);
        ivPDF = findViewById(R.id.ivPdf);
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tvProgress);
        button = findViewById(R.id.button2);

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

        datos = new EditText[]{
                etName,
                etInstrument,
                etAuthor
        };
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
        sRef.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    System.out.println("File Uploaded Successfully");
                    partitura = new Partitura(
                            mAuth.getCurrentUser().getEmail(),
                            etName.getText().toString(),
                            etInstrument.getText().toString(),
                            etAuthor.getText().toString(),
                            actvCategoria.getAdapter().toString(),
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()
                    );
                    mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(partitura);
                    Toast.makeText(this, "File Uploaded Successfully",Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show())
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    int progreso = (int) progress;
                    tvProgress.setText(progreso + "% Uploading...");
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null ) {
                //uploading the file
                button.setOnClickListener(v -> {
                    uploadFile(data.getData());
                    finish();
                });
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Validación de datos
                for (EditText campo : datos) {
                    if (campo.getText().length() < 1) {
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                        dialogo.setTitle(R.string.Aviso);// titulo y mensaje
                        dialogo.setMessage(R.string.vacio);
                        dialogo.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
                        dialogo.show();
                        break;
                    }
                    else{
                        button.performClick();
                    }
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}