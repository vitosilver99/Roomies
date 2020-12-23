package com.example.roomies.pagamenti;

import android.util.Log;

import com.example.roomies.calendario.UtentiClass;

import java.util.HashMap;

public class ModelloInteressato {
    private String nome_cognome;
    private String id_utente;
    private boolean pagato;

    public ModelloInteressato() {
    }

    public ModelloInteressato(String nome_cognome, String id_utente, boolean pagato) {
        this.nome_cognome = nome_cognome;
        this.id_utente = id_utente;
        this.pagato = pagato;
    }
    public ModelloInteressato(UtentiClass utente) {
        this.nome_cognome=utente.getNome_cognome();
        this.id_utente=utente.getUserId();
        this.pagato=false;
    }

    public String getNome_cognome() {
        return nome_cognome;
    }

    public void setNome_cognome(String nome_cognome) {
        this.nome_cognome = nome_cognome;
    }

    public String getId_utente() {
        return id_utente;
    }

    public void setId_utente(String id_utente) {
        this.id_utente = id_utente;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
    }

    public HashMap convertiInHashMap() {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("nome_cognome",nome_cognome);
        hashMap.put("id_utente",id_utente);
        hashMap.put("pagato",pagato);
        Log.d("convertiInHashMap",hashMap.toString());
        return hashMap;
    }

}
