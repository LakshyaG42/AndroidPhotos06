package com.cs213.androidphotos06;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private Context context;

    private int selectedPosition;
    private OnAlbumClickListener clickListener;
    public AlbumAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewHolder(LayoutInflater.from(context).inflate(R.layout.album_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = Album.albumsList.get(position);
        holder.albumName.setText(Album.albumsList.get(position).getName());

        if (position == selectedPosition) {
            // Change the appearance of the selected item
            holder.itemView.setBackgroundColor(Color.LTGRAY); // Change to selected color
        } else {
            // Change the appearance of the non-selected items
            holder.itemView.setBackgroundColor(Color.WHITE); // Change to default color
        }

        holder.itemView.setOnClickListener(view -> {
            // Update the selected position
            selectedPosition = position;
            notifyDataSetChanged(); // Notify adapter about the data set change
            if (clickListener != null) {
                clickListener.onAlbumClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Album.albumsList.size();
    }

    public interface OnAlbumClickListener {
        void onAlbumClick(int position);
    }

    public void setOnAlbumClickListener(OnAlbumClickListener listener) {
        this.clickListener = listener;
    }

    public Album getSelected() {
        return Album.albumsList.get(this.selectedPosition);
    }
}


