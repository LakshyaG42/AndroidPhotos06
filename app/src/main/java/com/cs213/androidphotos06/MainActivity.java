package com.cs213.androidphotos06;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumClickListener {
    private static final int REQUEST_STORAGE_PERMISSION = 1001;
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;

    int selectedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAlbums();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            displayItems();
            albumAdapter.setOnAlbumClickListener(this);
        }
        Button btnAddAlbum = findViewById(R.id.addAlbumButton);
        btnAddAlbum.setOnClickListener(view -> addAlbum());
        Button btnDeleteAlbum = findViewById(R.id.removeAlbumButton);
        btnDeleteAlbum.setOnClickListener(view -> deleteAlbum());
        Button btnRenameAlbum = findViewById(R.id.renameAlbumButton);
        btnRenameAlbum.setOnClickListener(view -> renameAlbum());
        Button btnOpenAlbum = findViewById(R.id.openAlbumButton);
        btnOpenAlbum.setOnClickListener(view -> openAlbum());
        Button btnSearch = findViewById(R.id.SearchButton);
        btnSearch.setOnClickListener(view -> searchPhotos());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void addAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Album Name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Album Name");
        builder.setView(input);
        boolean dupName = false;
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString().trim();
                // Add album to the list
                for (Album existing: Album.albumsList
                ) {
                    if(existing.getName().equals(albumName)) {
                        Toast.makeText(getApplicationContext(), "Album name already exists", Toast.LENGTH_SHORT).show();
                        addAlbum();
                        return;
                    }
                }
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
        if(selectedPosition == -1) {
            Toast.makeText(this, "Please select an album.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Album.albumsList.contains(albumAdapter.getSelected())) {
            Album.deleteFromAlbumList(albumAdapter.getSelected());
            albumAdapter.notifyDataSetChanged();
            updateAlbumsList();
            this.selectedPosition = -1;
        }
    }

    public void renameAlbum() {
        if(selectedPosition == -1) {
            Toast.makeText(this, "Please select an album.", Toast.LENGTH_SHORT).show();
            return;
        }
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
        this.selectedPosition = position;
    }

    private void openAlbum() {
        if(selectedPosition == -1) {
            Toast.makeText(this, "Please select an album.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Album.albumsList.contains(albumAdapter.getSelected())) {
            String selectedAlbum = Album.albumsList.get(selectedPosition).getName();
            Log.i("INFO", "Selected Album: " + selectedAlbum);
            Intent intent = new Intent(this, AlbumViewerActivity.class);
            intent.putExtra("selectedAlbum", selectedAlbum);
            startActivity(intent);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing storage
                // Your code to access external storage goes here
                displayItems();
            } else {
                // Permission denied, show a message or take appropriate action
                // For example, you can show a message and then finish the activity
                Toast.makeText(this, "Permission denied. Closing the app.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity or exit the app
            }
        }
    }




    //BELOW IS SEARCHING:
    private AutoCompleteTextView personTagInput;
    private AutoCompleteTextView locationTagInput;
    private Spinner operationSpinner;
    private List<String> personTags = new ArrayList<>();
    private List<String> locationTags = new ArrayList<>();

    private void searchPhotos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Photos");

        // Set up the input
        final AutoCompleteTextView personInput = new AutoCompleteTextView(this);
        personInput.setInputType(InputType.TYPE_CLASS_TEXT);
        personInput.setHint("Enter person tag");

        final AutoCompleteTextView locationInput = new AutoCompleteTextView(this);
        locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
        locationInput.setHint("Enter location tag");

        // Create autocomplete lists for person and location tags
        ArrayList<String> personTags = getPersonTags();
        ArrayList<String> locationTags = getLocationTags();

        // Set up adapter for autocomplete suggestions
        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, personTags);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locationTags);

        personInput.setAdapter(personAdapter);
        locationInput.setAdapter(locationAdapter);

        // Set up the operation spinner
        Spinner operationSpinner = new Spinner(this);
        ArrayAdapter<String> operationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList("Conjunction", "Disjunction", "Neither"));
        operationSpinner.setAdapter(operationAdapter);

        // Set the layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(personInput);
        layout.addView(locationInput);
        layout.addView(operationSpinner);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Search", (dialog, which) -> {
            String personTag = personInput.getText().toString().trim().toLowerCase();
            String locationTag = locationInput.getText().toString().trim().toLowerCase();
            String operation = operationSpinner.getSelectedItem().toString();
            Log.i("INFO", "Stored from Popup:" + personTag + " and " + locationTag + " and " + operation);

            if((personTag.isEmpty() || locationTag.isEmpty()) && operation.equals("Conjunction")) {
                Toast.makeText(this, "To use Conjunction Search must input for both Person Tag and Location Tag.", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("personTag", personTag);
            intent.putExtra("locationTag", locationTag);
            intent.putExtra("operation", operation);

            startActivity(intent);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to get all existing person tags from all photos in all albums
    private ArrayList<String> getPersonTags() {
        ArrayList<String> personTags = new ArrayList<>();
        for (Album album : Album.albumsList) {
            for (Photo photo : album.getPhotos()) {
                String tag = photo.getPerson();
                if (!tag.isEmpty() && !personTags.contains(tag)) {
                    personTags.add(tag);
                }
            }
        }
        return personTags;
    }

    // Method to get all existing location tags from all photos in all albums
    private ArrayList<String> getLocationTags() {
        ArrayList<String> locationTags = new ArrayList<>();
        for (Album album : Album.albumsList) {
            for (Photo photo : album.getPhotos()) {
                String tag = photo.getLocation();
                if (!tag.isEmpty() && !locationTags.contains(tag)) {
                    locationTags.add(tag);
                }
            }
        }
        return locationTags;
    }

}