package com.cs213.androidphotos06;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoViewHolder>  {
    private Context context;

    private int selectedPosition = -1;
    private Album currentAlbum;
    private OnPhotoClickListener clickListener;
    public PhotoListAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        for (Album album: Album.albumsList) {
            if(album.getName().equals(AlbumViewerActivity.currentAlbumName)) {
                currentAlbum = album;
            }
        }
        return new PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        for (Album album: Album.albumsList) {
            if(album.getName().equals(AlbumViewerActivity.currentAlbumName)) {
                currentAlbum = album;
                break;
            }
        }

        String text = currentAlbum.getPhotos().get(position).getFilePath() + "\n Tags: \n Person - " + currentAlbum.getPhotos().get(position).getPerson() + "\n Location - " + currentAlbum.getPhotos().get(position).getLocation();
        holder.photoFileName.setText(text);

        String filePath = currentAlbum.getPhotos().get(position).getFilePath();


        if (filePath != null && !filePath.isEmpty()) {
            // Check if the file path is a content URI
            if (filePath.startsWith("content://")) {
                // Load the image from the content URI using a ContentResolver
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(Uri.parse(filePath));
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.photoImageView.setImageBitmap(bitmap);
                        inputStream.close();
                    } else {
                        Log.e("Error", "Failed to open input stream for content URI: " + filePath);
                    }
                } catch (IOException e) {
                    Log.e("Error", "Failed to load image from content URI: " + e.getMessage());
                }
            } else {
                // Handle direct file path (if needed)
                File imageFile = new File(filePath);
                if (imageFile.exists() && isImageFile(imageFile)) {
                    Uri fileUri = Uri.fromFile(imageFile);
                    holder.photoImageView.setImageURI(fileUri);
                } else {
                    Log.e("Error", "Invalid file path or not an image file: " + filePath);
                }
            }
        } else {
            Log.e("Error", "File path is empty or null");
        }
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
                clickListener.OnPhotoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        for (Album album: Album.albumsList) {
            if(album.getName().equals(AlbumViewerActivity.currentAlbumName)) {
                currentAlbum = album;
            }
        }
        return currentAlbum.getPhotos().size();
    }

    public interface OnPhotoClickListener {
        void OnPhotoClick(int position);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.clickListener = listener;
    }

    private boolean isImageFile(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image");
    }
}


