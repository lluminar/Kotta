package net.lidia.iessochoa.kotta.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import net.lidia.iessochoa.kotta.R;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GoogleSignIn";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnSignInGoogle, btnSignIn;
    private EditText etEmail, etPassword;
    private String emailHolder, passwordHolder;
    private boolean editTextEmptyCheck;

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
            checkEditTextIsNotEmpty();
            if (editTextEmptyCheck)
                signIn();
            else
                Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
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

    private void signIn() {
        if (mAuth.getCurrentUser() != null) {
            finish();// Cerramos la actividad.
            //Abrimos la actividad que contiene el inicio de la funcionalidad de la app.
            startActivity(new Intent(this, PrincipalActivity.class));
        } else {
            mAuth.signInWithEmailAndPassword(emailHolder, passwordHolder)
                    .addOnCompleteListener(this, task -> {
                        // If task done Successful.
                        if(task.isSuccessful()){
                            // Closing the current Login Activity.
                            finish();
                            // Opening the UserProfileActivity.
                            Intent intent = new Intent(this, PrincipalActivity.class);
                            startActivity(intent);
                        }
                        else {
                            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                                    // Si quisieramos varios proveedores de autenticación. Mirar la documentación oficial, ya que cambia de una versión a otra
                                    // .setAvalaibleProviders(AuthUI.EMAIL_PROVIDER,AuthUI.GOOGLE_PROVIDER)
                                    // icono que mostrará, a mi no me funciona
                                    .setIsSmartLockEnabled(false)//para guardar contraseñas y usuario: true
                                    .build(), RC_SIGN_IN);
                            // Showing toast message when email or password not found in Firebase Online database.
                            Toast.makeText(this, "Email or Password Not found, Please create an account", Toast.LENGTH_LONG).show();
                        }
                    });
            //Si no está autenticado, llamamos al proceso de autenticación de FireBase
            /**/
        }
    }

    private void checkEditTextIsNotEmpty() {
        // Getting value form Email's EditText and fill into EmailHolder string variable.
        emailHolder = etEmail.getText().toString().trim();

        // Getting value form Password's EditText and fill into PasswordHolder string variable.
        passwordHolder = etPassword.getText().toString().trim();

        // Checking Both EditText is empty or not.
        if(TextUtils.isEmpty(emailHolder) || TextUtils.isEmpty(passwordHolder))
            // If any of EditText is empty then set value as false.
            editTextEmptyCheck = false;
        else
            // If any of EditText is empty then set value as true.
            editTextEmptyCheck = true ;
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

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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