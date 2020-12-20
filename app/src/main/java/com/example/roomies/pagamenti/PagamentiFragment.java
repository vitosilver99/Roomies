package com.example.roomies.pagamenti;

import android.app.Activity;
import android.app.Dialog;
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

import com.example.roomies.R;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PagamentiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagamentiFragment extends Fragment implements FirestoreAdapterPagamento.OnListaPagamentoClick {


    //casa di riferimento per i test: p60qZKwoxHmFn8KXYzEG

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";
    private static final String ARG_PAGAMENTO_ID = "param3";



    private String casaId;
    private String userId;


    private RecyclerView listaPagamenti;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreAdapterPagamento adapter;

    Activity main;

    //creare dialogbox relativa ai dettagli di un pagamento


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
                .setInitialLoadSizeHint(2)
                .setPageSize(2)
                .build();

        //query da firestore
        Query query= firebaseFirestore.collection("case").document(casaId).collection("pagamenti").limit(10);

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


        adapter = new FirestoreAdapterPagamento(options,this);

        listaPagamenti.setHasFixedSize(true);
        listaPagamenti.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listaPagamenti.setAdapter(adapter);
        return view;
    }

    //implemento l'interfaccia all'interno di FirestoreAdapterPagamento
    @Override
    public void onPagamentoClick(DocumentSnapshot snapshot, int position) {
        Log.d("PAGAMENTO_CLICK","click su elemento numero" + position + " con id uguale a " + snapshot.getId()+snapshot.getString("nome_pagamento"));

        //inserisco una finestra di dialogo che mi fornisce i dettagli di un pagamento
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_dettagli_pagamento);

        //TODO: forse non bisogna mettere final
        final TextView nomePagamento = dialog.findViewById(R.id.dettaglio_nome_pagamento);
        final TextView scadenzaPagamento = dialog.findViewById(R.id.dettaglio_data_scadenza);
        final TextView importoTotalePagamento = dialog.findViewById(R.id.dettaglio_importo_totale);
        final TextView importoSingoloPagamento = dialog.findViewById(R.id.dettaglio_importo_singolo);
        final Button confermaPagamento = dialog.findViewById(R.id.dettaglio_conferma_pagamento_button);
        final RecyclerView listaInteressati = dialog.findViewById(R.id.dettaglio_lista_interessati_pagamento);

        nomePagamento.setText(snapshot.getString("nome_pagamento"));
        scadenzaPagamento.setText(snapshot.get("scadenza_pagamento").toString());
        importoTotalePagamento.setText(snapshot.get("importo_totale").toString());
        importoSingoloPagamento.setText(snapshot.get("importo_singolo").toString());
        Log.d("ARRAY",snapshot.get("interessati").toString());

        //se non funziona this.getContext() prova getActivity();
        InteressatiAdapter interessatiAdapter = new InteressatiAdapter(this.getContext(),snapshot.getData());
        listaInteressati.setAdapter(interessatiAdapter);
        listaInteressati.setLayoutManager(new LinearLayoutManager(this.getContext()));
        dialog.show();


    }
}