package com.example.roomies;

import android.content.Intent;
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


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";


    private String casaID;
    private String userID;


    private RecyclerView listaPagamenti;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreAdapterPagamento adapter;

    public PagamentiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment com.example.roomies.PagamentiFragment.
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
            userID = getArguments().getString(ARG_USER_ID);
            casaID = getArguments().getString(ARG_CASA_ID);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagamenti, container, false);

        Log.d("CASA_ID", casaID);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //cast in RecyclerView necessario?
        listaPagamenti = (RecyclerView) view.findViewById(R.id.lista_pagamenti);

        //configurazione per la paginazione
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(2)
                .setPageSize(2)
                .build();

        //query da firestore
        Query query= firebaseFirestore.collection("case").document(casaID).collection("pagamenti").limit(10);

        //opzioni del recycler
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


    @Override
    public void onPagamentoClick(DocumentSnapshot snapshot, int position) {
        Log.d("PAGAMENTO_CLICK","click su elemento numero" + position + " con id uguale a " + snapshot.getId());

        //passa all'activity che contiene i dettagli del pagamento
        Intent intent =new Intent(getContext(),ModelloPagamento.class);
        //Log.d("user id :",task.getResult().getUser().getUid());
        //intent.putExtra("userID",task.getResult().getUser().getUid());


        startActivity(intent);
    }
}