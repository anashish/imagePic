package com.apptown.Picazzy.model;

import android.net.Uri;

/**
 * Created by ${="Ashish"} on 17/12/16.
 */

public class PhotoModel {

   private Uri imageUri;
   private String albumId;
   private String albumName;
   private long coverId;
    private int count;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }

    public PhotoModel(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public PhotoModel() {
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
    public void increaseCount() {
        count++;
    }
}
