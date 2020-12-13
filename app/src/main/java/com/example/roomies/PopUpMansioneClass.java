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
import android.widget.TableLayout;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class PopUpMansioneClass implements DatePickerDialog.OnDateSetListener {

    ArrayList<UtentiClass> utentiClasses;
    ArrayList<MansioniClass> mansioniClasses;
    Button seleziona_giorno;

    public PopUpMansioneClass(ArrayList<UtentiClass> utentiClasses,ArrayList<MansioniClass> mansioniClasses)
    {
        this.mansioniClasses = mansioniClasses;
        this.utentiClasses = utentiClasses;
    }

    public void showPopupWindow(final View view) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_crea_mansione, null);

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
        Spinner spinner = popupView.findViewById(R.id.spinner_mansioni);

        Log.d("sono nel popup",mansioniClasses.size()+"");
        List<String> mansioni = new ArrayList<String>();
        for(int i = 0; i< mansioniClasses.size();i++)
        {
            mansioni.add(mansioniClasses.get(i).getNome());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, mansioni);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        Spinner spinner_utenti = popupView.findViewById(R.id.spinner_utenti);

        List<String> utenti = new ArrayList<String>();
        for(int i = 0; i< utentiClasses.size();i++)
        {
            utenti.add(utentiClasses.get(i).getNome_cognome());
        }
        ArrayAdapter<String> dataAdapter_utenti = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, utenti);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_utenti.setAdapter(dataAdapter_utenti);

        seleziona_giorno = popupView.findViewById(R.id.seleziona_data);
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
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




        //Handler for clicking on the inactive zone of the window
        /*
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });*/
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
}
