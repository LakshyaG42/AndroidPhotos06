package com.cs213.androidphotos06;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>{

    private Context context;
    private ArrayList<Photo> searchResults;

    public SearchAdapter(Context context, ArrayList<Photo> searchResults) {
        this.searchResults = searchResults;
        this.context = context;

    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        String text = searchResults.get(position).getFilePath() + "\n Tags: \n Person - " + searchResults.get(position).getPerson() + "\n Location - " + searchResults.get(position).getLocation();
        holder.searchPhotoDescription.setText(text);
        String filePath = searchResults.get(position).getFilePath();
        if (filePath != null && !filePath.isEmpty()) {
            // Check if the file path is a content URI
            if (filePath.startsWith("content://")) {
                // Load the image from the content URI using a ContentResolver
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(Uri.parse(filePath));
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.searchImageView.setImageBitmap(bitmap);
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
                    holder.searchImageView.setImageURI(fileUri);
                } else {
                    Log.e("Error", "Invalid file path or not an image file: " + filePath);
                }
            }
        } else {
            Log.e("Error", "File path is empty or null");
        }

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private boolean isImageFile(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType != null && mimeType.startsWith("image");
    }
}
