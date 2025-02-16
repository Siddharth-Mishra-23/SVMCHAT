package com.example.svmchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    private String reciverimg, reciverUid, reciverName, SenderUID;
    private CircleImageView profile;
    private TextView reciverNName;
    private CardView sendbtn;
    private EditText textmsg;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    public static String senderImg;
    public static String reciverIImg;
    private String senderRoom, reciverRoom;
    private RecyclerView mmessangesAdpter;
    private ArrayList<msgModelclass> messagesArrayList;
    private messagesAdpter messagesAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Initialize UI components
        mmessangesAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessangesAdpter.setLayoutManager(linearLayoutManager);

        messagesArrayList = new ArrayList<>();
        messagesAdpter = new messagesAdpter(chatWin.this, messagesArrayList);
        mmessangesAdpter.setAdapter(messagesAdpter);

        // Get data from Intent
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        // Initialize UI components
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        profile = findViewById(R.id.profilechat);
        reciverNName = findViewById(R.id.recivername);

        // Load receiver image and name
        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(reciverName);

        // Create chat room references
        SenderUID = firebaseAuth.getUid();
        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;

        // Load messages from the database
        loadMessages();

        // Send message button click listener
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void loadMessages() {
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    messagesArrayList.add(messages);
                }
                messagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chatWin.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String message = textmsg.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(chatWin.this, "KUCH LIKHO TOH !!!", Toast.LENGTH_SHORT).show();
            return; // Exit early if message is empty
        }

        textmsg.setText("");
        Date date = new Date();
        msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime());

        // Send message to senderRoom
        database.getReference().child("chats").child(senderRoom).child("messages").push()
                .setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Send message to reciverRoom as well
                            database.getReference().child("chats").child(reciverRoom).child("messages").push()
                                    .setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // You can add any logic after sending the message
                                        }
                                    });
                        }
                    }
                });
    }
}
