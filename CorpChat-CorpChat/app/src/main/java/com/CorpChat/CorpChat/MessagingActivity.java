package com.CorpChat.CorpChat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.CorpChat.CorpChat.CorpChat.ChatObject;
import com.CorpChat.CorpChat.CorpChat.PicturesAdapter;
import com.CorpChat.CorpChat.CorpChat.MsgAdapter;
import com.CorpChat.CorpChat.CorpChat.MsgObject;
import com.CorpChat.CorpChat.AboutUser.CorpUserObject;
import com.CorpChat.CorpChat.UtilitiesLib.NotificationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagingActivity extends AppCompatActivity {

    private RecyclerView message, picturesss;
    private RecyclerView.Adapter messageAdapter, picturessAdapter;
    private RecyclerView.LayoutManager messageLayoutManager, picturesLayoutManager;

    ArrayList<MsgObject> messageArray;

    ChatObject chatObjct;

    DatabaseReference chatDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        chatObjct = (ChatObject) getIntent().getSerializableExtra("chatObject");

        chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatObjct.getChatKey()).child("messages");

        Button send = findViewById(R.id.sendMsg);
        Button mediaBtn = findViewById(R.id.media);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessGallery();
            }
        });

        initializeMsgRCV();
        initializePicturesRCV();
        getMessages();
    }

    private void getMessages() {
        chatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                if(snapshot.exists()){
                    String  text = "",
                            originatorID = "";
                    ArrayList<String> imageUrlList = new ArrayList<>();

                    if(snapshot.child("text").getValue() != null)
                        text = snapshot.child("text").getValue().toString();
                    if(snapshot.child("creator").getValue() != null)
                        originatorID = snapshot.child("creator").getValue().toString();

                    if(snapshot.child("media").getChildrenCount() > 0)
                        for (DataSnapshot mediaSnapshot : snapshot.child("media").getChildren())
                            imageUrlList.add(mediaSnapshot.getValue().toString());

                    MsgObject newMsg = new MsgObject(snapshot.getKey(), originatorID, text, imageUrlList);
                    messageArray.add(newMsg);
                    messageLayoutManager.scrollToPosition(messageArray.size()-1);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    int totalImageUploaded = 0;
    ArrayList<String> imageIdList = new ArrayList<>();
    EditText messageChat;
    private void sendMsg(){
        messageChat = findViewById(R.id.msg);

        String messageId = chatDB.push().getKey();
        final DatabaseReference newMessageDb = chatDB.child(messageId);

        final Map newMessageMap = new HashMap<>();

        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

        if(!messageChat.getText().toString().isEmpty())
            newMessageMap.put("text", messageChat.getText().toString());


        if(!mediaUriArray.isEmpty()){
            for (String mediaUri : mediaUriArray){
                String mediaId = newMessageDb.child("media").push().getKey();
                imageIdList.add(mediaId);
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatObjct.getChatKey()).child(messageId).child(mediaId);

                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("/media/" + imageIdList.get(totalImageUploaded) + "/", uri.toString());

                                totalImageUploaded++;
                                if(totalImageUploaded == mediaUriArray.size())
                                    updateDatabaseWithNewMessage(newMessageDb, newMessageMap);

                            }
                        });
                    }
                });
            }
        }else{
            if(!messageChat.getText().toString().isEmpty())
                updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
        }


    }


    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap){
        newMessageDb.updateChildren(newMessageMap);
        messageChat.setText(null);
        mediaUriArray.clear();
        imageIdList.clear();
        totalImageUploaded =0;
        picturessAdapter.notifyDataSetChanged();

        String newMsg;

        if(newMessageMap.get("text") != null)
            newMsg = newMessageMap.get("text").toString();
        else
            newMsg = "Sent Media";

        for(CorpUserObject user : chatObjct.getCorpUserObjectArrayList()){
            if(!user.getUserID().equals(FirebaseAuth.getInstance().getUid())){
                new NotificationManager(newMsg, "New Message", user.getNotificationID());
            }
        }
    }

    private void initializeMsgRCV() {
        messageArray = new ArrayList<>();
        message = findViewById(R.id.messageList);
        message.setNestedScrollingEnabled(false);
        message.setHasFixedSize(false);
        messageLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        message.setLayoutManager(messageLayoutManager);
        messageAdapter = new MsgAdapter(messageArray);
        message.setAdapter(messageAdapter);
    }



    int SELECT_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriArray = new ArrayList<>();

    private void initializePicturesRCV() {
        mediaUriArray = new ArrayList<>();
        picturesss = findViewById(R.id.mediaList);
        picturesss.setNestedScrollingEnabled(false);
        picturesss.setHasFixedSize(false);
        picturesLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        picturesss.setLayoutManager(picturesLayoutManager);
        picturessAdapter = new PicturesAdapter(getApplicationContext(), mediaUriArray);
        picturesss.setAdapter(picturessAdapter);
    }

    private void accessGallery() {
        Intent newIntent = new Intent();
        newIntent.setType("image/*");
        newIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        newIntent.setAction(newIntent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(newIntent, "Select Picture(s)"), SELECT_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaUriArray.add(data.getData().toString());
                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriArray.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                picturessAdapter.notifyDataSetChanged();
            }
        }
    }
}
