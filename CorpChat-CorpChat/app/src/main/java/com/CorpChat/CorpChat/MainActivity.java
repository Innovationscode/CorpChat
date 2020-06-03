package com.CorpChat.CorpChat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.CorpChat.CorpChat.CorpChat.CorpChatsAdapter;
import com.CorpChat.CorpChat.CorpChat.ChatObject;
import com.CorpChat.CorpChat.AboutUser.CorpUserObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chats;
    private RecyclerView.Adapter chatsAdapter;
    private RecyclerView.LayoutManager chatsLayoutManager;

    ArrayList<ChatObject> chatsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        Fresco.initialize(this);

        Button _signout = findViewById(R.id.signout);
        Button _searchUser = findViewById(R.id.searchUser);
        _searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchUserActivity.class));
            }
        });
        _signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();
                Intent newintent = new Intent(getApplicationContext(), SignInActivity.class);
                newintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newintent);
                finish();
                return;
            }
        });

        accessPermissions();
        startRecyclerView();
        getChats();
    }

    private void getChats(){
        DatabaseReference corpChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        corpChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                       ChatObject chat = new ChatObject(snapshot.getKey());
                       boolean  exists = false;
                       for (ChatObject mChatIterator : chatsArray){
                           if (mChatIterator.getChatKey().equals(chat.getChatKey()))
                               exists = true;
                       }
                       if (exists)
                           continue;
                       chatsArray.add(chat);
                       CreateChatData(chat.getChatKey());
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CreateChatData(String chatKey) {
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatKey).child("info");
        chatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String chatkey = "";

                    if(snapshot.child("id").getValue() != null)
                        chatkey = snapshot.child("id").getValue().toString();

                    for(DataSnapshot usersnapshot : snapshot.child("users").getChildren()){
                        for(ChatObject chat : chatsArray){
                            if(chat.getChatKey().equals(chatkey)){
                                CorpUserObject user = new CorpUserObject(usersnapshot.getKey());
                                chat.addUserToArrayList(user);
                                fetchUserData(user);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchUserData(CorpUserObject mUser) {
        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("user").child(mUser.getUserID());
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnap) {
                CorpUserObject userObject = new CorpUserObject(dataSnap.getKey());

                if(dataSnap.child("notificationKey").getValue() != null)
                    userObject.setNotificationID(dataSnap.child("notificationKey").getValue().toString());

                for(ChatObject chatObject : chatsArray){
                    for (CorpUserObject iterateUser : chatObject.getCorpUserObjectArrayList()){
                        if(iterateUser.getUserID().equals(userObject.getUserID())){
                            iterateUser.setNotificationID(userObject.getNotificationID());
                        }
                    }
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startRecyclerView() {
        chatsArray = new ArrayList<>();
        chats = findViewById(R.id.chatList);
        chats.setNestedScrollingEnabled(false);
        chats.setHasFixedSize(false);
        chatsLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        chats.setLayoutManager(chatsLayoutManager);
        chatsAdapter = new CorpChatsAdapter(chatsArray);
        chats.setAdapter(chatsAdapter);
    }

    private void accessPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }

}
