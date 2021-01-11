package com.example.roomies.home;

public class ModelloArticoloHome {
    private String articolo_id;

    //i nomi scelti per gli attributi devono essere esattamente gli stessi di quelli usati nel documento di Firestore
    private String nome_articolo;


    public ModelloArticoloHome() {
    }

    public ModelloArticoloHome(String nome_articolo, String articolo_id) {
        this.nome_articolo = nome_articolo;

        this.articolo_id = articolo_id;
    }

    public String getNome_articolo() {
        return nome_articolo;
    }

    public void setNome_articolo(String nome_articolo) {
        this.nome_articolo = nome_articolo;
    }



    public String getArticolo_id() {
        return articolo_id;
    }

    public void setArticolo_id(String articolo_id) {
        this.articolo_id = articolo_id;
    }
}
