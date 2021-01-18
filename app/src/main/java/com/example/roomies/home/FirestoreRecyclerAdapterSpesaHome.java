package com.example.roomies.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.spesa.FirestoreRecyclerAdapterSpesa;
import com.example.roomies.spesa.ModelloArticolo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreRecyclerAdapterSpesaHome extends FirestoreRecyclerAdapter<ModelloArticoloHome, FirestoreRecyclerAdapterSpesaHome.ArticoloHomeViewHolder> {

    private OnArticoloInteraction onArticoloInteraction;


    public FirestoreRecyclerAdapterSpesaHome(@NonNull FirestoreRecyclerOptions<ModelloArticoloHome> options, OnArticoloInteraction onArticoloInteraction) {
        super(options);
        this.onArticoloInteraction=onArticoloInteraction;
    }


    @Override
    protected void onBindViewHolder(@NonNull ArticoloHomeViewHolder holder, int position, @NonNull ModelloArticoloHome model) {

        holder.nome_articolo.setText(model.getNome_articolo());

    }


    @NonNull
    @Override
    public ArticoloHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modello_riga_articolo_home,parent,false);
        return new ArticoloHomeViewHolder(view);
    }

    public class ArticoloHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView nome_articolo;

        public ArticoloHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_articolo= itemView.findViewById(R.id.nome_articolo_home);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //a differenza del fragment pagamenti getItem (essendo un metodo di una classe diversa) restituisce non più un documentsnapshot ma un modelloArticolo quindi l'interfaccia che ho utilizzato in pagamenti è stata leggermente moodificata'
            onArticoloInteraction.onArticoloClick(getItem(getAdapterPosition()),getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            onArticoloInteraction.onArticoloLongClick(getItem(getAdapterPosition()),getAdapterPosition());

            //todo se non funziona il long click prova a cambiare in return true
            return false;


        }
    }





    public interface OnArticoloInteraction {

        //a differenza del paging adapter usato per il fragment pagamenti  i due metodi devono avere per necessità  (il perchè l'ho spiegato in un commento sopra) come
        //parametro un ModelloArticolo non più un DocumentSnapshot quindi se si vuole risalire al
        //documento di partenza bisogna aggiungere al modello dell'articolo anche l'id dell'articolo (cioè del documento di firestore)
        void onArticoloClick(ModelloArticoloHome articolo, int position);
        void onArticoloLongClick(ModelloArticoloHome articolo, int position);
    }

    @Override
    public void onDataChanged() {
        //quando cambiano i dati di firestore avvisa gli observer
        notifyDataSetChanged();
    }
}
