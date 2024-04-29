package com.cs213.androidphotos06;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView searchViewHeader;

    SearchAdapter searchAdapter;
    private String personTag;
    private String locationTag;
    private String operation;

    private ArrayList<Photo> searchResults;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchViewHeader = findViewById(R.id.SearchViewHeader);
        searchResults = new ArrayList<>();
        this.personTag = (String) getIntent().getSerializableExtra("personTag", String.class);
        this.locationTag = (String) getIntent().getSerializableExtra("locationTag", String.class);
        this.operation = (String) getIntent().getSerializableExtra("operation", String.class);
        Log.i("INFO", "Recieved from Popup:" + personTag + " and " + locationTag + " and " + operation);
        populateSearchArray();
        String searchQuery = null;
        if(!personTag.isEmpty() && !locationTag.isEmpty()) {
            searchQuery = "SearchType: " + operation + "\nPerson Tag: " + personTag + " Location Tag: " + locationTag;
        } else if(!personTag.isEmpty() ) {
            searchQuery = "SearchType: " + operation + "\nPerson Tag: " + personTag;
        } else if(!locationTag.isEmpty()) {
            searchQuery = "SearchType: " + operation + "\nLocation Tag: " + locationTag;
        } else {
            Toast.makeText(getApplicationContext(), "No Search Inputted", Toast.LENGTH_SHORT).show();
            goBack();
        }
        searchViewHeader.setText(searchQuery);
        displayResults(searchResults);
        Button btnGoBack = findViewById(R.id.goBackButton);
        btnGoBack.setOnClickListener(view -> goBack());

    }

    private void displayResults(ArrayList<Photo> searchResults) {
        recyclerView = findViewById(R.id.searchPhotosRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        searchAdapter = new SearchAdapter(this, searchResults);
        recyclerView.setAdapter(searchAdapter);
    }


    private void populateSearchArray() {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        switch(this.operation) {
            case "Conjunction":
                for (Album album: Album.albumsList) {
                    for (Photo photo: album.getPhotos()) {
                        if(photo.getPerson().toLowerCase().equals(personTag) && photo.getLocation().toLowerCase().equals(locationTag)) {
                            searchResults.add(photo);
                        }
                    }
                }
                break;
            case "Disjunction":
            case "Neither":
                for (Album album: Album.albumsList) {
                    for (Photo photo: album.getPhotos()) {
                        if(photo.getPerson().toLowerCase().equals(personTag) || photo.getLocation().toLowerCase().equals(locationTag)) {
                            searchResults.add(photo);
                        }
                    }
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), "No Operation Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void goBack() {
        getOnBackPressedDispatcher().onBackPressed();
        finish();
    }
}

