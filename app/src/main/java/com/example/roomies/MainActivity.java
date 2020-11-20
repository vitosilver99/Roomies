package com.example.roomies;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String parametri = b.getString("UserId");

        Toast.makeText(getApplicationContext(),"UserId:"+parametri,Toast.LENGTH_LONG).show();
    }
}