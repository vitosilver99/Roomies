package com.example.roomies.chat;

import android.content.Context;
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


    public UsersAdapterChat(Context context, List<UtentiClass> utenti) {
        this.context = context;
        this.utenti = utenti;
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
