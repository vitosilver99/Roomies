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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        final EditText num_telefono = findViewById((R.id.Num_registrazione));

        final ProgressBar load = findViewById(R.id.progressBar_reg);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        /*
        crea un Listener sul tasto registrati che ascolta i Click
        setOnClickListener() ha bisogno come parametro di un Listener particolare
        */
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_reg =  email.getText().toString();
                String pass_reg = password.getText().toString();
                String nome_reg = nome.getText().toString();
                String cognome_reg = cognome.getText().toString();
                String num_tel = num_telefono.getText().toString();

                if(TextUtils.isEmpty(nome_reg))
                {
                    nome.setError("Inserisci il nome");

                    //in caso di compilazione errata dei campi ritorna all'ascolto di Click sul tasto registrati
                    return;
                }

                if(TextUtils.isEmpty(cognome_reg))
                {
                    cognome.setError("Inserisci la cognome");
                    return;
                }

                if(num_tel.isEmpty())
                {
                    num_telefono.setError("Inserisci il numero del telefono");
                    return;
                }

                if(!isEmailValid(email_reg))
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
                    password.setError("Inserisci una password di almeno 6 caratteri");
                    return;
                }

                load.setVisibility(View.VISIBLE);

                 /*
                creazione utente in caso di compilazione corretta dei campi
                crea un Listener sul completamento (onComplete) dell'operazione di registrazione che ascolta
                setOnCompleteListener() ha bisogno come parametro di un Listener particolare
                */

                mAuth.createUserWithEmailAndPassword(email_reg,pass_reg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    //onComplete ha come parametro un Task, in questo caso un risultato di autenticazione
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //se l'utente è stato registrato
                        if(task.isSuccessful())
                        {
                            //Toast.makeText(RegistrazioneActivity.this,"Utente creato",Toast.LENGTH_LONG).show();

                            //mAuth può restituire ID dell'utente creato. L'ID viene generato automaticamente in FireBase
                            userID= mAuth.getCurrentUser().getUid();

                            //crea un riferimento al documento relativo al nuovo utente
                            DocumentReference documentReference = fStore.collection("utenti").document(userID);

                            //crea una mappa che contenga tutte le informazioni relative al nuovo utente
                            Map<String,Object> user = new HashMap<>();
                            user.put("nome",nome_reg);
                            user.put("cognome",cognome_reg);
                            user.put("num_telefono",num_tel);

                            //aggiorna il documento relativo al nuovo utente
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Op.Successo" ,"L'utente è stato creato con lo UserId: "+ userID);
                                    Intent intent=new Intent(getApplicationContext(),CheckCasaActivity.class);

                                    //passare l'identificatore utente all'activity relativa al controllo della casa
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("nome", nome_reg);
                                    intent.putExtra("cognome",cognome_reg);
                                    startActivity(intent);

                                    //nascondi barra di caricamento
                                    load.setVisibility(View.INVISIBLE);

                                    //chiudi l'attività di registrazione e di login
                                    RegistrazioneActivity.super.finish();
                                    LoginActivity.getInstance().finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Op.Negata" ,"l'utente non è stato creato: "+e.toString());
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(RegistrazioneActivity.this, "Errore di autenticazione: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            load.setVisibility(View.INVISIBLE);
                        }

                    }
                });

            }
        });

    }

    //controlla che la stringa inserita come email sia corretta
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}