package com.example.roomies.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roomies.AdapterCheckCasa;
import com.example.roomies.R;
import com.example.roomies.calendario.UtentiClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UsersFragment extends Fragment {

    private static final String ARG_USER_ID = "param1";
    private static final String ARG_CASA_ID = "param2";

    public String casaId;
    public String userId;

    private RecyclerView recyclerView;
    private UsersAdapterChat usersAdapterChat;
    private List<UtentiClass> utentiClasses;

    ImageView immagine_empty;
    TextView testo_empty;

    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, param1);
        args.putString(ARG_CASA_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            casaId = getArguments().getString(ARG_CASA_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_users,container,false);

            recyclerView = view.findViewById(R.id.RecyclerViewUsers);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            immagine_empty = view.findViewById(R.id.imageView_utente_empty);
            testo_empty = view.findViewById(R.id.textView_utente_empty);

            utentiClasses = new ArrayList<>();

            readUsers();

            return view;
    }

    private void readUsers()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(casaId).child("utenti");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                utentiClasses.clear();

                for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    HashMap<String,Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                    if(hashMap!=null){
                        UtentiClass utente = new UtentiClass(hashMap.get("nome_cognome").toString(),hashMap.get("user_id").toString());
                        if(!utente.getUserId().equals(userId)){
                            utentiClasses.add(utente);
                        }
                    }

                }
                if(!utentiClasses.isEmpty()){
                    immagine_empty.setVisibility(View.INVISIBLE);
                    testo_empty.setVisibility(View.INVISIBLE);

                    usersAdapterChat = new UsersAdapterChat(getContext(),utentiClasses,casaId);
                    recyclerView.setAdapter(usersAdapterChat);
                }else{
                    immagine_empty.setVisibility(View.VISIBLE);
                    testo_empty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}