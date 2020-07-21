package com.lvmo.tocatef2.ui.InicioSesion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.ui.Transision.TransisionActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btlogin;
    private ScrollView FormLogin;
    private ProgressBar pbLogin;
    private String name, email,password;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail= findViewById(R.id.editTextEmailLA);
        etPassword=findViewById(R.id.editTextPasswordLA);
        btlogin=findViewById(R.id.buttonLoginLA);
        FormLogin=findViewById(R.id.formLo);
        pbLogin=findViewById(R.id.progressBar);

        firebaseAuth =firebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        changeRegistroFormVisibility(true);

        eventoBt();
    }

    private void eventoBt() {
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (email.isEmpty()) {
                    etEmail.setError("El Correo es obligatorio");
                } else if (password.isEmpty()) {
                    etPassword.setError("La contrasña es obligatorio");
                } else {
                    iniciarSesion();
                }

            }});
    }

    private void iniciarSesion() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });

    }

    private void updateUI(FirebaseUser user) {
        if(user!= null){
            finish();
            Intent i= new Intent(LoginActivity.this, TransisionActivity.class);
            startActivity(i);
        } else{
            etPassword.setError("Error en Usuario O contraseña ");
            etPassword.requestFocus();
        }
    }

    private void changeRegistroFormVisibility(Boolean showForm) {
        pbLogin.setVisibility(showForm ? View.GONE: View.VISIBLE);
        FormLogin.setVisibility(showForm ? View.VISIBLE: View.GONE);
    }
}
