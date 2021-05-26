package net.lidia.iessochoa.kotta.ui;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.FirebaseContract;
import net.lidia.iessochoa.kotta.model.Partitura;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author Lidia Martínez Torregrosa
 */
public class AddActivity extends AppCompatActivity {
    private final int PICK_PDF_FILE = 2;
    private static final int MY_PERMISSIONS = 100;

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private FirebaseFirestore mDatabaseReference;

    private Partitura partitura;

    private ConstraintLayout clPrincipal;
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

        //Initialize FirebaseApp
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
        clPrincipal = findViewById(R.id.clPrincipal);

        setSupportActionBar(toolbar);
        categorias = getResources().getStringArray(R.array.category);
        //Create menu for spinner
        ArrayAdapter<String> adaptador1;
        AutoCompleteTextView actv;
        adaptador1 = new ArrayAdapter<>(this,R.layout.list_item_sp, categorias);
        actv = findViewById(R.id.fedCategoria);
        actv.setAdapter(adaptador1);

        /**
         * When user click imageView allow to get Pdf with permissions requiered
         */
        ivPDF.setOnClickListener(v -> {
            if (noNecesarioSolicitarPermisos()) getPDF();
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
     * Method of menu, this add items to action bar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_partitura, menu);
        return true;
    }

    /**
     * This method is uploading the file with information
     * @param data: To get pdf
     */
    @SuppressLint("ResourceType")
    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child(FirebaseContract.PartituraEntry.STORAGE_PATH_UPLOADS
                + System.currentTimeMillis() + getString(R.string.extension));
        sRef.putFile(data)
                .addOnSuccessListener(taskSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    partitura = new Partitura(
                        mAuth.getCurrentUser().getEmail(),
                        etName.getText().toString().toUpperCase(),
                        etInstrument.getText().toString(),
                        etAuthor.getText().toString(),
                        actvCategoria.getText().toString(),
                        taskSnapshot.getMetadata().getReference().getName()
                    );
                    mDatabaseReference.collection(FirebaseContract.PartituraEntry.DATABASE_PATH_UPLOADS).document().set(partitura);
                    ocultarTeclado();
                    Toast.makeText(this, getResources().getString(R.string.subidaExito),Toast.LENGTH_LONG).show();

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

    /**
     * Method to send an alert
     * @param advice
     */
    public void sendAlert(int advice) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(R.string.Aviso);
        dialogo.setMessage(advice);
        dialogo.setPositiveButton(android.R.string.ok, (dialogInterface, i) ->
                dialogInterface.dismiss());
        dialogo.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //When user click save score
            case R.id.action_save:
                //Data validation
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
     * Method to hide the keyboard
     */
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etAuthor.getWindowToken(), 0);
        }
    }

    /**
     * Creating an intent for file chooser
     */
    private void getPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_FILE);
    }

    /**
     * Check if permissions are needed
     * @return true if yes or false if not
     */
    private boolean noNecesarioSolicitarPermisos() {
        //if the version is less than 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        //check if we have permissions
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        //We indicate to the user why we need the permissions as long as he has not indicated that we do not do it again
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))  {
            Snackbar.make(clPrincipal, getString(R.string.nesitaPermisos),
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, v ->
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS)).show();
        } else {//we ask for permissions without indicating why
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS);
        }
        return false;//need to ask for permits
    }

    /**
     * If the permissions are denied we show the application options so that the user accepts the permissions
     */
    private void muestraExplicacionDenegacionPermisos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permisos));
        builder.setMessage(getString(R.string.nesitaPermisos));
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Intent intent = new Intent();

            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(getString(R.string.pacage), getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                    PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this,"tiene permisos", Toast.LENGTH_SHORT).show();
                getPDF();
            } else {//if permits are not accepted
                muestraExplicacionDenegacionPermisos();
            }
        }
    }
}