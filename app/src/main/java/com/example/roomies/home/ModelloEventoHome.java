package com.example.roomies.home;

import com.example.roomies.calendario.UtentiClass;

import java.util.ArrayList;

public class ModelloEventoHome {
    private String nome_evento;
    private String coinquilini;


    public ModelloEventoHome() {
    }

    public ModelloEventoHome(String nome_evento, String coinquilini) {
        this.nome_evento = nome_evento;
        this.coinquilini = coinquilini;
    }



    public String getNome_evento() {
        return nome_evento;
    }

    public void setNome_evento(String nome_evento) {
        this.nome_evento = nome_evento;
    }

    public String getCoinquilini() {
        return coinquilini;
    }

    public void setCoinquilini(String coinquilini) {
        this.coinquilini = coinquilini;
    }
}
