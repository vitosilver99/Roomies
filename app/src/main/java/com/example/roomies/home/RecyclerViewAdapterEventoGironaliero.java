package com.example.roomies.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.calendario.EventiClass;
import com.example.roomies.calendario.RecyclerViewAdapterMansione;
import com.example.roomies.calendario.UtentiClass;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class RecyclerViewAdapterEventoGironaliero extends RecyclerView.Adapter<RecyclerViewAdapterEventoGironaliero.MyViewHolder> {

    private Context mContext;
    List<EventiClass> eventi_giornalieri;

    public RecyclerViewAdapterEventoGironaliero(Context mContext, List<EventiClass> eventi_giornalieri){
        this.mContext = mContext;
        this.eventi_giornalieri = eventi_giornalieri;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterEventoGironaliero.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.eventi_giornalieri,parent,false);
        return new RecyclerViewAdapterEventoGironaliero.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterEventoGironaliero.MyViewHolder holder, int position) {
        holder.nome_evento.setText(eventi_giornalieri.get(position).getNome());

        int num_part = eventi_giornalieri.get(position).getPartecipanti().size();
        UtentiClass utenti = new UtentiClass();
        String partecipanti="";
        for(int i = 0; i<num_part; i++) {
            utenti = eventi_giornalieri.get(position).getPartecipanti().get(i);
            partecipanti = partecipanti + utenti.getNome_cognome() + ",";
        }

        holder.partecipanti_evento.setText(partecipanti);
    }

    @Override
    public int getItemCount() {
        return eventi_giornalieri.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nome_evento;
        public TextView partecipanti_evento;

        public MyViewHolder(View itemView) {
            super(itemView);
            nome_evento = (TextView) itemView.findViewById(R.id.nome_evento_giornaliero);
            partecipanti_evento = (TextView) itemView.findViewById(R.id.coinuquilini_evento_giornaliero);
        }
    }
}
