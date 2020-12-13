package com.example.roomies;

public class UtentiClass {
    private String nome_cognome;
    private String userId;

    public UtentiClass() {
    }

    public UtentiClass(String nome_cognome, String userId) {
        this.nome_cognome = nome_cognome;
        this.userId = userId;
    }

    public String getNome_cognome() {
        return nome_cognome;
    }

    public String getUserId() {
        return userId;
    }

    public void setNome_cognome(String nome_cognome) {
        this.nome_cognome = nome_cognome;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
