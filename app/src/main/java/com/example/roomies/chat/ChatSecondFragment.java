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

public class ChatSecondFragment extends Fragment {

    private UsersAdapterChat usersAdapterChat;
    private List<UtentiClass> mUser;

    FirebaseUser fuser;
    DatabaseReference reference;
    public String casaId;

    private List<ChatList> usersList;

    ImageView immagine_empty;
    TextView testo_empty;

    RecyclerView recyclerView;

    public ChatSecondFragment() {
        // Required empty public constructor
    }



    public static ChatSecondFragment newInstance(String param1, String param2) {
        ChatSecondFragment fragment = new ChatSecondFragment();
        Bundle args = new Bundle();
        args.putString("casaId", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            casaId = getArguments().getString("casaId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_second,container,false);

        recyclerView = view.findViewById(R.id.recyclerview_chat_utenti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        immagine_empty = view.findViewById(R.id.imageView_empty_chat);
        testo_empty = view.findViewById(R.id.textView_empty_chat);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child(casaId)
                .child("chatList")
                .child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                //ciclo per tutti gli utenti
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                    ChatList chatList = new ChatList(hashMap.get("id").toString());

                    usersList.add(chatList);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void chatList(){
        // prendo tutte le chat recenti

        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(casaId).child("utenti");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                    UtentiClass utente = new UtentiClass(hashMap.get("nome_cognome").toString(),hashMap.get("user_id").toString());
                    for(ChatList chatList : usersList){
                        if( utente.getUserId().equals(chatList.getId())){
                            mUser.add(utente);
                        }
                    }

                }

                if(!mUser.isEmpty()){
                    immagine_empty.setVisibility(View.INVISIBLE);
                    testo_empty.setVisibility(View.INVISIBLE);

                    usersAdapterChat = new UsersAdapterChat(getContext(),mUser,casaId);
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