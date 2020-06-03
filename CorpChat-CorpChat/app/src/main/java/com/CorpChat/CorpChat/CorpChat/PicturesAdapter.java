package com.CorpChat.CorpChat.CorpChat;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.CorpChat.CorpChat.R;

import java.util.ArrayList;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>{

    ArrayList<String> picturesArray;
    Context newContext;

    public PicturesAdapter(Context newContext, ArrayList<String> picturesArray){
        this.newContext = newContext;
        this.picturesArray = picturesArray;
    }

    @NonNull
    @Override
    public PicturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictures,null, false);
        PicturesViewHolder picturesViewHolder = new PicturesViewHolder(layoutview);


        return picturesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PicturesViewHolder holder, int position) {
        Glide.with(newContext).load(Uri.parse(picturesArray.get(position))).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return picturesArray.size();
    }


    public class PicturesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public PicturesViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pictures);
        }
    }
}
