package com.example.roomies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CheckCasaActivity extends AppCompatActivity {

    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";
    private static final String ARG_NOME_USER = "param3";
    private static final String ARG_COGNOME_USER = "param4";


    ViewPager viewPager;
    AdapterCheckCasa adapterCheckCasa;
    List<ModelCheckCasa> modelCheckCasas;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    String userId;
    String nomeUser;
    String cognomeUser;

    FirebaseFirestore fStore;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_casa);
        Bundle b = new Bundle();
        b = getIntent().getExtras();

        //ottieni identificatore utente dall'activity registrazione
        userId = b.getString(ARG_USER_ID);
        nomeUser = b.getString(ARG_NOME_USER);
        cognomeUser = b.getString(ARG_COGNOME_USER);
        Log.d("nome utente", nomeUser);
        Log.d("cognome utente", cognomeUser);

        Log.d("userID restituito", userId);
        fStore = FirebaseFirestore.getInstance();

        modelCheckCasas = new ArrayList<>();
        modelCheckCasas.add(new ModelCheckCasa(R.drawable.lambrusco, "PARTECIPA A UNA CASA", "Se hai ricevuto una mail di invito da parte di un tuo amico, inserisci il codice qui sotto per poter partecipare","Inserisci codice di invito"));
        modelCheckCasas.add(new ModelCheckCasa(R.drawable.imm, "CREA UNA CASA", "Crea subito una casa se non ne possiedi già una e invita tutti i tuoi amici",""));

        adapterCheckCasa = new AdapterCheckCasa(modelCheckCasas, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapterCheckCasa);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapterCheckCasa.getCount() -1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                }

                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}