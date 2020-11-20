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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    static LoginActivity activityA;
    private String userID;

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

                if(TextUtils.isEmpty(email))
                {
                    emailEditText.setError("Inserisci l'email");
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
                            Toast.makeText(LoginActivity.this,"Utente autenticato correttamente",Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                            //Log.d("user id :",task.getResult().getUser().getUid());
                            intent.putExtra("UserId",task.getResult().getUser().getUid());
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Errore di autenticazione: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    public static LoginActivity getInstance(){
        return   activityA;
    }

}
