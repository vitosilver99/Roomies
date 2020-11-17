package com.example.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrazioneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        final Button registrati = findViewById(R.id.btn_registrazione);
        final EditText nome = findViewById(R.id.Nome_registrazione);
        final EditText cognome = findViewById(R.id.Cognome_registrazione);
        final EditText email = findViewById(R.id.Email_registrazione);
        final EditText password = findViewById(R.id.Password_registrazione);
        final ProgressBar load = findViewById(R.id.progressBar_reg);

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_reg =  email.getText().toString();
                String pass_reg = password.getText().toString();
                String nome_reg = nome.getText().toString();
                String cognome_reg = cognome.getText().toString();

                if(TextUtils.isEmpty(nome_reg))
                {
                    nome.setError("Inserisci il nome");
                    return;
                }

                if(TextUtils.isEmpty(cognome_reg))
                {
                    cognome.setError("Inserisci la cognome");
                    return;
                }

                if(TextUtils.isEmpty(email_reg))
                {
                    email.setError("Inserisci l'email");
                    return;
                }

                if(TextUtils.isEmpty(pass_reg))
                {
                    password.setError("Inserisci la password");
                    return;
                }

                load.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email_reg,pass_reg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegistrazioneActivity.this,"Utente creato",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(RegistrazioneActivity.this, "Errore di autenticazione: "+task.getException().getMessage(),Toast.LENGTH_LONG);
                        }

                    }
                });

            }
        });


    }
}