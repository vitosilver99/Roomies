package com.example.roomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.roomies.calendario.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UdCasa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String parametri = b.getString("userID");
        UdCasa = b.getString("casaID");

        fStore = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        Toast.makeText(getApplicationContext(), "userID:" + parametri, Toast.LENGTH_LONG).show();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;

                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                selectedFragment = new HomeFragment();
                                break;
                            case R.id.navigation_spesa:
                                selectedFragment = new SpesaFragment();
                                break;
                            case R.id.navigation_pagamenti:
                                selectedFragment = new PagamentiFragment();
                                break;
                            case R.id.navigation_chat:
                                selectedFragment = new ChatFragment();
                                break;
                            case R.id.navigation_calendario:
                                selectedFragment = new CalendarFragment(UdCasa);
                                break;
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                        return true;
                    }
    };

}