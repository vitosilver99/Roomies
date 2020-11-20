package com.example.roomies;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ModelCheckCasa {

    private int image;
    private String title;
    private String desc;
    private  String hint_indirizzo_casa;

    public ModelCheckCasa(int image, String title, String desc, String hint_indirizzo_casa) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.hint_indirizzo_casa = hint_indirizzo_casa;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHintIndirizzoCasa() {
        return hint_indirizzo_casa;
    }

    public void setHintIndirizzoCasa(String indirizzo_casa) {
        this.hint_indirizzo_casa = indirizzo_casa;
    }
/*
    public Button getPartecipa_casa() {
        return partecipa_casa;
    }

    public void setPartecipa_casa(Button partecipa_casa) {
        this.partecipa_casa = partecipa_casa;
    }

    public Button getAggiungi_casa() {
        return aggiungi_casa;
    }

    public void setAggiungi_casa(Button aggiungi_casa) {
        this.aggiungi_casa = aggiungi_casa;
    }*/
}
