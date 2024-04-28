package com.cs213.androidphotos06;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumViewHolder extends RecyclerView.ViewHolder {
    public TextView albumName;

    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);
        albumName = itemView.findViewById(R.id.albumName);
    }


}
