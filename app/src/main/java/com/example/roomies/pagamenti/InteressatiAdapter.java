package com.example.roomies.pagamenti;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.MansioniClass;
import com.example.roomies.R;
import com.example.roomies.RecyclerViewAdapter;

import java.util.ArrayList;
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
        holder.pagato.setText(listaInteressati.get(position).isPagato()+"");
    }

    @Override
    public int getItemCount() {
        return listaInteressati.size();
    }

    public class InteressatoViewHolder extends RecyclerView.ViewHolder{

        TextView nomeCognome, pagato;

        public InteressatoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCognome = itemView.findViewById(R.id.interessato_nome_cognome);
            pagato = itemView.findViewById(R.id.interessato_pagato);
        }
    }



    public ArrayList<ModelloInteressato> convertiDaFirestore(Map<String,Object> mappaPagamento) {

        //serve a ricavare la lista degli interessati dal pagamento partendo da una mappa del documento pagamento

        //creo la lista di interessati che verrà riempita
        ArrayList<ModelloInteressato> listaInteressatiReturn = new ArrayList<ModelloInteressato>();

        Set<Map.Entry<String,Object>> insiemeEntryPagamento = mappaPagamento.entrySet();
        for (Map.Entry<String, Object> entryPagamento : insiemeEntryPagamento) {



            if (entryPagamento.getKey().equals("interessati")) {

                /*
                cast in (Map<String,Object>) necessario perchè l'entryPagamento in corrispondenza della
                chiave "interessati" non sarà un semplice oggetto (ad esempio String, Integer, ecc)
                ma a sua volta una mappa costituita da tre chiavi (nome_cognome, id_utente, pagato)
                */
                //NON FUNZIONA
                Map<String,Object> mappaInteressati = (Map<String,Object>) entryPagamento.getValue();

                /*
                    visto che mappaInteressati ha come chiavi 0, 1, 2, 3 (perchè la lista interessati è un array)
                    possiamo evitare di richiamare entrySet() in questo modo
                    Set<Map.Entry<String,Object>> set2 = mappaInteressati.entrySet();
                    per ottenere i singoli elementi dell'array
                    ma inserire direttamente mappaInteressati in un arraylist
                 */

                //posso inserire il set ottenuto da mappaInteressati direttamente in un arraylist
                // perchè la lista interessati è già stata definita come array nel documento di firestore
                ArrayList<Map<String,Object>> listaInteressati = (ArrayList<Map<String, Object>>) mappaInteressati;

                for(Map<String,Object> interessato : listaInteressati) {
                    listaInteressatiReturn.add(new ModelloInteressato((String)interessato.get("nome_cognome"), (String)interessato.get("id_utente"), (Boolean)interessato.get("pagato")));
                }
            }


        }
        return listaInteressatiReturn;
    }
}