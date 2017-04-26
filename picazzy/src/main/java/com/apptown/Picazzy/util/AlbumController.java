package com.apptown.Picazzy.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.apptown.Picazzy.model.AlbumModel;
import com.apptown.Picazzy.model.PhotoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${="Ashish"} on 18/12/16.
 */
public class AlbumController {

    private ContentResolver resolver;

    public AlbumController(Context context) {
        resolver = context.getContentResolver();
    }

    public List<PhotoModel> getCurrent() {
       List<PhotoModel> imageInfoList=new ArrayList<>();
        Cursor imageCursor = null;
        try {
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
            imageCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy);
            ArrayList<String> ids = new ArrayList<String>();
            if (imageCursor != null) {
                while (imageCursor.moveToNext() ) {
                    String imageLocation = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File imageFile = new File(imageLocation);
                    PhotoModel photoModel=new PhotoModel();
                    photoModel.setImageUri(Uri.fromFile(imageFile));
                    imageInfoList.add(photoModel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
        return imageInfoList;
    }

    public List<AlbumModel> getAlbums() {
        List<AlbumModel> albums = new ArrayList<AlbumModel>();
        Map<String, AlbumModel> map = new HashMap<String, AlbumModel>();


        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
                ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE,MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<AlbumModel>();
        cursor.moveToLast();
        AlbumModel current = new AlbumModel("Gallery", 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // "最近照片"相册
        albums.add(current);
        do {
            if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10)
                continue;

            current.increaseCount();
            String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
            if (map.keySet().contains(name))
                map.get(name).increaseCount();
            else {
                AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
                map.put(name, album);
                albums.add(album);
            }
        } while (cursor.moveToPrevious());
        return albums;
    }

    public List<PhotoModel> getAlbum(String name) {
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
                        ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE ,MediaStore.Images.ImageColumns.ORIENTATION}, "bucket_display_name = ?",
                new String[] { name }, ImageColumns.DATE_ADDED);
        if (cursor == null || !cursor.moveToNext())
            return new ArrayList<PhotoModel>();
        List<PhotoModel> photos = new ArrayList<PhotoModel>();
        cursor.moveToLast();
        do {
            if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
                PhotoModel photoModel = new PhotoModel();
                String imageLocation = cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
                File imageFile = new File(imageLocation);
                photoModel.setImageUri(Uri.fromFile(imageFile));
                photos.add(photoModel);
            }
        } while (cursor.moveToPrevious());
        return photos;
    }
}
