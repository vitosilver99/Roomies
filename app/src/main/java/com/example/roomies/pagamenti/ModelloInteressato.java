package com.example.roomies.pagamenti;

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



}
