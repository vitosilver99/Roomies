package com.example.roomies.calendario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.roomies.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapterVisualizzaEvento extends RecyclerView.Adapter<RecyclerviewAdapterVisualizzaEvento.MyViewHolder> {
    private Context mContext;
    private List<EventiClass> eventiRecyclerViewList;
    private List<UtentiClass> interessati;


    RecyclerviewAdapterVisualizzaEvento(Context context){
        mContext = context;
        eventiRecyclerViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        EventiClass eventiRecyclerView = eventiRecyclerViewList.get(position);
        holder.tvTaskName.setText(eventiRecyclerView.getNome());

        interessati =eventiRecyclerView.getPartecipanti();
        String part="";
        for(int i = 0; i<interessati.size();i++ ){
            if(i==interessati.size()-1){
                part = part + interessati.get(i).getNome_cognome()+"";
            }
            else {
                part = part + interessati.get(i).getNome_cognome()+" - ";
            }
        }
        holder.tvTaskDesc.setText(part);
    }

    @Override
    public int getItemCount() {
        return eventiRecyclerViewList.size();
    }

    public void setEventiRecyclerViewList(List<EventiClass> eventiRecyclerViewList) {
        this.eventiRecyclerViewList = eventiRecyclerViewList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskName;
        private TextView tvTaskDesc;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.task_name);
            tvTaskDesc = itemView.findViewById(R.id.task_desc);
        }
    }
}