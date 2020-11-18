package com.example.roomies;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    static LoginActivity activityA;

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
                String pass = passwordEditText.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    emailEditText.setError("Inserisci l'email");
                    return;
                }

                if(TextUtils.isEmpty(pass))
                {
                    passwordEditText.setError("Inserisci la password");
                    return;
                }
                loadingProgressBar.setVisibility(View.VISIBLE);

            }
        });

    }

    public static LoginActivity getInstance(){
        return   activityA;
    }

}
