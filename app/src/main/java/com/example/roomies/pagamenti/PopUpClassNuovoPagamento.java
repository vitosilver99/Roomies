package com.example.roomies.pagamenti;

//codice adattato da PopUpEventoClass



import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.calendario.NDSpinner;
import com.example.roomies.calendario.RecyclerViewAdapterEvento;
import com.example.roomies.calendario.UtentiClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopUpClassNuovoPagamento implements DatePickerDialog.OnDateSetListener{

    ArrayList<UtentiClass> listaUtenti;

    Button seleziona_giorno;

    FirebaseFirestore fStore;

    List<UtentiClass> utentiSelezionati;
    RecyclerViewAdapterNuovoPagamento myAdapter;
    RecyclerView myrv;
    View popupView;

    String casaId;

    Context context;





    public PopUpClassNuovoPagamento(ArrayList<UtentiClass> listaUtenti, String casaId, Context context)
    {
        this.listaUtenti = listaUtenti;
        this.casaId = casaId;
        this.context = context;
    }

    public void showPopupWindow(final View view) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_crea_pagamento, null);

        fStore = FirebaseFirestore.getInstance();

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true) ;

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);




        //Initialize the elements of our window, install the handler

        //ATTENZIONE HO PROVATO A CAMBIARE R.id.spinner_utenti_pagamento con R.id.spinner_utenti per vedere cosa succedeva
        //il modo corretto è con R.id.spinner_utenti_pagamento
        //non funziona con spinner presenti in altri layout
        NDSpinner spinner_utenti = popupView.findViewById(R.id.spinner_utenti_pagamento);

        List<String> listaNomiCognomi = new ArrayList<String>();
        for(int i = 0; i< listaUtenti.size(); i++)
        {
            listaNomiCognomi.add(listaUtenti.get(i).getNome_cognome());
        }
        ArrayAdapter<String> dataAdapter_utenti = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, listaNomiCognomi);
        dataAdapter_utenti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_utenti.setAdapter(dataAdapter_utenti);

        seleziona_giorno = popupView.findViewById(R.id.seleziona_scadenza_pagamento);
        seleziona_giorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDailog(popupView);
                Log.d("sono nel calendario","sono dentro");
            }
        });

        //gestione aggiunta utente all'evento


        myrv = (RecyclerView) popupView.findViewById(R.id.RecyclerView_spinner);
        utentiSelezionati= new ArrayList<>();

        spinner_utenti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("numero spinner",""+position);
                if(!utentiSelezionati.contains(listaUtenti.get(position)))
                {
                    utentiSelezionati.add(listaUtenti.get(position));
                    aggiornaAdapter();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }



        });


        Button btn_aggiungi = popupView.findViewById(R.id.button_aggiungi_pagamento);
        EditText importo_totale = popupView.findViewById(R.id.importo_totale_pagamento_popup);
        EditText nome = popupView.findViewById(R.id.nome_pagamento_popup);

        btn_aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> mappaPagamento = new HashMap<>();
                Date data = new Date();
                Log.d("scadenza pagamento",data.toString());
                try {
                    data=new SimpleDateFormat("dd/MM/yyyy").parse(seleziona_giorno.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(TextUtils.isEmpty(nome.getText().toString())) {
                    nome.setError("Inserisci il nome del pagamento");
                    return;
                }
                if(TextUtils.isEmpty(importo_totale.getText().toString())) {
                    importo_totale.setError("Inserisci l'importo totale del pagamento");
                    return;
                }
                if(utentiSelezionati.size()==0) {
                    Toast.makeText(context,"Inserisci almeno un interessato dal pagamento",Toast.LENGTH_LONG).show();
                    return;
                }
                //nessun controllo sulla data (se non viene inserita dall'utente viene inserita automaticamente la data odierna)




                mappaPagamento.put("nome_pagamento",nome.getText().toString());
                mappaPagamento.put("scadenza_pagamento",data);
                mappaPagamento.put("non_pagato", utentiSelezionati.size());



                //converti UtentiClass in ModelloInteressato (modello interessato ha un attributo boolean che utenti class non ha)
                ArrayList<ModelloInteressato> listaInteressati= new  ArrayList<ModelloInteressato>() {
                };
                for (UtentiClass utenteEntry : utentiSelezionati){
                    //ho creato un costruttore apposito all'interno di modellointeressato per la conversione
                    listaInteressati.add(new ModelloInteressato(utenteEntry));
                }
                mappaPagamento.put("interessati", listaInteressati);




                mappaPagamento.put("importo_totale",Float.parseFloat( importo_totale.getText().toString()));
                mappaPagamento.put("importo_singolo",Float.parseFloat( importo_totale.getText().toString())/utentiSelezionati.size());
                //modifica mappa


                fStore.collection("case").document(casaId).collection("pagamenti").add(mappaPagamento).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        popupWindow.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(popupView.getContext(),"Errore: "+e.getMessage(),Toast.LENGTH_LONG);
                    }
                });
            }
        });


    }

    private void showDatePickerDailog(View PopupView){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PopupView.getContext(),
                R.style.DialogTheme,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + month + "/" + year;
        seleziona_giorno.setText(date);
    }

    public void eliminaElementoSelezionato(int position)
    {
        utentiSelezionati.remove(position);
        Log.d("numero dopo elimina",""+ listaUtenti.size());
        aggiornaAdapter();
    }

    public void aggiornaAdapter()
    {
        
        myAdapter = new RecyclerViewAdapterNuovoPagamento(popupView.getContext(),utentiSelezionati, this);
        myrv.setLayoutManager(new GridLayoutManager(popupView.getContext(),2));
        myrv.setAdapter(myAdapter);
    }

}
