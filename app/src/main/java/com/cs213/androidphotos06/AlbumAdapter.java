package com.cs213.androidphotos06;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private Context context;

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

        holder.itemView.setOnClickListener(view -> {
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
}


