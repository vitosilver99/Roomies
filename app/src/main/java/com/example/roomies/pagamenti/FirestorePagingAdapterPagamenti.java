package com.example.roomies.pagamenti;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


//con questa classe è possibile ricavare tutti i dati contenuti nell'adapter (di tipo FirestorePagingAdapter) presente in PagamentiFragment

/*
 * FirestoreAdapterPagamento è un'estensione di FirestorePagingAdapter (o di FirestoreRecyclerAdapter) che permette
 * di catturare i click di un elemento. Il metodo onClick è possibile implementarlo nella classe PagamentoViewHolder
 */
public class FirestorePagingAdapterPagamenti extends FirestorePagingAdapter<ModelloPagamento, FirestorePagingAdapterPagamenti.PagamentoViewHolder> {
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */

    //impostiamo l'interfaccia onPagamentoInteraction
    private OnPagamentoInteraction onPagamentoInteraction;


    public FirestorePagingAdapterPagamenti(@NonNull FirestorePagingOptions<ModelloPagamento> options, OnPagamentoInteraction onPagamentoInteraction) {
        super(options);
        this.onPagamentoInteraction = onPagamentoInteraction;
    }

    @Override
    protected void onBindViewHolder(@NonNull PagamentoViewHolder holder, int position, @NonNull ModelloPagamento model) {
        holder.nome_pagamento.setText(model.getNome_pagamento());
        //TODO verifica che toGMTString restituisca la data corretta

        Date scadenza_pagamento = model.getScadenza_pagamento();
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        String scadenza = formatter.format(scadenza_pagamento);
        holder.scadenza_pagamento.setText(scadenza);

        holder.non_pagato.setText(model.getNon_pagato()+"");
        if(model.getNon_pagato()>0) {
            holder.importo_totale.setTextColor(Color.RED);
            holder.euro_simbolo.setTextColor(Color.RED);
        }
        else {
            holder.importo_totale.setTextColor(Color.GREEN);
            holder.euro_simbolo.setTextColor(Color.GREEN);
        }

        holder.importo_totale.setText(model.getImporto_totale()+"");

        //qui possiamo ricavare anche l'id del pagamento tramite model.getPagamento_id() se in pagamentoFragment il parser modificato riempie anche quel campo


    }

    @NonNull
    @Override
    public PagamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modello_riga_pagamento,parent,false);

        //return new PagamentiFragment.PagamentoViewHolder(view) diventa return new PagamentoViewHolder(view)
        return new PagamentoViewHolder(view);
    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);

        //provo a notificare da qui gli osservatori sull'adapter
        notifyDataSetChanged();


        switch (state){
            case LOADING_INITIAL:
                //qui si può mostrare una progressBar
                Log.d("PAGING_LOG","Carico dati iniziali");
                break;
            case LOADING_MORE:
                //qui si può mostrare una progressBar
                Log.d("PAGING_LOG","Carico dati ulteriori");
                break;
            case FINISHED:
                Log.d("PAGING_LOG","Tutti i dati sono stati caricati");
                break;
            case ERROR:
                Log.d("PAGING_LOG","Errore nel caricamento dati");
                break;
            case LOADED:




                //qui si può nascondere la progressBar
                Log.d("PAGING_LOG","Totale dati caricati"+getItemCount());
                break;

        }
    }

    //classe PagamentoViewHolder deve essere public o private? secondo me private va anche bene
    public class PagamentoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView nome_pagamento;
        private TextView scadenza_pagamento;
        private TextView non_pagato;
        private TextView importo_totale;
        private TextView euro_simbolo;

        public PagamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_pagamento = itemView.findViewById(R.id.nome_pagamento);
            scadenza_pagamento = itemView.findViewById(R.id.scadenza_pagamento);
            non_pagato = itemView.findViewById(R.id.non_pagato);
            importo_totale = itemView.findViewById(R.id.importo_totale);
            euro_simbolo = itemView.findViewById(R.id.euro);

            //qui è possibile implementare i metodi onClick sull'intera View (chiamata in questo caso itemView) o su un singolo TextView (ad esempio nome_pagamento)
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        //PagamentoViewHolder deve implementare i listener di una View per poter effettuare click e long click
        @Override
        public void onClick(View v) {
            onPagamentoInteraction.onPagamentoClick(getItem(getAdapterPosition()),getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            Log.d("LONG_CLICK","long click");
            onPagamentoInteraction.onPagamentoLongClick(getItem(getAdapterPosition()),getAdapterPosition());


            //funziona sia se ritorna vero sia se ritorna falso
            return false;
        }
    }

    //creo un'interfaccia che può essere implementata in maniera diversa in base al frammento che utilizza questo adapter (FirestoreAdapterPagamento)
    public interface OnPagamentoInteraction {
         void onPagamentoClick(DocumentSnapshot snapshot, int position);
         void onPagamentoLongClick(DocumentSnapshot snapshot, int position);

    }


}
