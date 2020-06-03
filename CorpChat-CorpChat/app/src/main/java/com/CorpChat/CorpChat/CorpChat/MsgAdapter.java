package com.CorpChat.CorpChat.CorpChat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.CorpChat.CorpChat.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    ArrayList<MsgObject> messageArray;

    public MsgAdapter(ArrayList<MsgObject> messageArray){
        this.messageArray = messageArray;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutviews = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutviews.setLayoutParams(params);

        MsgViewHolder messageViewHolder = new MsgViewHolder(layoutviews);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MsgViewHolder holder, final int position) {
        holder.message.setText(messageArray.get(position).getMsgText());
        holder.senderM.setText(messageArray.get(position).getMsgSenderId());

        if(messageArray.get(holder.getAdapterPosition()).getImageUrlList().isEmpty())
            holder.mediaBtn.setVisibility(View.GONE);

        holder.mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageArray.get(holder.getAdapterPosition()).getImageUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageArray.size();
    }





    class MsgViewHolder extends RecyclerView.ViewHolder{
        TextView message,
                    senderM;
        Button mediaBtn;
        LinearLayout linearLayout;
        MsgViewHolder(View view){
            super(view);
            linearLayout = view.findViewById(R.id.userlayout);

            message = view.findViewById(R.id.usersLayout);
            senderM = view.findViewById(R.id.msgSender);

            mediaBtn = view.findViewById(R.id.pictures);
        }
    }
}
