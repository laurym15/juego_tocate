package com.lvmo.tocatef2.ui.InicioSesion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;

import com.facebook.appevents.AppEventsLogger;
import com.lvmo.tocatef2.ui.Transision.TransisionActivity;

public class MainActivity extends AppCompatActivity {

    private Button btInSesion, btRegistro;
    private ScrollView FormLogin,layoutProBar;
    private String  name;
    private ImageView milogo;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseFirestore db;
    private TextView etInicioS;
    private boolean BtRegresar=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInicioS=findViewById(R.id.textViewMA);
        loginButton = findViewById(R.id.login_button);
        btRegistro = findViewById(R.id.buttonRegistro);
        btInSesion = findViewById(R.id.buttonInSesion);
        layoutProBar=findViewById(R.id.LayoutPBar);
        FormLogin = findViewById(R.id.formlongin);
        milogo=findViewById(R.id.milogoMA);
        ProgressBar pbLogin = findViewById(R.id.progressBarlogin);
        pbLogin.setIndeterminate(true);

        db = FirebaseFirestore.getInstance();
        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeLoginFormVisibility(false);
            }
        }, 2000);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userf = firebaseAuth.getCurrentUser();
                if (userf != null) {
                    updateUI(userf);
                }
            }
        };
        eventoBtlog();
    }

    //para facebook//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
//pa facebook//

    //para face-firebase//
    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential;
        credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });
    }
    //face-firebase//

    private void eventoBtlog() {

        btInSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegistoActivity.class);
                startActivity(i);
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, " Accion Cancelada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Error x Error x Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(final FirebaseUser user) {
        changeLoginFormVisibility(false);
        if (user != null) {

            if (AccessToken.getCurrentAccessToken() != null) {
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    //String id = profile.getId();
                    name = profile.getName();
                    //  String photoUrl = profile.getProfilePictureUri(100, 100).toString();
                    //etNombreF.setText(name);

                    db.collection("users")
                            .document(user.getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    finish();
                                    Intent i = new Intent(MainActivity.this, TransisionActivity.class);
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    User nuevoUsuario = new User(name, 0, 0, user.getUid());
                                    db.collection("users")
                                            .document(user.getUid())
                                            .set(nuevoUsuario)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    finish();
                                                    Intent i = new Intent(MainActivity.this, TransisionActivity.class);
                                                    startActivity(i);
                                                }
                                            });
                                }
                            });

                } else {
                    Profile.fetchProfileForCurrentAccessToken();
                }
            }
            /*  pregutna por nombre deusario facebook  */
            finish();
            Intent i = new Intent(MainActivity.this, TransisionActivity.class);
            startActivity(i);
        } else {
            changeLoginFormVisibility(true);

        }
    }

    private void changeLoginFormVisibility( Boolean showForm) {
        FormLogin.setVisibility(showForm ? View.GONE: View.VISIBLE);
        layoutProBar.setVisibility(showForm ? View.VISIBLE: View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth.addAuthStateListener((firebaseAuthListener));
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
        if(BtRegresar){changeLoginFormVisibility(false);}
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        BtRegresar=true;
    }
}