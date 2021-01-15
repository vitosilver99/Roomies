package com.example.roomies.calendario;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.roomies.R;

import java.util.ArrayList;

public class PopUpClass{

    ArrayList<UtentiClass> utentiClasses;
    ArrayList<MansioniClass> mansioniClasses;
    String UdCasa;

    //PopupWindow display method
    public  PopUpClass(ArrayList<UtentiClass> utentiClasses,ArrayList<MansioniClass> mansioniClasses, String UdCasa)
    {
        this.mansioniClasses = mansioniClasses;
        this.utentiClasses = utentiClasses;
        this.UdCasa =UdCasa;
    }

    public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        Button buttonMansione = popupView.findViewById(R.id.button_mansione_popup);
        Button buttonEvento = popupView.findViewById(R.id.button_evento_popup);
        Button buttonClose = popupView.findViewById(R.id.button_chiudi_popup);


        //Log.d("POPUP: ",""+documentoCasa.get("numero_utenti"));

        buttonMansione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                PopUpMansioneClass popUpClass = new PopUpMansioneClass(utentiClasses,mansioniClasses,UdCasa);
                popUpClass.showPopupWindow(view);
            }
        });

        buttonEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                PopUpEventoClass popUpClass = new PopUpEventoClass(utentiClasses,UdCasa);
                popUpClass.showPopupWindow(view);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });




    }

}