package com.cs213.androidphotos06;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumViewHolder extends RecyclerView.ViewHolder {
    public TextView albumName;

    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);
        albumName = itemView.findViewById(R.id.albumName);
        itemView.setOnClickListener(view -> {
            // Toggle the background color or any other visual indicator
            if (itemView.getBackground() != null) {
                // Check if the background color is already changed
                itemView.setBackgroundColor(Color.TRANSPARENT); // Change to default color
            } else {
                itemView.setBackgroundColor(Color.LTGRAY); // Change to new color
            }
        });

    }


}
