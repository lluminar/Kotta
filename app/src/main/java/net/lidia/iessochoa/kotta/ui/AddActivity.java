package net.lidia.iessochoa.kotta.ui;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import net.lidia.iessochoa.kotta.R;

public class AddActivity extends AppCompatActivity {

    private final int PICK_PDF_FILE = 2;
    private String[] categorias;
    private AutoCompleteTextView actvCategoria;
    private ImageView ivPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        categorias = getResources().getStringArray(R.array.category);

        actvCategoria = findViewById(R.id.fedCategoria);
        ivPDF = findViewById(R.id.ivPdf);

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
}