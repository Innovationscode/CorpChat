package com.CorpChat.CorpChat.CorpChat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.CorpChat.CorpChat.MessagingActivity;
import com.CorpChat.CorpChat.R;

import java.util.ArrayList;

public class CorpChatsAdapter extends RecyclerView.Adapter<CorpChatsAdapter.CorpChatListViewHolder> {

    ArrayList<ChatObject> chatsArgs;

    public CorpChatsAdapter(ArrayList<ChatObject> chatList){
        this.chatsArgs = chatList;
    }

    @NonNull
    @Override
    public CorpChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inflate.setLayoutParams(lp);

        CorpChatListViewHolder listVHolder = new CorpChatListViewHolder(inflate);
        return listVHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CorpChatListViewHolder holder, final int position) {
        holder.titles.setText(chatsArgs.get(position).getChatKey());

        holder.userlayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(v.getContext(), MessagingActivity.class);
                newIntent.putExtra("chatObject", chatsArgs.get(holder.getAdapterPosition()));
                v.getContext().startActivity(newIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatsArgs.size();
    }





    public class CorpChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView titles;
        public LinearLayout userlayOut;
        public CorpChatListViewHolder(View view){
            super(view);
            titles = view.findViewById(R.id.title);
            userlayOut = view.findViewById(R.id.userlayout);
        }
    }
}
