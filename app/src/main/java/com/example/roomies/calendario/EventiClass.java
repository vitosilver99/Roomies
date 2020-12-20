package com.example.roomies.calendario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventiClass {
    private String idEvento;
    private Date data;

    public EventiClass() {
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
