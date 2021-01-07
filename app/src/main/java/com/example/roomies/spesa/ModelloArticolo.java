package com.example.roomies.spesa;

public class ModelloArticolo {
    private String articolo_id;

    //i nomi scelti per gli attributi devono essere esattamente gli stessi di quelli usati nel documento di Firestore
    private String nome_articolo;
    private Boolean da_comprare;

    public ModelloArticolo() {
    }

    public ModelloArticolo(String nome_articolo, Boolean da_comprare, String articolo_id) {
        this.nome_articolo = nome_articolo;
        this.da_comprare = da_comprare;
        this.articolo_id = articolo_id;
    }

    public String getNome_articolo() {
        return nome_articolo;
    }

    public void setNome_articolo(String nome_articolo) {
        this.nome_articolo = nome_articolo;
    }


    public Boolean getDa_comprare() {
        return da_comprare;
    }

    public void setDa_comprare(Boolean da_comprare) {
        this.da_comprare = da_comprare;
    }

    public String getArticolo_id() {
        return articolo_id;
    }

    public void setArticolo_id(String articolo_id) {
        this.articolo_id = articolo_id;
    }
}
