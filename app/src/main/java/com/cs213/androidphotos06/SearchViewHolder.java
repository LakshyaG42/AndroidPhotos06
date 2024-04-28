package com.cs213.androidphotos06;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SearchViewHolder extends RecyclerView.ViewHolder {
    public TextView searchPhotoDescription;

    public ImageView searchImageView;


    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        searchPhotoDescription = itemView.findViewById(R.id.searchPhotoDescription);
        searchImageView = itemView.findViewById(R.id.searchImageView);
    }
}
