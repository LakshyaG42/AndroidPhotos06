package com.cs213.androidphotos06;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumClickListener {
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAlbums();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        displayItems();
        Button btnAddAlbum = findViewById(R.id.addAlbumButton);
        btnAddAlbum.setOnClickListener(view -> addAlbum());
        Button btnDeleteAlbum = findViewById(R.id.removeAlbumButton);
        btnDeleteAlbum.setOnClickListener(view -> deleteAlbum());
        Button btnRenameAlbum = findViewById(R.id.renameAlbumButton);
        btnRenameAlbum.setOnClickListener(view -> renameAlbum());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        albumAdapter.setOnAlbumClickListener(this);
    }

    private void addAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Album Name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Album Name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString().trim();
                // Add album to the list
                if (!albumName.isEmpty()) {
                    Album newAlbum = new Album(albumName);
                    albumAdapter.notifyDataSetChanged();
                    updateAlbumsList();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void deleteAlbum() {
        if(Album.albumsList.contains(albumAdapter.getSelected())) {
            Album.deleteFromAlbumList(albumAdapter.getSelected());
            albumAdapter.notifyDataSetChanged();
            updateAlbumsList();
        }
    }

    public void renameAlbum() {
        if(Album.albumsList.contains(albumAdapter.getSelected())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter New Album Name");

            // Set up the input
            final EditText input = new EditText(this);
            input.setHint("Album Name");
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String albumName = input.getText().toString().trim();
                    // Add album to the list
                    if (!albumName.isEmpty()) {
                        albumAdapter.getSelected().setName(albumName);
                        albumAdapter.notifyDataSetChanged();
                        updateAlbumsList();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
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
    }


    private void updateAlbumsList() {
        try {
            FileOutputStream fileOut = openFileOutput("albums.ser", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(Album.albumsList);
            out.close();
            fileOut.close();
            Log.i("INFO", "Albums list saved successfully");
        } catch (IOException e) {
            Log.e("Saving", "Albums list saving unsuccessful Error Msg:" + e.getMessage());
        }

        try {
            FileInputStream fileIn = openFileInput("albums.ser");
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);
            Album.albumsList = (ArrayList<Album>) inputStream.readObject();
            inputStream.close();
            fileIn.close();
            Log.i("INFO", "Album list loaded successfully");
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAlbums() {
        try {
            FileInputStream fileIn = openFileInput("albums.ser");
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);
            Album.albumsList = (ArrayList<Album>) inputStream.readObject();
            inputStream.close();
            fileIn.close();
            Log.i("INFO", "Album list loaded successfully");
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}