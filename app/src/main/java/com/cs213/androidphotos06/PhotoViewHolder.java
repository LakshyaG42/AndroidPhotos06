package com.cs213.androidphotos06;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public TextView photoFileName;
    public ImageView photoImageView;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);

        photoFileName = itemView.findViewById(R.id.photoFileName);
        photoImageView = itemView.findViewById(R.id.photoImageView);
        itemView.setOnClickListener(view -> {
            // Toggle the background color or any other visual indicator
            itemView.setBackgroundColor(Color.LTGRAY); // Change to default color
        });

    }


}
