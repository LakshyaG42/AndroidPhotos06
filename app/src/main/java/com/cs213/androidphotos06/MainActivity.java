package com.cs213.androidphotos06;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumClickListener {
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        displayItems();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        albumAdapter.setOnAlbumClickListener(this);
    }
    private void displayItems() {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        albumAdapter = new AlbumAdapter(this);
        recyclerView.setAdapter(albumAdapter);
    }

    public void onAlbumClick(int position) {
        Album selectedAlbum = Album.albumsList.get(position);
        String albumName = selectedAlbum.getName();
        // Use the selectedAlbum or albumName as needed
        // Example: Toast.makeText(this, "Selected Album: " + albumName, Toast.LENGTH_SHORT).show();
    }
}