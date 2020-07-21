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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.ui.Transision.TransisionActivity;

public class RegistoActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etName;
    private Button btRegistrar;
    private ScrollView FormRegistro;
    private ProgressBar pbRegistro;
    private String name, email,password;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);
        etName= findViewById(R.id.editTextName);
        etEmail= findViewById(R.id.editTextEmail);
        etPassword=findViewById(R.id.editTextPassword);
        btRegistrar=findViewById(R.id.buttonRegistro);
        FormRegistro=findViewById(R.id.formRegistro);
        pbRegistro=findViewById(R.id.progressBar);

        firebaseAuth =firebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        changeRegistroFormVisibility(true);

        //Evento de boton iniciar sesion
        eventoBt();
    }

    private void eventoBt() {
        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name = etName.getText().toString();
                 email =etEmail.getText().toString();
                 password =etPassword.getText().toString();

                if(name.length()<=4)
                {
                    etName.setError("El nombre es obligatorio");
                }else  if(email.isEmpty())
                {
                    etEmail.setError("El Correo es obligatorio");
                }else if(password.length()<=5)
                {
                    etPassword.setError("La contraseña es insegura, introduzca minimo 6 caracteres");
                }else{
                   CrearUsuario();

                }

            }
        });

    }

    private void CrearUsuario() {
        changeRegistroFormVisibility(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(RegistoActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });


    }

    private void updateUI(FirebaseUser user) {
        if(user!= null){
            name=etName.getText().toString();
            User nuevoUsuario= new User(name,0,0,user.getUid());
            db.collection("users")
                    .document(user.getUid())
                    .set(nuevoUsuario)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            Intent i= new Intent(RegistoActivity.this, TransisionActivity.class);
                            startActivity(i);
                        }
                    });

        } else{
            changeRegistroFormVisibility(true);
            etPassword.setError("Algo esta mal, Usuario no puede ser nulo");
            etPassword.requestFocus();
        }
    }

    private void changeRegistroFormVisibility(Boolean showForm) {
        pbRegistro.setVisibility(showForm ? View.GONE: View.VISIBLE);
        FormRegistro.setVisibility(showForm ? View.VISIBLE: View.GONE);
    }
}
