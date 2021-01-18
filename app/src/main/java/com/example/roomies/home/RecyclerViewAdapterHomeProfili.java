package com.example.roomies.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.calendario.UtentiClass;

import java.util.List;

public class RecyclerViewAdapterHomeProfili  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<UtentiClass> utentiClassList;
    private int ultima_posizione;

    private static int TYPE_ADD = 1;
    private static int TYPE_USER = 2;

    public RecyclerViewAdapterHomeProfili(Context mContext, List<UtentiClass> utentiClasses, int ultima_posizione){
        this.mContext = mContext;
        this.utentiClassList = utentiClasses;
        this.ultima_posizione = ultima_posizione;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        /*LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.profilo_utente_home,parent,false);
        return new MyViewHolder(view);*/

        switch (viewType) {
            case 1:
                LayoutInflater mInflater = LayoutInflater.from(mContext);
                view = mInflater.inflate(R.layout.add_profilo_utente,parent,false);
                return new MyViewHolder2(view);
            default:
                LayoutInflater mInflater2 = LayoutInflater.from(mContext);
                view = mInflater2.inflate(R.layout.profilo_utente_home,parent,false);
                return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ADD) {

            MyViewHolder2 viewHolder2 = (MyViewHolder2) holder;
            viewHolder2.aggiungi_utente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO aggiungere il popup per invitare un coinquilino
                    //Log.d("premuto il tasto","ciao");
                }
            });

        } else {

            MyViewHolder viewHolder = (MyViewHolder) holder;

            char[] nome_char = utentiClassList.get(position).getNome_cognome().toCharArray();
            String nome="";

            for(int i=0;i<nome_char.length;i++){
                if(!(nome_char[i]==' ')){
                    nome = nome + nome_char[i]+"";
                }else{
                    break;
                }
            }

            viewHolder.nome_selezionato.setText(nome);
        }


        //holder.nome_selezionato.setText(utentiClassList.get(position).getNome_cognome());
    }

    @Override
    public int getItemCount() {
        return utentiClassList.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome_selezionato;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome_selezionato = (TextView) itemView.findViewById(R.id.nome_profilo);
        }
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder {

        CardView aggiungi_utente;
        public MyViewHolder2(View itemView){
            super(itemView);
                aggiungi_utente = itemView.findViewById(R.id.btn_add_user);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //Log.d("posizione",""+position+ "ultima"+ ultima_posizione);
        if (position==ultima_posizione) {
            return TYPE_ADD;
        } else {
            return TYPE_USER;
        }
    }
}
