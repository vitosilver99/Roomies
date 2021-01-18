package com.example.roomies.home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.roomies.LoginActivity;
import com.example.roomies.MainActivity;
import com.example.roomies.R;
import com.example.roomies.calendario.EventiClass;
import com.example.roomies.calendario.MansioniClass;
import com.example.roomies.calendario.UtentiClass;
import com.example.roomies.pagamenti.ModelloPagamento;
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
    private static final String ARG_NOME_USER = "param3";
    private static final String ARG_COGNOME_USER = "param4";

    private String casaId;
    private String userId;
    private String nomeUser;
    private String cognomeUser;

    private FirebaseFirestore firebaseFirestore;

    //non è stato aggiunto da me ma serve nella parte che devo implementare
    private FirestoreRecyclerAdapterSpesaHome spesaAdapter;
    private FirestoreRecyclerAdapterEventiHome eventiAdapter;


    private RecyclerView listaSpesa;


    private FirestoreRecyclerAdapterPagamentiHome pagamentiAdapter;
    private RecyclerView listaPagamenti;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2, String param3,  String param4) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, param1);
        args.putString(ARG_CASA_ID, param2);
        args.putString(ARG_NOME_USER, param3);
        args.putString(ARG_COGNOME_USER, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            casaId = getArguments().getString(ARG_CASA_ID);
            nomeUser = getArguments().getString(ARG_NOME_USER);
            cognomeUser = getArguments().getString(ARG_COGNOME_USER);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //logout
        ImageView btn_logout = view.findViewById(R.id.btn_logoout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


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
        RecyclerView listaEventi = view.findViewById(R.id.recyclerView_eventi_giornalieri_home);

        listaEventi.setHasFixedSize(true);
        listaEventi.setLayoutManager(new LinearLayoutManager(this.getContext()));

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.d("data",currentDate);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date today = format.parse(currentDate);
            Log.d("data_data", today + "");

            Query queryEventi = firebaseFirestore.collection("case").document(casaId).collection("eventi")
                    .whereEqualTo("giorno", today);

            queryEventi.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    Log.d("queryeventi",queryDocumentSnapshots.size()+"");
                }
            });

            FirestoreRecyclerOptions<ModelloEventoHome> optionsEventi = new FirestoreRecyclerOptions.Builder<ModelloEventoHome>()
                    .setLifecycleOwner(this)
                    .setQuery(queryEventi, new SnapshotParser<ModelloEventoHome>() {
                        @NonNull
                        @Override
                        public ModelloEventoHome parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                            Log.d("parser","sono nel parser");

                            Map<String, Object> map = snapshot.getData();
                            String listaUtenti = "";
                            for(Map.Entry<String, Object> entry : map.entrySet()) {
                                if (entry.getKey().equals("coinquilini")) {
                                    ArrayList<Map<String,String>> utentiClassArrayList = (ArrayList<Map<String, String>>) entry.getValue();
                                    Log.d("parser",utentiClassArrayList.size()+"");

                                    for(int i=0;i<utentiClassArrayList.size()-1;i++) {
                                        listaUtenti= listaUtenti + (utentiClassArrayList.get(i).get("nome_cognome")+", ");
                                    }
                                    listaUtenti= listaUtenti + (utentiClassArrayList.get(utentiClassArrayList.size()-1).get("nome_cognome"));
                                }
                            }
                            ModelloEventoHome modelloEventoHome = new ModelloEventoHome(snapshot.get("nome").toString(),listaUtenti);
                            Toast.makeText(getContext(),listaUtenti,Toast.LENGTH_LONG).show();
                            Log.d("parse lista utenti",listaUtenti);
                            return modelloEventoHome;
                        }
                    }).build();
            eventiAdapter = new FirestoreRecyclerAdapterEventiHome(optionsEventi);
            listaEventi.setAdapter(eventiAdapter);

        }
        catch (Exception e) {

        }

        eventiAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            public void checkEmpty(){
                ConstraintLayout constraintLayout = view.findViewById(R.id.empty_eventi_home);
                if(eventiAdapter.getItemCount() == 0) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    listaEventi.setVisibility(View.INVISIBLE);
                }
                else {
                    constraintLayout.setVisibility(View.INVISIBLE);
                    listaEventi.setVisibility(View.VISIBLE);
                }
            }
        });

        listaSpesa = (RecyclerView) view.findViewById(R.id.lista_spesa_home);

        listaSpesa.setHasFixedSize(true);
        listaSpesa.setLayoutManager(new LinearLayoutManager(this.getContext()));



        //query per ottenere la lista della spesa
        Query querySpesa = firebaseFirestore.collection("case").document(casaId)
                .collection("lista_spesa").whereEqualTo("da_comprare",true).orderBy("nome_articolo");


        /*

        querySpesa.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int sizequery = queryDocumentSnapshots.size();
                Log.d("query numero articoli",sizequery+"");
            }
        });

         */
        FirestoreRecyclerOptions<ModelloArticoloHome> optionsSpesa = new FirestoreRecyclerOptions.Builder<ModelloArticoloHome>()
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

        spesaAdapter = new FirestoreRecyclerAdapterSpesaHome(optionsSpesa, this);
        listaSpesa.setAdapter(spesaAdapter);

        //todo togli la scritta articolo dal modello del singolo articolo (chiedi a vito)

        //lista spesa vuota
        ImageView lista_spesa_vuota = view.findViewById(R.id.lista_spesa_vuota_home);
        TextView lista_spesa_vuota_text = view.findViewById(R.id.lista_spesa_vuota_home_text);
        spesaAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            void checkEmpty() {
                if(spesaAdapter.getItemCount() == 0) {
                    lista_spesa_vuota.setVisibility(View.VISIBLE);
                    lista_spesa_vuota_text.setVisibility(View.VISIBLE);
                    listaSpesa.setVisibility(View.GONE);
                    Log.d("elementi recycler spesa",spesaAdapter.getItemCount()+"");
                }
                else {
                    lista_spesa_vuota.setVisibility(View.GONE);
                    lista_spesa_vuota_text.setVisibility(View.GONE);
                    listaSpesa.setVisibility(View.VISIBLE);
                    Log.d("elementi recycler spesa",spesaAdapter.getItemCount()+"");
                }

            }
        });






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
                            DocumentReference docRef = firebaseFirestore.collection("case")
                                    .document(casaId)
                                    .collection("lista_spesa")
                                    .document(spesaAdapter.getItem(i).getArticolo_id());
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


        //todo fare lista pagamenti
        //modello pagamento nome, importo totale, quanti non hanno ancora pagato



        listaPagamenti = (RecyclerView) view.findViewById(R.id.lista_pagamenti_home);

        listaPagamenti.setHasFixedSize(true);
        listaPagamenti.setLayoutManager(new LinearLayoutManager(this.getContext()));



        HashMap<String,Object> checkUtenteInteressato = new HashMap<>();
        checkUtenteInteressato.put("id_utente",userId);
        checkUtenteInteressato.put("pagato",false);
        checkUtenteInteressato.put("nome_cognome",nomeUser+" "+cognomeUser);
        Query queryPagamenti = firebaseFirestore.collection("case")
                .document(casaId)
                .collection("pagamenti")
                .whereArrayContains("interessati",checkUtenteInteressato)
                .whereNotEqualTo("non_pagato",0)
                .orderBy("non_pagato", Query.Direction.DESCENDING)
                .orderBy("scadenza_pagamento");
        //todo modificare il layout con vito in modo che si capisca che i pagamenti sono solo quelli che interessano all'utente


        queryPagamenti.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int sizequery = queryDocumentSnapshots.size();
                Log.d("query numero pagamenti",sizequery+"");
            }
        });


        FirestoreRecyclerOptions<ModelloPagamento> optionsPagamenti = new FirestoreRecyclerOptions.Builder<ModelloPagamento>()
                .setLifecycleOwner(this)
                .setQuery(queryPagamenti, new SnapshotParser<ModelloPagamento>() {
                    @NonNull
                    @Override
                    public ModelloPagamento parseSnapshot(@NonNull DocumentSnapshot snapshot) {

                        ModelloPagamento pagamento = snapshot.toObject(ModelloPagamento.class);
                        String pagamento_id = snapshot.getId();
                        pagamento.setPagamento_id(pagamento_id);
                        return pagamento;
                    }
                })
                .build();

        pagamentiAdapter = new FirestoreRecyclerAdapterPagamentiHome(optionsPagamenti);
        listaPagamenti.setAdapter(pagamentiAdapter);


        ImageView lista_pagamenti_vuota = view.findViewById(R.id.lista_pagamenti_vuota_home);
        TextView lista_pagamenti_vuota_text = view.findViewById(R.id.lista_pagamenti_vuota_home_text);
        pagamentiAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            void checkEmpty() {
                if(pagamentiAdapter.getItemCount() == 0) {
                    lista_pagamenti_vuota.setVisibility(View.VISIBLE);
                    lista_pagamenti_vuota_text.setVisibility(View.VISIBLE);
                    listaPagamenti.setVisibility(View.GONE);
                    Log.d("elementi recycler spesa",pagamentiAdapter.getItemCount()+"");
                }
                else {
                    lista_pagamenti_vuota.setVisibility(View.GONE);
                    lista_pagamenti_vuota_text.setVisibility(View.GONE);
                    listaPagamenti.setVisibility(View.VISIBLE);
                    Log.d("elementi recycler spesa",pagamentiAdapter.getItemCount()+"");
                }

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