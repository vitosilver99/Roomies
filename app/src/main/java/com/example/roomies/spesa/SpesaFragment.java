package com.example.roomies.spesa;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomies.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpesaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpesaFragment extends Fragment implements FirestoreRecyclerAdapterSpesa.OnArticoloInteraction{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";

    // TODO: Rename and change types of parameters
    private String casaId;
    private String userId;

    private RecyclerView listaSpesa;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapterSpesa spesaAdapter;

    private TextView lista_spesa_vuota;
    public SpesaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment com.example.roomies.spesa.SpesaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpesaFragment newInstance(String param1, String param2) {
        SpesaFragment fragment = new SpesaFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spesa, container, false);
        listaSpesa=(RecyclerView) view.findViewById(R.id.lista_spesa);
        lista_spesa_vuota = view.findViewById(R.id.lista_spesa_vuota);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //query per ottenere la lista della spesa
        Query query = firebaseFirestore.collection("case").document(casaId).collection("lista_spesa").orderBy("da_comprare").orderBy("nome_articolo");

        FirestoreRecyclerOptions<ModelloArticolo> options = new FirestoreRecyclerOptions.Builder<ModelloArticolo>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<ModelloArticolo>() {
                    @NonNull
                    @Override
                    public ModelloArticolo parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        //non funziona per via del boolean
                        //ModelloArticolo articolo = snapshot.toObject(ModelloArticolo.class);
                        Log.d("ciao boolean",snapshot.get("da_comprare").getClass().getCanonicalName());

                        ModelloArticolo articolo = new ModelloArticolo(snapshot.getString("nome_articolo"),snapshot.getBoolean("da_comprare"),snapshot.getId());
                        return articolo;
                    }
                })
                .build();

        spesaAdapter = new FirestoreRecyclerAdapterSpesa(options,this);
        Log.d("FRAGSPESA elementi",spesaAdapter.getItemCount()+"");
        listaSpesa.setHasFixedSize(true);
        listaSpesa.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listaSpesa.setAdapter(spesaAdapter);


        //todo controllo lista vuota. se vuota metti immagine di sfondo. capire se fare in questo modo utilizzando un observer o fare override del metodo onattachedtorecyclerview dell'adapter
        spesaAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            void checkEmpty() {
                if(spesaAdapter.getItemCount() == 0) {
                    lista_spesa_vuota.setVisibility(View.VISIBLE);
                    listaSpesa.setVisibility(View.GONE);
                    Log.d("elementi recycler spesa",spesaAdapter.getItemCount()+"");
                }
                else {
                    lista_spesa_vuota.setVisibility(View.GONE);
                    listaSpesa.setVisibility(View.VISIBLE);
                    Log.d("elementi recycler spesa",spesaAdapter.getItemCount()+"");
                }

            }
        });



        FloatingActionButton fab = view.findViewById(R.id.add_articolo_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_aggiungi_articolo);
                Button aggiungi_articolo=dialog.findViewById(R.id.button_aggiungi_articolo);
                TextView nome_articolo_inserito=dialog.findViewById(R.id.inserisci_nome_articolo);
                aggiungi_articolo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("ciaos",nome_articolo_inserito.getText().toString());
                        if(nome_articolo_inserito.getText().toString().equals("")) {
                            //todo fare un controllo più carino
                            Log.d("ramo no","no");
                            Toast.makeText(getContext(),"nome articolo vuoto",Toast.LENGTH_LONG).show();

                        }
                        else {
                            Log.d("ramo si ","si");
                            Map<String,Object> nuovoArticolo= new HashMap<>();
                            nuovoArticolo.put("nome_articolo",nome_articolo_inserito.getText().toString());
                            nuovoArticolo.put("da_comprare",true);
                            firebaseFirestore.collection("case").document(casaId).collection("lista_spesa").add(nuovoArticolo);

                            dialog.hide();
                        }

                    }
                });


                dialog.show();
            }
        });



        return view;
    }

    @Override
    public void onArticoloClick(ModelloArticolo articolo, int position) {
        firebaseFirestore.collection("case").document(casaId).collection("lista_spesa").document(articolo.getArticolo_id()).update("da_comprare",!articolo.getDa_comprare());
    }

    @Override
    public void onArticoloLongClick(ModelloArticolo articolo, int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_rimuovi_articolo);


        Button rimuovi_articolo= dialog.findViewById(R.id.rimuovi_articolo);
        rimuovi_articolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo notificare observer
                firebaseFirestore.collection("case").document(casaId).collection("lista_spesa").document(articolo.getArticolo_id()).delete();
                dialog.hide();
            }
        });

        dialog.show();


    }
}