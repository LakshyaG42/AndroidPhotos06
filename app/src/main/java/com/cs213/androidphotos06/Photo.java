package com.cs213.androidphotos06;

import android.util.Log;


import java.io.Serializable;
import java.util.HashMap;



public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filePath;
    private String personTag;

    private String locationTag;

    public Photo(String filePath) {
        this.filePath = filePath;
        this.personTag = null;
        this.locationTag = null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setPersonTag(String s){
        if(s.isEmpty()) {
            deletePersonTag();
        } else {
            this.personTag = s;
        }
    }

    public String getPerson() {
        return this.personTag;
    }
    public String getLocation() {
        return this.locationTag;
    }
    public void deletePersonTag() {
        this.personTag = null;
    }

    public void setLocationTag(String s){
        if(s.isEmpty()) {
            deleteLocationTag();
        } else {
            this.locationTag = s;
        }

    }

    public void deleteLocationTag() {
        this.locationTag = null;
    }


}