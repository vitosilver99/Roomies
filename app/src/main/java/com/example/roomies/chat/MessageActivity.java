package com.example.roomies.chat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.roomies.R;
import com.example.roomies.calendario.UtentiClass;
import com.google.android.gms.common.SignInButton;
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

public class MessageActivity extends AppCompatActivity {


    TextView username;
    ImageView imageView;
    String casaId;
    String userIdRicevente;

    RecyclerView recyclerViewy;
    EditText msg_editText;
    ImageButton sendBtn;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //imageView = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.username_chat);

        sendBtn = findViewById(R.id.btn_send);
        msg_editText = findViewById(R.id.text_send);

        //Recyclerview
        recyclerView = findViewById(R.id.recyclerview_chat);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        intent = getIntent();
        userIdRicevente = intent.getStringExtra("userId");
        casaId = intent.getStringExtra("casaId");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(casaId).child("utenti").child(userIdRicevente);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                UtentiClass utente;
                if (hashMap != null) {
                    utente = new UtentiClass(hashMap.get("nome_cognome").toString(), hashMap.get("user_id").toString());

                    username.setText(utente.getNome_cognome());

                    //TODO aggiungere immagine del profilo

                    readMessagers(fuser.getUid(), userIdRicevente, "default");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_editText.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(),userIdRicevente,msg);
                }

                msg_editText.setText("");
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciver", receiver);
        hashMap.put("message",message);

        reference.child(casaId).child("chats").push().setValue(hashMap);

        // Aggiungere un utente al fragment chat: Ultima chat con i contatti

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference(casaId).child("chatList")
                .child(fuser.getUid())
                .child(userIdRicevente);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userIdRicevente);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessagers(String myid, String userid, String imageurl){

        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child(casaId).child("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();
                    Chat chat = new Chat(hashMap.get("sender").toString(),hashMap.get("reciver").toString(),hashMap.get("message").toString());

                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                        chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){

                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setSupportActionBar(Toolbar toolbar) {
    }
}