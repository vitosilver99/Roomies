package com.example.roomies.pagamenti;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
public class InteressatiAdapter extends RecyclerView.Adapter<InteressatiAdapter.InteressatoViewHolder> {

    Context context;
    ArrayList<ModelloInteressato> listaInteressati;


    public InteressatiAdapter(Context context, Map<String,Object> documentoPagamento){
        this.context=context;
        this.listaInteressati=convertiDaFirestore(documentoPagamento);
    }

    @NonNull
    @Override
    public InteressatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.modello_riga_interessato_pagamento,parent,false);
        return new InteressatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InteressatoViewHolder holder, int position) {
        holder.nomeCognome.setText(listaInteressati.get(position).getNome_cognome());
        if(listaInteressati.get(position).isPagato()) {
            holder.nomeCognome.setTextColor(Color.GREEN);
        }
        else {
            holder.nomeCognome.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return listaInteressati.size();
    }

    public class InteressatoViewHolder extends RecyclerView.ViewHolder{

        TextView nomeCognome;

        public InteressatoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCognome = itemView.findViewById(R.id.interessato_nome_cognome);
        }
    }



    public ArrayList<ModelloInteressato> convertiDaFirestore(Map<String,Object> mappaPagamento) {

        //serve a ricavare la lista degli interessati dal pagamento partendo da una mappa del documento pagamento

        //creo la lista di interessati che verrà riempita
        ArrayList<ModelloInteressato> listaInteressatiReturn = new ArrayList<ModelloInteressato>();
        Set<Map.Entry<String,Object>> insiemeEntryPagamento = mappaPagamento.entrySet();
        for (Map.Entry<String, Object> entryPagamento : insiemeEntryPagamento) {
            if (entryPagamento.getKey().equals("interessati")) {
                ArrayList<Map<String,Object>> listaInteressati = (ArrayList<Map<String,Object>>) entryPagamento.getValue();
                for(Map<String,Object> interessato : listaInteressati) {
                    listaInteressatiReturn.add(new ModelloInteressato((String)interessato.get("nome_cognome"), (String)interessato.get("id_utente"), (Boolean)interessato.get("pagato")));
                }
            }
        }
        return listaInteressatiReturn;
    }


    //getter per la lista degli interessati
    public ArrayList<ModelloInteressato> getListaInteressati() {
        return listaInteressati;
    }

    public void setPagato(String userId) {



        ListIterator<ModelloInteressato> iterator = listaInteressati.listIterator();
        while (iterator.hasNext()) {
            ModelloInteressato next = iterator.next();
            if (next.getId_utente().equals(userId)) {
                next.setPagato(true);
                iterator.set(next);
            }
        }

        //ricrea il layout con i dati aggiornati (sto usando impropriamente questo metodo)
        notifyDataSetChanged();


    }
}