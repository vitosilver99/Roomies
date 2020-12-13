package com.example.roomies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext ;
    private List<UtentiClass> mData ;


    public RecyclerViewAdapter(Context mContext, List<UtentiClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.add_utent_spinner,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.nome_selezionato.setText(mData.get(position).getNome_cognome());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("card view","sono nella card view dello spinner");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome_selezionato;
        Button elimina;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome_selezionato = (TextView) itemView.findViewById(R.id.nome_add_spinner) ;
            elimina = (Button) itemView.findViewById(R.id.btn_elimina_add_spinner);
            cardView = (CardView) itemView.findViewById(R.id.CardViewUtenteSpinner);

        }
    }


}