package com.CorpChat.CorpChat.AboutUser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.CorpChat.CorpChat.R;

import java.util.ArrayList;

public class CorpUserListAdapter extends RecyclerView.Adapter<CorpUserListAdapter.CorpUserListViewHolder> {

    ArrayList<CorpUserObject> CorpUserListArgs;

    public CorpUserListAdapter(ArrayList<CorpUserObject> CorpUserListArgs){
        this.CorpUserListArgs = CorpUserListArgs;
    }


    @NonNull
    @Override
    public CorpUserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newLayoutView.setLayoutParams(layoutParams);

        CorpUserListViewHolder listViewHolder = new CorpUserListViewHolder(newLayoutView);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CorpUserListViewHolder holder, final int position) {
        holder.phoneName.setText(CorpUserListArgs.get(position).getUserName());
        holder._phoneNumber.setText(CorpUserListArgs.get(position).getPhoneNumber());

        holder.include.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CorpUserListArgs.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return CorpUserListArgs.size();
    }



    class CorpUserListViewHolder extends RecyclerView.ViewHolder{
        TextView phoneName, _phoneNumber;
        LinearLayout linearLayout;
        CheckBox include;
        CorpUserListViewHolder(View view){

            super(view);
            phoneName = view.findViewById(R.id.Phname);
            _phoneNumber = view.findViewById(R.id.phnumber);
            include = view.findViewById(R.id.checkBoxAdd);
            linearLayout = view.findViewById(R.id.userlayout);
        }
    }
}
