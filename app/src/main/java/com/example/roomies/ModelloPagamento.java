package com.example.roomies;

import java.sql.Date;
import java.sql.Timestamp;

public class ModelloPagamento {


    private String pagamento_id;
    //i nomi scelti per gli attributi devono essere esattamente gli stessi di quelli usati nel documento di Firestore

    private String nome_pagamento;
    private int non_pagato;

    //quale classe utilizzare per la data di scadenza
    private java.util.Date scadenza_pagamento;

    public ModelloPagamento() {
    }

    public ModelloPagamento(String nome, int non_pagato, java.util.Date scadenza, String pagamento_id) {
        this.nome_pagamento = nome;
        this.non_pagato = non_pagato;
        this.scadenza_pagamento = scadenza;
        this.pagamento_id=pagamento_id;
    }

    public String getNome_pagamento() {
        return nome_pagamento;
    }

    public void setNome_pagamento(String nome_pagamento) {
        this.nome_pagamento = nome_pagamento;
    }

    public int getNon_pagato() {
        return non_pagato;
    }

    public void setNon_pagato(int non_pagato) {
        this.non_pagato = non_pagato;
    }

    public java.util.Date getScadenza_pagamento() {
        return scadenza_pagamento;
    }

    public void setScadenza_pagamento(java.util.Date scadenza_pagamento) {
        this.scadenza_pagamento = scadenza_pagamento;
    }

    public String getPagamento_id() {
        return pagamento_id;
    }

    public void setPagamento_id(String pagamento_id) {
        this.pagamento_id = pagamento_id;
    }
}
