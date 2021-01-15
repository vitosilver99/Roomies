package com.example.roomies.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreRecyclerAdapterEventiHome extends FirestoreRecyclerAdapter<ModelloEventoHome,FirestoreRecyclerAdapterEventiHome.EventoHomeViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreRecyclerAdapterEventiHome(@NonNull FirestoreRecyclerOptions<ModelloEventoHome> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreRecyclerAdapterEventiHome.EventoHomeViewHolder holder, int position, @NonNull ModelloEventoHome model) {
        holder.nome_evento.setText(model.getNome_evento());
        holder.coinquilini.setText(model.getCoinquilini());
    }

    @NonNull
    @Override
    public FirestoreRecyclerAdapterEventiHome.EventoHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventi_giornalieri,parent,false);
        return new EventoHomeViewHolder(view);
    }

    public class EventoHomeViewHolder extends RecyclerView.ViewHolder {
        private TextView nome_evento;
        private TextView coinquilini;

        public EventoHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_evento = itemView.findViewById(R.id.nome_evento_giornaliero);
            coinquilini = itemView.findViewById(R.id.coinuquilini_evento_giornaliero);
        }
    }
}
