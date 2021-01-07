package com.example.roomies.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.calendario.UtentiClass;

import java.util.List;

public class UsersAdapterChat extends RecyclerView.Adapter<UsersAdapterChat.ViewHolder> {

    private Context context;
    private List<UtentiClass> utenti;
    private String casaId;


    public UsersAdapterChat(Context context, List<UtentiClass> utenti,String casaId) {
        this.context = context;
        this.utenti = utenti;
        this.casaId = casaId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);

        return new UsersAdapterChat.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UtentiClass utentiClass = utenti.get(position);
        holder.nome.setText(utentiClass.getNome_cognome());

        holder.itemView.setOnClickListener((v) -> {
            Intent i = new Intent(context, MessageActivity.class);
            i.putExtra("userId",utentiClass.getUserId());
            i.putExtra("casaId",casaId);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return utenti.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nome;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.userNameChat);

        }
    }

}
