package com.CorpChat.CorpChat;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.CorpChat.CorpChat.AboutUser.CorpUserListAdapter;
import com.CorpChat.CorpChat.AboutUser.CorpUserObject;
import com.CorpChat.CorpChat.UtilitiesLib.PhoneNumberPrefix;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView _corpUserList;
    private RecyclerView.Adapter corpUserListAdapter;
    private RecyclerView.LayoutManager corpUserListLayoutManager;

    ArrayList<CorpUserObject> corpArrayUserList, corpUserContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        corpUserContactList = new ArrayList<>();
        corpArrayUserList = new ArrayList<>();

        Button createChats = findViewById(R.id.createChatId);
        createChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
            }
        });

        startRecyclerView();
        accessContactList();
    }

    private void createChat(){
        String chatskeys = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatskeys).child("info");
        DatabaseReference corpUserDB = FirebaseDatabase.getInstance().getReference().child("user");

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", chatskeys);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);

        Boolean validChat = false;
        for(CorpUserObject mUser : corpArrayUserList){
            if(mUser.getSelected()){
                validChat = true;
                newChatMap.put("users/" + mUser.getUserID(), true);
                corpUserDB.child(mUser.getUserID()).child("chat").child(chatskeys).setValue(true);
            }
        }

        if(validChat){
            chatInfoDb.updateChildren(newChatMap);
            corpUserDB.child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatskeys).setValue(true);
        }

    }

    private void accessContactList(){

        String countryIsoprefix = getCountryISO();

        Cursor cursorForContactList = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(cursorForContactList.moveToNext()){
            String contactName = cursorForContactList.getString(cursorForContactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactPhoneNumber = cursorForContactList.getString(cursorForContactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contactPhoneNumber = contactPhoneNumber.replace(" ", "");
            contactPhoneNumber = contactPhoneNumber.replace("-", "");
            contactPhoneNumber = contactPhoneNumber.replace("(", "");
            contactPhoneNumber = contactPhoneNumber.replace(")", "");

            if(!String.valueOf(contactPhoneNumber.charAt(0)).equals("+"))
                contactPhoneNumber = countryIsoprefix + contactPhoneNumber;

            CorpUserObject contact = new CorpUserObject("", contactName, contactPhoneNumber);
            corpUserContactList.add(contact);
            getUserDetails(contact);
        }
    }

    private void getUserDetails(CorpUserObject mContact) {
        DatabaseReference newUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = newUserDB.orderByChild("phone").equalTo(mContact.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String  phone = "",
                            name = "";
                    for(DataSnapshot childSnapshot : snapshot.getChildren()){
                        if(childSnapshot.child("phone").getValue()!=null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();


                        CorpUserObject newUser = new CorpUserObject(childSnapshot.getKey(), name, phone);
                        if (name.equals(phone))
                            for(CorpUserObject mContactIterator : corpUserContactList){
                                if(mContactIterator.getPhoneNumber().equals(newUser.getPhoneNumber())){
                                    newUser.setUserName(mContactIterator.getUserName());
                                }
                            }

                        corpArrayUserList.add(newUser);
                        corpUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private String getCountryISO(){
        String iso_phone = null;

        TelephonyManager telManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telManager.getNetworkCountryIso()!=null)
            if (!telManager.getNetworkCountryIso().toString().equals(""))
                iso_phone = telManager.getNetworkCountryIso().toString();

        return PhoneNumberPrefix.getPhone(iso_phone);
    }

    private void startRecyclerView() {
        _corpUserList = findViewById(R.id.corpUserList);
        _corpUserList.setNestedScrollingEnabled(false);
        _corpUserList.setHasFixedSize(false);
        corpUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        _corpUserList.setLayoutManager(corpUserListLayoutManager);
        corpUserListAdapter = new CorpUserListAdapter(corpArrayUserList);
        _corpUserList.setAdapter(corpUserListAdapter);
    }
}
