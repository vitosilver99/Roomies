package com.example.roomies;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class LoginActivity extends AppCompatActivity {

    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";
    private static final String ARG_NOME_USER = "param3";
    private static final String ARG_COGNOME_USER = "param4";



    FirebaseAuth fAuth;
    static LoginActivity activityA;
    FirebaseFirestore fStore;
    private String userId;
    private String casaId;
    private String nomeUser;
    private String cognomeUser;
    FirebaseUser firebaseUser;


    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser!=null){

            userId=fAuth.getCurrentUser().getUid();

            //Log.d("user id :",task.getResult().getUser().getUid());

            fStore.collection("utenti").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra(ARG_USER_ID,userId);
                            intent.putExtra(ARG_CASA_ID,documentSnapshot.get("casa").toString());

                            nomeUser = documentSnapshot.getString("nome");
                            cognomeUser = documentSnapshot.getString("cognome");
                            intent.putExtra(ARG_NOME_USER,nomeUser);
                            intent.putExtra(ARG_COGNOME_USER,cognomeUser);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText emailEditText = findViewById(R.id.Email_login);
        final EditText passwordEditText = findViewById(R.id.Password_login);
        final Button loginButton = findViewById(R.id.btn_login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final TextView registrazione = findViewById(R.id.Registrazione_login);


        activityA = this;

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        registrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrazioneActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if(!isEmailValid(email))
                {
                    emailEditText.setError("Inserisci una mail valida");
                    return;
                }

                if(TextUtils.isEmpty(password))
                {
                    passwordEditText.setError("Inserisci la password");
                    return;
                }
                loadingProgressBar.setVisibility(View.VISIBLE);

                //autenticazione

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            userId=fAuth.getCurrentUser().getUid();

                            //Prelevo il codice della casa dell'utente, il nome e il cognome
                            fStore.collection("utenti").document(userId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            casaId = documentSnapshot.get("casa").toString();

                                            Toast.makeText(LoginActivity.this,"Utente autenticato correttamente",Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "casaId:" + casaId, Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            //Log.d("user id :",task.getResult().getUser().getUid());
                                            intent.putExtra(ARG_USER_ID,task.getResult().getUser().getUid());
                                            intent.putExtra(ARG_CASA_ID,casaId);

                                            nomeUser = documentSnapshot.getString("nome");
                                            cognomeUser = documentSnapshot.getString("cognome");
                                            intent.putExtra(ARG_NOME_USER,nomeUser);
                                            intent.putExtra(ARG_COGNOME_USER,cognomeUser);

                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Errore: " +e.getMessage(), Toast.LENGTH_LONG).show();
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Errore di autenticazione: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });
    }

    public static LoginActivity getInstance(){
        return   activityA;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
