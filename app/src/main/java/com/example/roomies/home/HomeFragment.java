package com.example.roomies.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roomies.R;
import com.example.roomies.calendario.EventiClass;
import com.example.roomies.calendario.MansioniClass;
import com.example.roomies.calendario.UtentiClass;
import com.example.roomies.spesa.FirestoreRecyclerAdapterSpesa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {


    private static final String ARG_PARAM1 = "casaId";


    private String casaId;

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapterSpesa spesaAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            casaId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        //prendo tutti gli utenti presenti nella casa cosi da inserirli nella recyclerView profili della home
        firebaseFirestore.collection("case").document(casaId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> map = document.getData();
                    List<UtentiClass> utentiClasses = new ArrayList<>();


                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getKey().equals("utenti")) {
                            ArrayList arrayList = (ArrayList) entry.getValue();
                            for(int i=0; i<arrayList.size(); i++ )
                            {
                                Map<String, Object> map_utenti = (Map<String, Object>) arrayList.get(i);
                                if(!map_utenti.get("user_id").toString().equals(firebaseAuth.getUid()))
                                {
                                    UtentiClass utenti = new UtentiClass(map_utenti.get("nome_cognome").toString(),map_utenti.get("user_id").toString());
                                    utentiClasses.add(utenti);
                                }
                            }
                        }
                    }
                    UtentiClass utente_fittizio = new UtentiClass("","");
                    utentiClasses.add(utente_fittizio);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView_home_utenti);
                    recyclerView.setLayoutManager(layoutManager);

                    RecyclerViewAdapterHomeProfili adapterHomeProfili = new RecyclerViewAdapterHomeProfili(getContext(),utentiClasses,utentiClasses.size()-1);

                    recyclerView.setAdapter(adapterHomeProfili);
                }else
                {

                }
            }
        });

        //query per visualizzare gli eventi giornalieri

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.d("data",currentDate);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date today = format.parse(currentDate);
            Log.d("data_data",today+"");
            Query query = firebaseFirestore.collection("case").document(casaId).collection("eventi")
                    .whereEqualTo("giorno",today);

            Log.d("giorno", today+"");
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    if(!documentSnapshots.isEmpty()){
                        List<EventiClass> eventi = new ArrayList<>();
                        List<UtentiClass> utenti = new ArrayList<>();

                        Log.d("grandezza documenti", documentSnapshots.size()+"");
                        int i;
                        for( i =0; i<documentSnapshots.size();i++) {
                            Map<String, Object> map = documentSnapshots.getDocuments().get(i).getData();
                            String nome = "";
                            for(Map.Entry<String, Object> entry : map.entrySet()){
                                if(entry.getKey().equals("coinquilini")){
                                    ArrayList arrayList = (ArrayList) entry.getValue();
                                    UtentiClass utenti_partecipanti;
                                    for(int j=0; j<arrayList.size(); j++ )
                                    {
                                        Map<String, Object> map_utenti = (Map<String, Object>) arrayList.get(j);
                                        utenti_partecipanti = new UtentiClass(map_utenti.get("nome_cognome").toString(),map_utenti.get("userId").toString());
                                        utenti.add(utenti_partecipanti);
                                    }
                                }
                                if(entry.getKey().equals("nome")){

                                    nome = (String) entry.getValue();
                                }
                            }
                            System.out.println("numero for"+i);
                            EventiClass eventi_giornalieri = new EventiClass(nome,utenti);


                            eventi.add(eventi_giornalieri);
                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                        RecyclerViewAdapterEventoGironaliero recyclerViewAdapter = new RecyclerViewAdapterEventoGironaliero(getContext(),eventi);
                        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_eventi_giornalieri_home);

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(recyclerViewAdapter);


                    }else{
                        //TODO disattivare la recyclerview e fare uscire un messaggio di incitare ad aggiungere un evento, oppure che non ci sono eventi da mostrare
                    }
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }








        // Inflate the layout for this fragment
        return view;
    }
}