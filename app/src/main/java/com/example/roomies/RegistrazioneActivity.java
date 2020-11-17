package com.example.roomies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrazioneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        Button registrati = findViewById(R.id.btn_registrazione);
        EditText nome = findViewById(R.id.Nome_registrazione);
        EditText cognome = findViewById(R.id.Cognome_registrazione);
        EditText email = findViewById(R.id.Email_registrazione);
        EditText password = findViewById(R.id.Password_registrazione);
        ProgressBar load = findViewById(R.id.progressBar_reg);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = nome.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(TextUtils.isEmpty(nom))
                {
                    nome.setError("Inserisci il nome");
                    return;
                }

                if(TextUtils.isEmpty(pass))
                {
                    password.setError("Inserisci la password");
                    return;
                }


            }
        });


    }
}