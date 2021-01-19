package com.example.roomies.home;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomies.R;
import com.example.roomies.calendario.UtentiClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterHomeProfili  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<UtentiClass> utentiClassList;
    private int ultima_posizione;
    private View view;
    private String casaId;

    private static int TYPE_ADD = 1;
    private static int TYPE_USER = 2;

    public RecyclerViewAdapterHomeProfili(Context mContext, List<UtentiClass> utentiClasses, int ultima_posizione, String casaId){
        this.mContext = mContext;
        this.utentiClassList = utentiClasses;
        this.ultima_posizione = ultima_posizione;
        this.casaId = casaId;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

                    Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.dialog_aggiungi_utente_alla_home);
                    Button chiudi_dialog=dialog.findViewById(R.id.cihudi_dialog_add_utente);
                    TextView codice_casa=dialog.findViewById(R.id.codice_casa_dialog);
                    ImageView copia_codice = dialog.findViewById(R.id.copy_codice_casa_dialog);

                    codice_casa.setText(casaId);

                    copia_codice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Codice casa", casaId);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(view.getContext(),"Codice copiato",Toast.LENGTH_LONG).show();
                        }
                    });

                    chiudi_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
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

            if(nome.length()>=8){
                viewHolder.nome_selezionato.setTextSize(10);
            }else{
                viewHolder.nome_selezionato.setTextSize(14);
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
