package com.cs213.androidphotos06;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SlideshowActivity extends AppCompatActivity {
    Album currentAlbum;
    String currentAlbumName;

    private int currentPosition = 0;
    private ImageView imageView;
    private TextView photoDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        imageView = findViewById(R.id.PhotoDisplay);
        photoDescription = findViewById(R.id.PhotoDescription);

        this.currentAlbumName = (String) getIntent().getSerializableExtra("selectedAlbum", String.class);
        for (Album album: Album.albumsList) {
            if(album.getName().equals(currentAlbumName)) {
                currentAlbum = album;
            }
        }

        displayPhoto(currentPosition);

        // Set up next button
        Button nextButton = findViewById(R.id.nextButton);

        // Set up previous button
        Button prevButton = findViewById(R.id.prevButton);
        updateButtons(nextButton, prevButton);

        nextButton.setOnClickListener(view -> {
            if (currentPosition < currentAlbum.getPhotos().size() - 1) {
                currentPosition++;
                updateButtons(nextButton, prevButton);
                displayPhoto(currentPosition);
            }
        });
        prevButton.setOnClickListener(view -> {
            if (currentPosition > 0) {
                currentPosition--;
                updateButtons(nextButton, prevButton);
                displayPhoto(currentPosition);
            }
        });

        Button btnReturn = findViewById(R.id.ReturnToAlbumButton);
        btnReturn.setOnClickListener(view -> goBack());
    }

    private void displayPhoto(int position) {
        if (position >= 0 && position < currentAlbum.getPhotos().size()) {
            Photo photo = currentAlbum.getPhotos().get(position);
            imageView.setImageURI(Uri.parse(photo.getFilePath()));
            photoDescription.setText("Filename: " + photo.getFilePath()
                    + "\nPerson: " + photo.getPerson()
                    + "\nLocation: " + photo.getLocation());
        }
    }

    private void updateButtons(Button nextButton, Button prevButton) {
        if(currentAlbum.getPhotos().size() > 2) {
            if(currentPosition > 0 && currentPosition < currentAlbum.getPhotos().size() - 1) {
                prevButton.setEnabled(true);
                nextButton.setEnabled(true);
            } else if(currentPosition == 0){
                prevButton.setEnabled(false);
                nextButton.setEnabled(true);
            } else if(currentPosition == currentAlbum.getPhotos().size() - 1){
                prevButton.setEnabled(true);
                nextButton.setEnabled(false);
            }
        } else {
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }
    private void goBack() {
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }
}
