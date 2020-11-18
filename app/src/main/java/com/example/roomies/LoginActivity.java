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
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
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
                String snome = usernameEditText.getText().toString().trim();
                String spass = passwordEditText.getText().toString().trim();

                if(TextUtils.isEmpty(snome))
                {
                    usernameEditText.setError("Inserisci il nome");
                    return;
                }

                if(TextUtils.isEmpty(spass))
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
