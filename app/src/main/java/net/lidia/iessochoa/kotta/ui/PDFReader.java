package net.lidia.iessochoa.kotta.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;

import net.lidia.iessochoa.kotta.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static net.lidia.iessochoa.kotta.ui.home.HomeFragment.EXTRA_PDF;

/**
 * @author Lidia Martínez Torregrosa
 */
public class PDFReader extends AppCompatActivity {

    private TextView tvView;
    private PDFView pdfView;
    String resultado;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        tvView = findViewById(R.id.tvViewer);
        pdfView = findViewById(R.id.pdfViewer);
        //To return to PrincipalActivity when users click the cross
        toolbar = findViewById(R.id.toolbarPdf);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, PrincipalActivity.class);
            startActivity(intent);
        });

        /**
         * Check if there are connection
         * if there are allow to show the pdf if not we advice user
         */
        if (isConnected()) {
            resultado = getIntent().getStringExtra(EXTRA_PDF);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.fui_no_internet))
                    .setMessage("Go to Setting?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> startActivity(
                            new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)))
                    .setNegativeButton("No", (dialog, which) -> {
                    });
            //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();
        }
        new RetrivePdfStream().execute(resultado);
    }

    /**
     * Method to check if there are connection
     * @return: if there are or not
     */
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    /**
     * Go to the main activity of project
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }

    /**
     * Class to create PDFViewer
     */
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