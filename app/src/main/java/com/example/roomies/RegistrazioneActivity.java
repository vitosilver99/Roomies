package com.example.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class RegistrazioneActivity extends AppCompatActivity {

    String userID;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_reg =  email.getText().toString();
                String pass_reg = password.getText().toString();
                String nome_reg = nome.getText().toString();
                String cognome_reg = cognome.getText().toString();

                //controlli(nome_reg,cognome_reg,email_reg,pass_reg,nome,cognome,email,password);
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

                if(!email_reg.contains("@"))
                {
                    email.setError("Inserisci una mail valida");
                    return;
                }

                if(TextUtils.isEmpty(pass_reg))
                {
                    password.setError("Inserisci la password");
                    return;
                }
                if(pass_reg.length()<6)
                {
                    password.setError("Inserisci una password di almeno 6 cifre");
                }

                load.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email_reg,pass_reg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegistrazioneActivity.this,"Utente creato",Toast.LENGTH_LONG).show();
                            userID= mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("utenti").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("nome",nome_reg);
                            user.put("cognome",cognome_reg);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Op.Successo" ,"l'utente è stato creato con lo UserId: "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Op.Negata" ,"l'utente non è stato creato: "+e.toString());
                                }
                            });

                            load.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            RegistrazioneActivity.super.finish();
                            LoginActivity.getInstance().finish();
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

 /*   private void controlli(String nome_reg, String cognome_reg, String email_reg, String pass_reg, EditText nome,
                           EditText cognome, EditText email, EditText password )
    {

    }*/

}