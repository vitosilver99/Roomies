package com.example.roomies.spesa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirestoreRecyclerAdapterSpesa extends FirestoreRecyclerAdapter<ModelloArticolo,FirestoreRecyclerAdapterSpesa.ArticoloViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private OnArticoloInteraction onArticoloInteraction;


    public FirestoreRecyclerAdapterSpesa(@NonNull FirestoreRecyclerOptions<ModelloArticolo> options, OnArticoloInteraction onArticoloInteraction) {
        super(options);
        this.onArticoloInteraction=onArticoloInteraction;
    }

    @Override
    protected void onBindViewHolder(@NonNull ArticoloViewHolder holder, int position, @NonNull ModelloArticolo model) {

        holder.nome_articolo.setText(model.getNome_articolo());
        holder.da_comprare.setText(String.valueOf(model.getDa_comprare()));

    }

    @NonNull
    @Override
    public ArticoloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modello_riga_articolo,parent,false);
        return new ArticoloViewHolder(view);
    }

    public class ArticoloViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView nome_articolo;
        private TextView da_comprare;


        public ArticoloViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_articolo= itemView.findViewById(R.id.nome_articolo);
            da_comprare = itemView.findViewById(R.id.da_comprare);
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
        void onArticoloClick(ModelloArticolo articolo, int position);
        void onArticoloLongClick(ModelloArticolo articolo, int position);
    }
}