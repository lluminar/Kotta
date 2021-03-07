package net.lidia.iessochoa.kotta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GoogleSignIn";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnSignInGoogle;
    private Button btnSignIn;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        btnSignIn = findViewById(R.id.btnSignIn);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnSignInGoogle.setOnClickListener(view -> {
            signInGoogle();
        });

        btnSignIn.setOnClickListener(v -> {
            signIn();
        });

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Create a GoogleSignInClient with specifications options by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    //Google Sign In was succesful, authenticate with firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign in failed", e);
                }
            } else {
                Log.d(TAG, "Error al loguear: " + task.getException().toString());
                Toast.makeText(this, "Error" + task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signIn() {
        if (mAuth.getCurrentUser() != null) {
            finish();// Cerramos la actividad.
            //Abrimos la actividad que contiene el inicio de la funcionalidad de la app.
            startActivity(new Intent(this, PrincipalActivity.class));
        } else {
            //Si no está autenticado, llamamos al proceso de autenticación de FireBase
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    // Si quisieramos varios proveedores de autenticación. Mirar la documentación oficial, ya que cambia de una versión a otra
                    // .setAvalaibleProviders(AuthUI.EMAIL_PROVIDER,AuthUI.GOOGLE_PROVIDER)
                    // icono que mostrará, a mi no me funciona
                    .setLogo(R.drawable.ic_launcher_background)
                    .setIsSmartLockEnabled(false)//para guardar contraseñas y usuario: true
                    .build(), RC_SIGN_IN);
        }
    }

    /**
     * Get Credentials of user with a token
     * @param idToken: Token of user
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "SignInWithCredential: success");
                Intent principalActivity = new Intent(this, PrincipalActivity.class);
                startActivity(principalActivity);
                this.finish();
            }
            else
                Log.w(TAG, "SignInWithCredential: fail", task.getException());

        });
    }

    /**
     * Method to verify if the user is logged in
     * If it is, we prevent you from logging in again
     */
    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent principalActivity = new Intent(this, PrincipalActivity.class);
            startActivity(principalActivity);
        }
        super.onStart();
    }
}