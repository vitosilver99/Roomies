package com.example.roomies;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.roomies.spesa.SpesaFragment;
import com.example.roomies.chat.ChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.Toast;
import com.example.roomies.calendario.*;
import com.example.roomies.pagamenti.*;

public class MainActivity extends AppCompatActivity {


    // parametri inizializzazione PagamentiFragment
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";



    //FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String casaID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle b = new Bundle();
        b = getIntent().getExtras();
        userID = b.getString("userID");
        casaID = b.getString("casaID");

        fStore = FirebaseFirestore.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        Toast.makeText(getApplicationContext(), "userID:" + userID, Toast.LENGTH_LONG).show();


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
                                Bundle args= new Bundle();
                                args.putCharSequence(ARG_USER_ID, userID);
                                args.putCharSequence(ARG_CASA_ID, casaID);
                                selectedFragment = new SpesaFragment();
                                selectedFragment.setArguments(args);
                                break;
                            case R.id.navigation_pagamenti:

                                //devo passare al nuovo fragment casaID cioè l'ID della casa (lo passo in param1)
                                Bundle args2= new Bundle();
                                args2.putCharSequence(ARG_USER_ID, userID);
                                args2.putCharSequence(ARG_CASA_ID, casaID);
                                selectedFragment = new PagamentiFragment();
                                selectedFragment.setArguments(args2);

                                break;
                            case R.id.navigation_chat:
                                Bundle args_chat= new Bundle();
                                args_chat.putCharSequence(ARG_USER_ID, userID);
                                args_chat.putCharSequence(ARG_CASA_ID, casaID);
                                selectedFragment = new ChatFragment();
                                selectedFragment.setArguments(args_chat);

                                break;
                            case R.id.navigation_calendario:
                                selectedFragment = new CalendarFragment(casaID);
                                break;
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                        return true;

                    }
    };

}