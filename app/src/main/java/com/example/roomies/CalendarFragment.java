package com.example.roomies;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.MoreObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CalendarFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MainActivity mainActivity;
    //FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UdCasa;
    DocumentSnapshot DocumentoCasa;
    ArrayList<UtentiClass> utentiClasses;
    ArrayList<MansioniClass> mansioniClasses;

    public CalendarFragment(String UdCasa) {
        this.UdCasa = UdCasa;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fStore = FirebaseFirestore.getInstance();

        //Log.d("Numero_utenti",""+documentSnapshot.get("numero_utenti").toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_calendar, container, false);
        //Toast.makeText(view.getContext(),"Mi trovo nel fragment"+ fAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();

        Log.d("Casa id: ",""+UdCasa);

        //Prendo il documento associato alla casa inerente all'utente loggato
        Task<DocumentSnapshot> documentSnapshot =  fStore.collection("case").document(UdCasa).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                DocumentoCasa = document;
                                Log.d("Numero casa  : ",DocumentoCasa.get("numero_utenti").toString());

                                Map<String, Object> map = DocumentoCasa.getData();
                                mansioniClasses = new ArrayList<MansioniClass>();

                                //creo la lista delle mansioni
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    if (entry.getKey().equals("lista_mansioni")) {
                                        ArrayList arrayList = (ArrayList) entry.getValue();
                                        for(int i=0; i<arrayList.size(); i++ )
                                        {
                                            Log.d("numero mansioni ", arrayList.get(i).toString()+"");
                                            MansioniClass utenti = new MansioniClass(arrayList.get(i).toString());
                                            mansioniClasses.add(utenti);
                                        }
                                        Log.d("numero mansioni ", mansioniClasses.size()+"");

                                        /*
                                        mansioniClasses = (ArrayList<MansioniClass>) entry.getValue();
                                        Log.d("TAG", entry.getValue().toString());
                                        Log.d("TAG", "Numero: "+mansioniClasses.size() + " Primo elemento: "+mansioniClasses.get(0));*/
                                    }
                                }

                                utentiClasses = new ArrayList<UtentiClass>();

                                //creo la lista degli utenti
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    if (entry.getKey().equals("utenti")) {
                                        ArrayList arrayList = (ArrayList) entry.getValue();
                                        for(int i=0; i<arrayList.size(); i++ )
                                        {
                                            Map<String, Object> map_utenti = (Map<String, Object>) arrayList.get(i);
                                            UtentiClass utenti = new UtentiClass(map_utenti.get("nome_cognome").toString(),map_utenti.get("user_id").toString());
                                            utentiClasses.add(utenti);
                                        }
                                        Log.d("numero elementi ",""+utentiClasses.size());
                                        Log.d("elemento 0 ",""+utentiClasses.get(0).getNome_cognome());
                                        Log.d("elemento 1 ",""+utentiClasses.get(1).getNome_cognome());
                                    }
                                }

                            } else {
                                Toast.makeText(view.getContext(),"Errore di connessione", Toast.LENGTH_LONG);
                            }
                        } else {
                            Log.d(TAG,"get failed with ", task.getException());
                        }
                    }
                });


        //Log.d("Numero utenti: ",""+mansioniClasses.getNome());

        FloatingActionButton fab =  view.findViewById(R.id.add_event_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpClass popUpClass = new PopUpClass(utentiClasses,mansioniClasses);
                popUpClass.showPopupWindow(view);
            }
        });



        return view;
    }



}