package net.lidia.iessochoa.kotta.ui;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;
import net.lidia.iessochoa.kotta.ui.home.HomeFragment;
import net.lidia.iessochoa.kotta.ui.home.HomeViewModel;

public class AddActivity extends AppCompatActivity {
    public static final String EXTRA_PARTITURA = "net.lidia.iessochoa.kotta.AddActivity";
    public static final String EXTRA_PARTITURA_RESULT = "net.lidia.iessochoa.kotta.AddActivity";
    private final int PICK_PDF_FILE = 2;

    private Partitura partitura;
    private HomeViewModel homeViewModel;

    private EditText etName;
    private EditText etInstrument;
    private EditText etAuthor;
    private ImageView ivPDF;
    private AutoCompleteTextView actvCategoria;
    private String[] categorias;
    private EditText[] datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etName = findViewById(R.id.etName);
        etInstrument = findViewById(R.id.etInstrument);
        etAuthor = findViewById(R.id.etAuthor);
        actvCategoria = findViewById(R.id.fedCategoria);
        ivPDF = findViewById(R.id.ivPdf);

        categorias = getResources().getStringArray(R.array.category);

        partitura = getIntent().getExtras().getParcelable(EXTRA_PARTITURA);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //Se crean los menús para los spinners
        ArrayAdapter<String> adaptador1;
        AutoCompleteTextView actv;
        adaptador1 = new ArrayAdapter<>(this,R.layout.list_item_sp, categorias);
        actv = findViewById(R.id.fedCategoria);
        actv.setAdapter(adaptador1);

        ivPDF.setOnClickListener(v -> {
            String directorio = Environment.getExternalStorageDirectory().getPath();
            openFile(Uri.parse(directorio));
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

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Validación de datos
                Intent intent = new Intent();
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
                        partitura.setNombre(etName.getText().toString());
                        partitura.setAutor(etAuthor.getText().toString());
                        partitura.setInstrumento(etInstrument.getText().toString());
                        getIntent().putExtra(EXTRA_PARTITURA_RESULT, partitura);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}