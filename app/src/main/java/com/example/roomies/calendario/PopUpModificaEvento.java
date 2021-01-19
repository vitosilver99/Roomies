package com.example.roomies.calendario;

import android.app.DatePickerDialog;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopUpModificaEvento implements DatePickerDialog.OnDateSetListener {

    ArrayList<UtentiClass> utentiClasses;
    List<UtentiClass> utentiSelezionati;

    FirebaseFirestore fStore;

    RecyclerViewAdapterModificaEvento myAdapter;
    RecyclerView myrv;

    EventiClass evento_da_modificare;
    Button seleziona_giorno;
    View popupView;
    String UdCasa;
    CalendarFragment calendarFragment;
    View view;


    public PopUpModificaEvento(ArrayList<UtentiClass> utentiClasses, String UdCasa, EventiClass evento_da_modificare, CalendarFragment calendarFragment, View view)
    {
        this.utentiClasses = utentiClasses;
        this.UdCasa = UdCasa;
        this.evento_da_modificare = evento_da_modificare;
        this.calendarFragment = calendarFragment;
        this.view = view;
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
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true) ;
        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        //elementi presenti nel popup
        TextView modifica_nome = popupView.findViewById(R.id.textview_nome_evento_popup);
        Button btn_aggiungi = popupView.findViewById(R.id.button_aggiungi_evento);
        EditText descrizione = popupView.findViewById(R.id.descrizione_evento_popup);
        EditText nome = popupView.findViewById(R.id.nome_evento_popup);
        NDSpinner spinner_utenti = popupView.findViewById(R.id.spinner_utenti_evento);
        seleziona_giorno = popupView.findViewById(R.id.seleziona_data_evento);
        myrv = popupView.findViewById(R.id.RecyclerView_spinner);


        modifica_nome.setText("Modifica evento");
        btn_aggiungi.setText("Modifica");

        nome.setText(evento_da_modificare.getNome());
        descrizione.setText(evento_da_modificare.getDescrizione());

        //Date date = format.parse(evento_da_modificare.getData().toString());

        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyy");
        String data_evento = format.format(evento_da_modificare.getData());
        seleziona_giorno.setText(data_evento);



        utentiSelezionati = new ArrayList<>();
        utentiSelezionati = evento_da_modificare.getPartecipanti();
        //Log.d("data",""+date.toString());
        aggiornaAdapter();

        //aggiungo gli utenti allo spinner
        List<String> utenti = new ArrayList<String>();
        for(int i = 0; i< utentiClasses.size();i++)
        {
            utenti.add(utentiClasses.get(i).getNome_cognome());
        }
        ArrayAdapter<String> dataAdapter_utenti = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, utenti);
        dataAdapter_utenti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_utenti.setAdapter(dataAdapter_utenti);

        //fa uscire il calendario per scegliere la data
        seleziona_giorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDailog(popupView);
                Log.d("sono nel calendario","sono dentro");
            }
        });


        //gestione aggiunta utente all'evento
        spinner_utenti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("numero spinner",""+position);
                UtentiClass utente_cliccato = utentiClasses.get(position);

                boolean presente = false;
                for(UtentiClass temp : utentiSelezionati )
                {
                    if(temp.getUserId().equals(utente_cliccato.getUserId()))
                    {
                       presente = true;
                    }
                }

                if(!presente){
                    utentiSelezionati.add(utente_cliccato);
                    aggiornaAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


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


                fStore.collection("case").document(UdCasa).collection("eventi").document(evento_da_modificare.getIdEvento())
                        .update(singolo_evento).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        calendarFragment.aggiornaRecyclerView(view);
                        popupWindow.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
    public void eliminaElementoSelezionato(int position)
    {
        utentiSelezionati.remove(position);
        Log.d("numero dopo elimina",""+utentiClasses.size());
        aggiornaAdapter();
    }

    public void aggiornaAdapter()
    {
        myAdapter = new RecyclerViewAdapterModificaEvento(popupView.getContext(),utentiSelezionati,PopUpModificaEvento.this);
        myrv.setLayoutManager(new GridLayoutManager(popupView.getContext(),2));
        myrv.setAdapter(myAdapter);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        NumberFormat f = new DecimalFormat("00");
        String date = dayOfMonth + "/" + f.format(month) + "/" + year;
        seleziona_giorno.setText(date);
    }
}
