package com.example.roomies.home;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.roomies.R;
import com.example.roomies.calendario.EventiClass;
import com.example.roomies.calendario.MansioniClass;
import com.example.roomies.calendario.UtentiClass;
import com.example.roomies.spesa.FirestoreRecyclerAdapterSpesa;
import com.example.roomies.spesa.ModelloArticolo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements FirestoreRecyclerAdapterSpesaHome.OnArticoloInteraction{



    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";

    private String casaId;
    private String userId;


    private FirebaseFirestore firebaseFirestore;

    //non è stato aggiunto da me ma serve nella parte che devo implementare
    private FirestoreRecyclerAdapterSpesaHome spesaAdapter;


    private RecyclerView listaSpesa;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, param1);
        args.putString(ARG_CASA_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            casaId = getArguments().getString(ARG_CASA_ID);
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();


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
                                if(!map_utenti.get("user_id").toString().equals(userId))
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




        listaSpesa = (RecyclerView) view.findViewById(R.id.lista_spesa_home);

        listaSpesa.setHasFixedSize(true);
        listaSpesa.setLayoutManager(new LinearLayoutManager(this.getContext()));





        //query per ottenere la lista della spesa
        Query querySpesa = firebaseFirestore.collection("case").document(casaId).collection("lista_spesa").whereEqualTo("da_comprare",true).orderBy("nome_articolo");


        /*

        querySpesa.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int sizequery = queryDocumentSnapshots.size();
                Log.d("query numero articoli",sizequery+"");
            }
        });

         */
        FirestoreRecyclerOptions<ModelloArticoloHome> options = new FirestoreRecyclerOptions.Builder<ModelloArticoloHome>()
                .setLifecycleOwner(this)
                .setQuery(querySpesa, new SnapshotParser<ModelloArticoloHome>() {
                    @NonNull
                    @Override
                    public ModelloArticoloHome parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        //non funziona per via del boolean
                        //ModelloArticolo articolo = snapshot.toObject(ModelloArticolo.class);
                        Log.d("ciao boolean",snapshot.get("da_comprare").getClass().getCanonicalName());

                        ModelloArticoloHome articolo = new ModelloArticoloHome(snapshot.getString("nome_articolo"),snapshot.getId());
                        return articolo;
                    }
                })
                .build();

        spesaAdapter = new FirestoreRecyclerAdapterSpesaHome(options, this);
        Log.d("FRAGHOME elementi",spesaAdapter.getItemCount()+"");
        listaSpesa.setAdapter(spesaAdapter);
        Log.d("FRAGHOME elementi",spesaAdapter.getItemCount()+"");





        Button spesa_fatta = view.findViewById(R.id.spesa_fatta);
        spesa_fatta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());

                dialog.setContentView(R.layout.dialog_conferma_spesa_fatta);

                Button spesa_fatta = dialog.findViewById(R.id.conferma_spesa_fatta);
                spesa_fatta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get a new write batch
                        WriteBatch batch = firebaseFirestore.batch();




                        for(int i=0;i<spesaAdapter.getItemCount();i++) {
                            DocumentReference docRef = firebaseFirestore.collection("case").document(casaId).collection("lista_spesa").document(spesaAdapter.getItem(i).getArticolo_id());
                            batch.update(docRef,"da_comprare",false);
                        }



                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(),"Spesa fatta",Toast.LENGTH_LONG).show();
                                dialog.hide();
                            }
                        });
                    }
                });


                dialog.show();



            }
        });


        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onArticoloClick(ModelloArticoloHome modelloArticoloHome, int position) {

    }

    @Override
    public void onArticoloLongClick(ModelloArticoloHome modelloArticoloHome, int position) {

    }
}