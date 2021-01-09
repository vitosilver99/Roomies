package com.example.roomies.calendario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventiClass {
    private String idEvento;
    private Date data;
    private String nome;
    private List<UtentiClass> partecipanti;

    public EventiClass() {
    }

    public EventiClass(String nome,List<UtentiClass> partecipanti){
        this.nome = nome;
        this.partecipanti = partecipanti;
    }

    public EventiClass(String idEvento, String data) {
        this.idEvento = idEvento;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
        try {
            this.data = format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<UtentiClass> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(List<UtentiClass> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public EventiClass(String idEvento, Date data) {
        this.idEvento = idEvento;
        this.data=data;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
