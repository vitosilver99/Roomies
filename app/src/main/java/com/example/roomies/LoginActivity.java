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
    FirebaseAuth fAuth;
    static LoginActivity activityA;
    FirebaseFirestore fStore;
    private String userID;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
            //Log.d("user id :",task.getResult().getUser().getUid());

            fStore.collection("utenti").document(fAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            intent.putExtra("userID",fAuth.getCurrentUser().getUid());
                            intent.putExtra("casaID",documentSnapshot.get("casa").toString());

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

                            //Prelevo il codice della casa dell'utente
                            fStore.collection("utenti").document(task.getResult().getUser().getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String UdCasa = documentSnapshot.get("casa").toString();

                                            Toast.makeText(LoginActivity.this,"Utente autenticato correttamente",Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "casaId:" + UdCasa, Toast.LENGTH_LONG).show();

                                            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                                            //Log.d("user id :",task.getResult().getUser().getUid());
                                            intent.putExtra("userID",task.getResult().getUser().getUid());
                                            intent.putExtra("casaID",UdCasa);

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
