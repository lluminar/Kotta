package net.lidia.iessochoa.kotta.ui;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import net.lidia.iessochoa.kotta.R;

public class AddActivity extends AppCompatActivity {

    private String[] categorias;
    private AutoCompleteTextView actvCategoria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        categorias = getResources().getStringArray(R.array.category);

        actvCategoria = findViewById(R.id.fedCategoria);

        //Se crean los menús para los spinners
        ArrayAdapter<String> adaptador1;
        AutoCompleteTextView actv;
        adaptador1 = new ArrayAdapter<>(this,R.layout.list_item_sp, categorias);
        actv = findViewById(R.id.fedCategoria);
        actv.setAdapter(adaptador1);
    }
}