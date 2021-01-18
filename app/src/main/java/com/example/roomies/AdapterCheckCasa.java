package com.example.roomies;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AdapterCheckCasa extends PagerAdapter {


    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";
    private static final String ARG_NOME_USER = "param3";
    private static final String ARG_COGNOME_USER = "param4";


    private List<ModelCheckCasa> modelCheckCasas;
    private LayoutInflater layoutInflater;
    private CheckCasaActivity context;

    public AdapterCheckCasa(List<ModelCheckCasa> modelCheckCasas, CheckCasaActivity context) {
        this.modelCheckCasas = modelCheckCasas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modelCheckCasas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_view_registrazione, container, false);

        ImageView imageView;
        TextView title, desc;
        TextView fieldCasaId;
        Button partecipa_casa;
        Button crea_casa;
        fieldCasaId = view.findViewById(R.id.textView_indirizzo_Casa);
        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);

        partecipa_casa = view.findViewById(R.id.btn_aggiungi_coinquilino);
        crea_casa = view.findViewById(R.id.btn_crea_casa);

        String raccoltaCase ="case";
        String raccoltaUtenti = "utenti";


        imageView.setImageResource(modelCheckCasas.get(position).getImage());
        title.setText(modelCheckCasas.get(position).getTitle());
        desc.setText(modelCheckCasas.get(position).getDesc());
        fieldCasaId.setHint(modelCheckCasas.get(position).getHintIndirizzoCasa());

        //Log.d("Valore di position:", ""+position);

        if(position==1)
        {
            crea_casa.setVisibility(View.VISIBLE);
            partecipa_casa.setVisibility(View.INVISIBLE);
            fieldCasaId.setVisibility(View.INVISIBLE);
        }

        partecipa_casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String casaId = fieldCasaId.getText().toString();
                /*
                quando un nuovo utente si aggiunge a una casa già esistente bisogna aggiungere
                l'utente alla raccolta utenti della casa, incrementare il numero di inquilini
                e aggiungere l'id della casa all'utente
                */

                Map<String, Object> map = new HashMap<>();
                map.put("nome_cognome",context.nomeUser + " " + context.cognomeUser);
                map.put("user_id",context.userId);
                //inserisci il nuovo utente nella raccolta utenti della casa e aggiorna il numero di partecipanti
                Task<Void> aggiungiUtenteCasa = context.fStore.collection(raccoltaCase).document(casaId)
                        .update(
                                "utenti", FieldValue.arrayUnion(map),
                                "numero_utenti" , FieldValue.increment(1)
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //aggiungere all'utente la casa appena creata
                                Task<Void> aggiungiCasaUtente = context.fStore.collection(raccoltaUtenti).document(context.userId)
                                        .update(
                                                "casa",casaId
                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                creaCasaRealtime(casaId, context.userId, context.nomeUser + " " + context.cognomeUser);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                            }
                        });
            }
        });

        //aggiungere una nuova casa alla raccolta case e indicare un riferimento della casa all'interno dell'utente
        crea_casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();

                String nome_cognome = context.nomeUser + " " + context.cognomeUser;
                Log.d("nome dentro l'adapter :",nome_cognome);

                map.put("nome_cognome",nome_cognome);
                map.put("user_id",context.userId);

                Map<String, Object> data = new HashMap<>();

                //crea una sottoraccolta composta da un solo utente
                data.put("utenti", Arrays.asList(map));
                data.put("numero_utenti", 1);
                data.put("lista_mansioni", Arrays.asList("Lavare bagno","Lavare cucina","Lavare soggiorno","Lavare corridoio"));

                //aggiungi l'utente alla casa e crea il documento casa
                context.fStore.collection(raccoltaCase).add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                //ottieni casaId dal riferimento al documento appena creato
                                String casaId = documentReference.getId();

                                context.fStore.collection(raccoltaUtenti).document(context.userId)
                                        .update(
                                                "casa",casaId
                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                                        //creo l'istanza della casa con all'interno l'utente nel db realtime
                                        creaCasaRealtime(casaId,context.userId, nome_cognome);


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                            }
                        });
            }
        });


        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    public void creaCasaRealtime(String casaId, String userID, String nome_cognome) {
        HashMap<String, String> map = new HashMap<>();

        map.put("nome_cognome",nome_cognome);
        map.put("user_id",context.userId);
        map.put("image", "default");


        context.dbRef = FirebaseDatabase.getInstance().getReference(casaId).child("utenti").child(userID);

        context.dbRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Intent intent = new Intent(context.getApplicationContext(),MainActivity.class);
                    intent.putExtra(ARG_USER_ID,context.userId);
                    intent.putExtra(ARG_CASA_ID,casaId);
                    intent.putExtra(ARG_NOME_USER,context.nomeUser);
                    intent.putExtra(ARG_COGNOME_USER,context.cognomeUser);
                    //passa alla MainActivity e chiudi CheckCasaActivity
                    context.startActivity(intent);
                    context.finish();
                }
                else{
                    Toast.makeText(context.getApplicationContext(),"Errore di connessione",Toast.LENGTH_LONG);
                }
            }
        });
    }
}
