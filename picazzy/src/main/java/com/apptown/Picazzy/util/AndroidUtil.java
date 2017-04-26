package com.apptown.Picazzy.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ${="Ashish"} on 18/12/16.
 */

public class AndroidUtil {


    public static void show(String msg, Context context){

        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

    }

    public static boolean checkImageOrientation(Context context,Uri imageUri) {
        boolean isPortrait = false;
        try {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            Bitmap bmRotated = roate(bitmap, rotation);

            int imageWidth = bmRotated.getWidth();
            int imageHeight = bmRotated.getHeight();

            if (imageHeight >= imageWidth)
                isPortrait = true;
            else
                isPortrait = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  isPortrait;
    }
    private static Bitmap roate(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Drawable> getImage(Context context,String folderName) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] images =assetManager.list(folderName);
        ArrayList<String> listImages = new ArrayList<String>(Arrays.asList(images));
        List<Drawable> drawableArrayList=new ArrayList<>();
        for(int i=0;i<listImages.size();i++){
            InputStream inputstream=context.getAssets().open(folderName+"/"
                    +listImages.get(i));
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            drawableArrayList.add(drawable);
        }
        return drawableArrayList;
    }


    public static void printLog(String message){

    }
}
