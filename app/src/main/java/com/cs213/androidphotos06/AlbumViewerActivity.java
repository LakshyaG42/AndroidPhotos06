package com.cs213.androidphotos06;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AlbumViewerActivity extends AppCompatActivity implements PhotoListAdapter.OnPhotoClickListener {

    private static final int MEDIA_IMAGE_REQUEST_CODE = 1001;
    RecyclerView recyclerView;
    PhotoListAdapter photoListAdapter;

    public static String currentAlbumName;
    Album currentAlbum;

    int selectedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAlbums();
        this.currentAlbumName = (String) getIntent().getSerializableExtra("selectedAlbum", String.class);

        for (Album album: Album.albumsList) {
            if(album.getName().equals(currentAlbumName)) {
                currentAlbum = album;
            }
        }
        setContentView(R.layout.album_view);
        TextView albumViewHeader = findViewById(R.id.AlbumViewHeader);
        albumViewHeader.setText("Photos in Album: " + currentAlbum.getName());
        displayItems();
        Button btnAddPhoto = findViewById(R.id.addPhotoButton);
        btnAddPhoto.setOnClickListener(view -> addPhoto());
        Button btnDeletePhoto = findViewById(R.id.deletePhotoButton);
        btnDeletePhoto.setOnClickListener(view -> deletePhoto());
        Button btnModifyTags = findViewById(R.id.modifyTagsButton);
        btnModifyTags.setOnClickListener(view -> modifyTags());
        Button btnSlideShow = findViewById(R.id.slideshowButton);
        btnSlideShow.setOnClickListener(view -> slideshow());
        Button btnMovePhoto = findViewById(R.id.movePhotoButton);
        btnMovePhoto.setOnClickListener(view -> movePhoto());

        Button btnReturn = findViewById(R.id.returnButton);
        btnReturn.setOnClickListener(view -> goBack());

        photoListAdapter.setOnPhotoClickListener(this);
    }


    private void displayItems() {
        recyclerView = findViewById(R.id.photosRecylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        photoListAdapter = new PhotoListAdapter(this);
        recyclerView.setAdapter(photoListAdapter);
    }
    public void OnPhotoClick(int position) {
        this.selectedPosition = position;
    }

    // Method to add a photo to the album
    private void addPhoto() {
        // Create an intent to pick an image from the device
        Intent getFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getFileIntent.setType("image/*"); // Allow only images to be selected
        startActivityForResult(getFileIntent, MEDIA_IMAGE_REQUEST_CODE);
    }

    // Handle result of file selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == MEDIA_IMAGE_REQUEST_CODE) {
            // Get the selected file's URI
            Uri fileUri = data.getData();

            // Process the file URI as needed (e.g., save to the album)
            if (fileUri != null) {
                saveImageToAlbum(fileUri);
                photoListAdapter.notifyDataSetChanged();
            } else {
                Log.e("Error", "Selected file URI is null");
            }
        }
    }

    // Save the selected image to the current album
    private void saveImageToAlbum(Uri fileUri) {
        try {
            // Use ContentResolver to open an input stream for the selected file
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                // Save the image to a file in the app's internal storage directory
                File directory = getDir("images", Context.MODE_PRIVATE);

                File imageFile = new File(directory, "image_" + getFileName(fileUri));
                FileOutputStream outputStream = new FileOutputStream(imageFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close streams
                inputStream.close();
                outputStream.close();

                // Update the Photo object with the file path
                Photo newPhoto = new Photo(imageFile.getAbsolutePath());
                boolean duplicate = false;
                for (Photo photo : currentAlbum.getPhotos()) {
                    if(photo.getFilePath().equals(imageFile.getAbsolutePath())) {
                        duplicate = true;
                        break;
                    }
                }
                if(!duplicate) {
                    currentAlbum.addPhoto(newPhoto);
                    Log.i("Photo", "Photo added to album. Photos in Album: " + currentAlbum.getPhotos().toString());
                    updateAlbumsList();
                    photoListAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    Toast.makeText(this, "Photo added to album", Toast.LENGTH_SHORT).show();
                } else{
                    Log.e("E", "DUPLICATE PHOTO ADDED!!!!");
                    Toast.makeText(this, "Duplicate Photo, not added", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.e("Error", "Failed to open input stream for file");
            }
        } catch (IOException e) {
            Log.e("Error", "Failed to read file data: " + e.getMessage());
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void deletePhoto() {
        if(selectedPosition == -1) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }
        Photo selectedPhoto = currentAlbum.getPhotos().get(this.selectedPosition);
        for (Photo photo: currentAlbum.getPhotos()
             ) {
            if(photo.getFilePath().equals(selectedPhoto.getFilePath())) {
                currentAlbum.removePhoto(selectedPhoto);
                break;
            }
        }
        updateAlbumsList();
        photoListAdapter.notifyItemRemoved(this.selectedPosition);
        photoListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Photo deleted from album", Toast.LENGTH_SHORT).show();

    }

    private void modifyTags() {
        if(selectedPosition == -1) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modify Tags");

        // Set up the input
        final EditText personInput = new EditText(this);
        personInput.setInputType(InputType.TYPE_CLASS_TEXT);
        personInput.setHint("Enter person tag");

        final EditText locationInput = new EditText(this);
        locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
        locationInput.setHint("Enter location tag");

        // Set the layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(personInput);
        layout.addView(locationInput);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String personTag = personInput.getText().toString().trim();
            String locationTag = locationInput.getText().toString().trim();

            // Modify the tags for the selected photo
            if (selectedPosition != RecyclerView.NO_POSITION) {
                Photo selectedPhoto = currentAlbum.getPhotos().get(selectedPosition);
                selectedPhoto.setPersonTag(personTag);
                selectedPhoto.setLocationTag(locationTag);
                updateAlbumsList();
                photoListAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Tags modified", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void slideshow() {
        String selectedAlbum = currentAlbumName;
        Log.i("INFO", "Selected Album: " + selectedAlbum);
        Intent intent = new Intent(this, SlideshowActivity.class);
        intent.putExtra("selectedAlbum", selectedAlbum);
        startActivity(intent);
    }

    private void movePhoto() {
        if(selectedPosition == -1) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> albumNames = new ArrayList<>();
        for (Album album : Album.albumsList) {
            if (!album.getName().equals(currentAlbumName)) {
                albumNames.add(album.getName());
            }
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move to:");

    }

    private void goBack() {
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }

    private void loadAlbums() {
        try {
            FileInputStream fileIn = openFileInput("albums.ser");
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);
            Album.albumsList = (ArrayList<Album>) inputStream.readObject();
            for (Album album: Album.albumsList) {
                if(album.getName().equals(currentAlbumName))
                    currentAlbum = album;
            }
            inputStream.close();
            fileIn.close();
            Log.i("INFO", "Album list loaded successfully");
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateAlbumsList() {
        try {
            Log.i("SAVING/LOADING", "SAVING! Photos in Album: " + currentAlbum.getPhotos().toString());
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
            for (Album album: Album.albumsList) {
                if(album.getName().equals(currentAlbumName))
                currentAlbum = album;
            }
            Log.i("SAVING/LOADING", "LOADING! Photos in Album: " + currentAlbum.getPhotos().toString());
            inputStream.close();
            fileIn.close();
            Log.i("INFO", "Album list loaded successfully");
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
