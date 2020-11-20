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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AdapterCheckCasa extends PagerAdapter {

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
        View view = layoutInflater.inflate(R.layout.item, container, false);

        ImageView imageView;
        TextView title, desc;
        TextView indirizzo_casa;
        Button partecipa_casa;
        Button aggiungi_casa;
        indirizzo_casa = view.findViewById(R.id.textView_indirizzo_Casa);
        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);

        partecipa_casa = view.findViewById(R.id.btn_aggiungi_coinquilino);
        aggiungi_casa = view.findViewById(R.id.btn_crea_casa);



        imageView.setImageResource(modelCheckCasas.get(position).getImage());
        title.setText(modelCheckCasas.get(position).getTitle());
        desc.setText(modelCheckCasas.get(position).getDesc());
        indirizzo_casa.setHint(modelCheckCasas.get(position).getHintIndirizzoCasa());

        //Log.d("Valore di position:", ""+position);

        if(position==1)
        {
            aggiungi_casa.setVisibility(View.VISIBLE);
            partecipa_casa.setVisibility(View.INVISIBLE);
            indirizzo_casa.setVisibility(View.INVISIBLE);
        }

        partecipa_casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Map<String, Object> dataUtenti = new HashMap<>();
                dataUtenti.put("u",context.userID);*/

                String indirizzo = indirizzo_casa.getText().toString();

                Log.d("indirizzo casa:",indirizzo);
                Task<Void> aggiungiUtenteCasa = context.fStore.collection("casa").document(indirizzo)
                        .update(
                                "utenti.utente3", context.userID
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                                intent.putExtra("UserId",context.userID);
                                context.startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                            }
                        });
            }
        });

        aggiungi_casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckCasaActivity checkCasaActivity = context;
                Map<String, Object> dataUtenti = new HashMap<>();
                dataUtenti.put("utente1",checkCasaActivity.userID);

                Map<String, Object> data = new HashMap<>();
                data.put("utenti", dataUtenti);
                data.put("numero_utenti", 1);

                checkCasaActivity.fStore.collection("casa").add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Intent intent = new Intent(context.getApplicationContext(),MainActivity.class);
                                intent.putExtra("UserId",context.userID);
                                context.startActivity(intent);
                                context.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });




        /*
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("param", models.get(position).getTitle());
                context.startActivity(intent);
                // finish();
            }
        });*/

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
