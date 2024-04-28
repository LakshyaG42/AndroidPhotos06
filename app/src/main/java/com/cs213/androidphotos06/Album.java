package com.cs213.androidphotos06;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import android.util.Log;
/**
 * Album Model
 * @author Dhruv Shidhaye
 * @author Lakshya Gour
 */

public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    static ArrayList<Album> albumsList;

    static{
        albumsList = new ArrayList<>();
    }

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
        albumsList.add(this);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    // Additional methods for photo management

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        this.photos.remove(photo);
    }

    public void copyPhoto(String filename, Album album) {
        for (Photo photo : album.getPhotos()) {
            if (photo.getFilePath().equals(filename)) {
                Log.e("Album", "Photo already exists in the destination album");
                return;
            }
        }
        for (Photo photo : photos) {
            if (photo.getFilePath().equals(filename)) {
                album.addPhoto(photo);
                return;
            }
        }
    }
    public void movePhoto(String filename, Album albumTo) {
        for (Photo photo : albumTo.getPhotos()) {
            if (photo.getFilePath().equals(filename)) {
                Log.e("Album", "Photo already exists in the destination album");
                return;
            }
        }
        for (Photo photo : photos) {

            if (photo.getFilePath().equals(filename)) {
                albumTo.addPhoto(photo);
                photos.remove(photo);
                return;
            }
        }
    }

    //Android Static Methods to keep track of albums

    public static void deleteFromAlbumList(Album a) {
        albumsList.remove(a);
    }

}
