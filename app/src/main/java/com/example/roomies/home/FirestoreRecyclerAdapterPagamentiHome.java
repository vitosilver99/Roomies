package com.example.roomies.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.pagamenti.ModelloPagamento;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FirestoreRecyclerAdapterPagamentiHome extends FirestoreRecyclerAdapter<ModelloPagamento, FirestoreRecyclerAdapterPagamentiHome.PagamentoHomeViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreRecyclerAdapterPagamentiHome(@NonNull FirestoreRecyclerOptions<ModelloPagamento> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreRecyclerAdapterPagamentiHome.PagamentoHomeViewHolder holder, int position, @NonNull ModelloPagamento model) {
        holder.nome_pagamento.setText(model.getNome_pagamento());

        holder.non_pagato.setText(model.getNon_pagato()+"");
        if(model.getNon_pagato()>0) {
            holder.importo_totale.setTextColor(Color.RED);
            holder.euro_simbolo.setTextColor(Color.RED);
        }
        else {
            holder.importo_totale.setTextColor(Color.GREEN);
            holder.euro_simbolo.setTextColor(Color.GREEN);
        }

        holder.importo_totale.setText(String.format("%.2f", model.getImporto_totale()));

    }

    @NonNull
    @Override
    public FirestoreRecyclerAdapterPagamentiHome.PagamentoHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modello_riga_pagamento_home,parent,false);
        return new PagamentoHomeViewHolder(view);
    }

    public class PagamentoHomeViewHolder extends RecyclerView.ViewHolder {
        private TextView nome_pagamento;
        private TextView non_pagato;
        private TextView importo_totale;
        private TextView euro_simbolo;

        public PagamentoHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_pagamento = itemView.findViewById(R.id.nome_pagamento_home);
            non_pagato = itemView.findViewById(R.id.non_pagato_home);
            importo_totale = itemView.findViewById(R.id.importo_totale_home);
            euro_simbolo = itemView.findViewById(R.id.euro_home);
        }
    }

    @Override
    public void onDataChanged() {
        //quando cambiano i dati di firestore avvisa gli observer
        notifyDataSetChanged();
    }
}
