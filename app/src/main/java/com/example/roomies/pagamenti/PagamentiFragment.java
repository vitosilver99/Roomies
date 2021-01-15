package com.example.roomies.pagamenti;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//aggiunto io

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomies.R;
import com.example.roomies.calendario.UtentiClass;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;


//TODO IL TASTO PER RIMUOVERE UN INTERESSATO NEL POPUP DI CREAZIONE DI UN NUOVO PAGAMENTO VIENE COPERTO DAL NOME TROPPO LUNGO
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PagamentiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagamentiFragment extends Fragment implements FirestorePagingAdapterPagamenti.OnPagamentoInteraction {


    //casa di riferimento per i test: p60qZKwoxHmFn8KXYzEG

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";
    private static final String ARG_PAGAMENTO_ID = "param3";



    private String casaId;
    private String userId;


    private RecyclerView listaPagamenti;
    private FirebaseFirestore firebaseFirestore;
    private FirestorePagingAdapterPagamenti pagamentiAdapter;

    //Activity main;
    
    //attributi necessari per add_pagamento_floating
    DocumentSnapshot documentoCasa;
    ArrayList<UtentiClass> listaUtentiPagamento;

    //TODO creare dialogbox relativa ai dettagli di un pagamento


    public PagamentiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment com.example.roomies.pagamenti.PagamentiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PagamentiFragment newInstance(String param1, String param2) {
        PagamentiFragment fragment = new PagamentiFragment();
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
            userId = getArguments().getString(ARG_USER_ID);
            casaId = getArguments().getString(ARG_CASA_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        //TODO ATTENZIONE HO MODIFICATO attachtoRoot a true (default false)
        View view = inflater.inflate(R.layout.fragment_pagamenti, container, false);

        Log.d("CASA_ID", casaId);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //cast in RecyclerView necessario?
        listaPagamenti = (RecyclerView) view.findViewById(R.id.lista_pagamenti);

        //configurazione per la paginazione
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(6)
                .setPageSize(6)
                .build();

        //query da firestore
        Query query= firebaseFirestore.collection("case").document(casaId).collection("pagamenti").limit(10).orderBy("non_pagato", Query.Direction.DESCENDING).orderBy("scadenza_pagamento");

        //opzioni del FirestorePagingAdapter customizzato cioè del FirestoreAdapterPagamento

        FirestorePagingOptions<ModelloPagamento> options = new FirestorePagingOptions.Builder<ModelloPagamento>()

                //imposta l'adapter in modo che esso si fermi quando il fragment si ferma e parta quando il fragment parte
                .setLifecycleOwner(this)
                .setQuery(query, config, new SnapshotParser<ModelloPagamento>() {
                    @NonNull
                    @Override
                    public ModelloPagamento parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        //prendi un documento dallo snapshot e inseriscilo in una riga
                        ModelloPagamento pagamentoRiga = snapshot.toObject(ModelloPagamento.class);
                        String pagamento_id = snapshot.getId();
                        pagamentoRiga.setPagamento_id(pagamento_id);
                        return pagamentoRiga;
                    }
                })
                .build();


        pagamentiAdapter = new FirestorePagingAdapterPagamenti(options,this);

        listaPagamenti.setHasFixedSize(true);
        listaPagamenti.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listaPagamenti.setAdapter(pagamentiAdapter);


        //todo controllo lista vuota. se vuota metti immagine di sfondo
        pagamentiAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("change",pagamentiAdapter.getItemCount()+"");

            }
        });
        Log.d("observers",pagamentiAdapter.hasObservers()+"");





        //aggiunta button per creare un nuovo documento di pagamento (modifico codice di vito da CalendarFragment)
        FloatingActionButton fab =  view.findViewById(R.id.add_pagamento_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Prendo il documento associato alla casa inerente all'utente loggato e creo la lista utenti
                Task<DocumentSnapshot> documentSnapshot =  firebaseFirestore.collection("case").document(casaId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        documentoCasa = document;
                                        Log.d("Numero casa  : ",documentoCasa.get("numero_utenti").toString());

                                        Map<String, Object> mappaCasa = documentoCasa.getData();
                                        Log.d("mappa documento",mappaCasa+"");


                                        listaUtentiPagamento = new ArrayList<UtentiClass>();

                                        //creo la lista degli utenti. Codice simile si trova in InteressatiAdapter
                                        for (Map.Entry<String, Object> entryCasa : mappaCasa.entrySet()) {
                                            if (entryCasa.getKey().equals("utenti")) {
                                                ArrayList arrayList = (ArrayList) entryCasa.getValue();
                                                for(int i=0; i<arrayList.size(); i++ )
                                                {
                                                    Map<String, Object> mappaUtente = (Map<String, Object>) arrayList.get(i);
                                                    UtentiClass utenti = new UtentiClass(mappaUtente.get("nome_cognome").toString(),mappaUtente.get("user_id").toString());
                                                    listaUtentiPagamento.add(utenti);
                                                }
                                                Log.d("numero elementi ",""+ listaUtentiPagamento.size());
                                            }
                                        }

                                        PopUpClassNuovoPagamento popUpClassNuovoPagamento = new PopUpClassNuovoPagamento(listaUtentiPagamento,casaId);
                                        popUpClassNuovoPagamento.showPopupWindow(view);
                                    } else {
                                        Toast.makeText(view.getContext(),"Errore di connessione", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Log.d(TAG,"get failed with ", task.getException());
                                }
                            }
                        });
            }
        });
        
        
        
        return view;
    }

    //implemento l'interfaccia all'interno di FirestoreAdapterPagamento
    @Override
    public void onPagamentoClick(DocumentSnapshot snapshot, int position) {
        Log.d("PAGAMENTO_CLICK","click su elemento numero" + position + " con id uguale a " + snapshot.getId()+snapshot.getString("nome_pagamento"));

        //inserisco una finestra di dialogo che mi fornisce i dettagli di un pagamento
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_dettagli_pagamento);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        //TODO: forse non bisogna mettere final
        final TextView nomePagamento = dialog.findViewById(R.id.dettaglio_nome_pagamento);
        final TextView scadenzaPagamento = dialog.findViewById(R.id.dettaglio_data_scadenza);
        final TextView importoTotalePagamento = dialog.findViewById(R.id.dettaglio_importo_totale);
        final TextView importoSingoloPagamento = dialog.findViewById(R.id.dettaglio_importo_singolo);
        final Button confermaPagamento = dialog.findViewById(R.id.dettaglio_conferma_pagamento_button);
        final RecyclerView listaInteressati = dialog.findViewById(R.id.dettaglio_lista_interessati_pagamento);


        nomePagamento.setText(snapshot.getString("nome_pagamento"));
        scadenzaPagamento.setText(snapshot.getDate("scadenza_pagamento").toLocaleString());
        importoTotalePagamento.setText(snapshot.get("importo_totale").toString());
        importoSingoloPagamento.setText(snapshot.get("importo_singolo").toString());
        Log.d("ARRAY",snapshot.get("interessati").toString());

        //se non funziona this.getContext() prova getActivity();
        InteressatiAdapter interessatiAdapter = new InteressatiAdapter(this.getContext(),snapshot.getData());
        listaInteressati.setAdapter(interessatiAdapter);
        listaInteressati.setLayoutManager(new LinearLayoutManager(this.getContext()));



        confermaPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean utentePresente=false;
                Log.d("LISTAINTERESSTI",interessatiAdapter.getListaInteressati().toString());
                ModelloInteressato interessato;
                Iterator<ModelloInteressato> iterator = interessatiAdapter.getListaInteressati().iterator();
                while (iterator.hasNext()){
                    interessato = iterator.next();
                    Log.d("INTERESSATO", interessato.getId_utente());
                    Log.d("USERID", userId);

                    //se l'utente che sta utilizzando l'app è tra i vari interessati al pagamento
                    if(interessato.getId_utente().equals(userId)) {
                        utentePresente=true;
                        Log.d("USERPRESENTE", "utente presente");
                        //salvo interessato in una variabile dummy perchè gli aggiornamenti su firestore vengono fatti in maniera asincrona e quindi il secondo aggiornamento potrebbe lavorare con
                        // un valore dell'iteratore non corretto (l'iteratore potrebbe essere passato all'interessato successivo al momento di eseguire il secondo aggiornamento)
                        boolean pagato = interessato.isPagato();

                        if(pagato) {

                            //main  oppure getContext()?
                            Toast.makeText(getContext(), "Questo pagamento è stato già saldato", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ModelloInteressato interessatoAggiornato=new ModelloInteressato(interessato.getNome_cognome(),interessato.getId_utente(),true);
                            firebaseFirestore.getInstance().document(snapshot.getReference().getPath()).update("interessati",FieldValue.arrayRemove(interessato.convertiInHashMap()),"interessati",FieldValue.arrayUnion(interessatoAggiornato.convertiInHashMap()),"non_pagato",FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Il pagamento è stato saldato", Toast.LENGTH_SHORT).show();
                                    //CONTROLLA SE VENGONO MODIFICATI GLI ELEMENTI sia nel dialog che nel fragment
                                    //probabilmente verranno aggiornati solo gli elementi nel fragment perchè paga fa riferimento ai dati di firestore
                                    //mentre interessatiAdapter è un adapter che fa riferimento a dati contenuti al suo interno e che quindi non vengono più aggiornati dopo la sua creazione


                                    //aggiorna adapter degli interessati
                                    interessatiAdapter.setPagato(userId);

                                    //aggiorna adapter pagamenti in background
                                    pagamentiAdapter.refresh();



                                }
                            });
                        }
                    }
                    
                }
                if(!utentePresente) {
                    Toast.makeText(getContext(), "Non sei interessato da questo pagamento", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //ATTENZIONE RICORDATI SEMPRE DI MOSTRARE IL DIALOGO
        dialog.show();
    }

    @Override
    public void onPagamentoLongClick(DocumentSnapshot snapshot, int position) {
        Log.d("PAGAMENTO_LONG_CLICK","long click su elemento numero" + position + " con id uguale a " + snapshot.getId()+snapshot.getString("nome_pagamento"));

        //inserisco una finestra di dialogo che mi fornisce i dettagli di un pagamento
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_rimuovi_pagamento);

        Button rimuovi_pagamento= dialog.findViewById(R.id.rimuovi_pagamento);
        rimuovi_pagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot.getReference().delete();
                dialog.hide();
                pagamentiAdapter.refresh();
            }
        });

        dialog.show();
    }


}