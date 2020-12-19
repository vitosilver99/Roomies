package com.example.roomies;

import android.app.DatePickerDialog;
import android.graphics.drawable.BitmapDrawable;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopUpEventoClass implements DatePickerDialog.OnDateSetListener{

    ArrayList<UtentiClass> utentiClasses;
    ArrayList<MansioniClass> mansioniClasses;
    Button seleziona_giorno;

    FirebaseFirestore fStore;

    List<UtentiClass> utentiSelezionati;
    RecyclerViewAdapterEvento myAdapter;
    RecyclerView myrv;

    View popupView;

    String UdCasa;

    public PopUpEventoClass(ArrayList<UtentiClass> utentiClasses, String UdCasa)
    {
        this.utentiClasses = utentiClasses;
        this.UdCasa = UdCasa;
    }

    public void showPopupWindow(final View view) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_crea_evento, null);

        fStore = FirebaseFirestore.getInstance();

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true) ;

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler


        Spinner spinner_utenti = popupView.findViewById(R.id.spinner_utenti_evento);

        List<String> utenti = new ArrayList<String>();
        for(int i = 0; i< utentiClasses.size();i++)
        {
            utenti.add(utentiClasses.get(i).getNome_cognome());
        }
        ArrayAdapter<String> dataAdapter_utenti = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, utenti);
        dataAdapter_utenti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_utenti.setAdapter(dataAdapter_utenti);

        seleziona_giorno = popupView.findViewById(R.id.seleziona_data_evento);
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
                if(!utentiSelezionati.contains(utentiClasses.get(position)))
                {
                    utentiSelezionati.add(utentiClasses.get(position));
                    aggiornaAdapter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        Button btn_aggiungi = popupView.findViewById(R.id.button_aggiungi_evento);
        EditText descrizione = popupView.findViewById(R.id.descrizione_evento_popup);
        EditText nome = popupView.findViewById(R.id.nome_evento_popup);

        btn_aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> singolo_evento = new HashMap<>();
                Date data = new Date();
                try {
                    data=new SimpleDateFormat("dd/MM/yyyy").parse(seleziona_giorno.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                singolo_evento.put("giorno",data);
                singolo_evento.put("nome",nome.getText().toString());
                singolo_evento.put("descrizione",descrizione.getText().toString());
                singolo_evento.put("coinquilini", utentiSelezionati);


                //aggiungo l'evento appena creato alla collezione Eventi
                Date finalData = data;
                fStore.collection("eventi").add(singolo_evento).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("evento_id",documentReference.getId());
                        map.put("data" , finalData);

                        fStore.collection("case").document(UdCasa).update(
                                "eventi", FieldValue.arrayUnion(map)
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                popupWindow.dismiss();
                            }
                        });
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
        Log.d("numero dopo elimina",""+utentiClasses.size());
        aggiornaAdapter();
    }

    public void aggiornaAdapter()
    {
        myAdapter = new RecyclerViewAdapterEvento(popupView.getContext(),utentiSelezionati,PopUpEventoClass.this);
        myrv.setLayoutManager(new GridLayoutManager(popupView.getContext(),2));
        myrv.setAdapter(myAdapter);
    }

}
