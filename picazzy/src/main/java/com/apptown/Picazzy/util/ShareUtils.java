package com.apptown.Picazzy.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import magick.MagickException;

/**
 * Created by ${="Ashish"} on 28/1/17.
 */

public class ShareUtils {


    public static void shareWithFacebook(ImageView imageView, Context context) throws IOException {

        String fileName=createImageFile(context).getAbsolutePath();
        FileOutputStream  fileOutputStream = new FileOutputStream(fileName);
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bsResized);
        fileOutputStream.close();
         addImageToGallery(fileName, context);
        launch(fileName, "com.facebook.katana",context);

    }
    public static void shareWithInstagram(ImageView imageView, Context context, boolean isPortrait) throws IOException, MagickException {


        String  filename = createImageFile(context).getAbsolutePath();
        FileOutputStream  fileOutputStream = new FileOutputStream(filename);
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bmm1=InstaShare.share_Instagram(bmp,context,isPortrait);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmm1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bsResized);
        fileOutputStream.close();
         addImageToGallery(filename, context);
         launch(filename, "com.instagram.android",context);

    }
    public static void shareWithWhats(ImageView imageView, Context context) throws IOException {

        String fileName=createImageFile(context).getAbsolutePath();
        FileOutputStream  fileOutputStream = new FileOutputStream(fileName);
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bsResized);
        fileOutputStream.close();
        addImageToGallery(fileName, context);
        launch(fileName, "com.whatsapp",context);

    }
    public static void shareWithAll(ImageView imageView,Activity context){

        Bitmap icon = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        OutputStream outstream;
        try {
            outstream = context.getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivityForResult(Intent.createChooser(share, "Share Image"), 2);

    }
    public static void download(ImageView imageView,Context context) throws IOException {
        String  filename = createImageFile(context).getAbsolutePath();
        FileOutputStream  fileOutputStream = new FileOutputStream(filename);
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bsResized);
        fileOutputStream.close();

        addImageToGallery(filename, context);
        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();

    }
    public static void shareWithTwitter(ImageView imageView, Context context) throws IOException {

        String  filename = createImageFile(context).getAbsolutePath();
        FileOutputStream  fileOutputStream = new FileOutputStream(filename);
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bsResized);
        fileOutputStream.close();

        addImageToGallery(filename, context);
        launch(filename, "com.twitter.android",context);
    }



    private static String genrateRandone() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(12);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public  static  String SaveBitmap(ImageView imageView) throws IOException {

        File   folder = new File("/sdcard/Picazzy/");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String  filename = "/Picazzy/" + genrateRandone() + ".jpg";
        FileOutputStream  fileOutputStream = new FileOutputStream(filename);
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(bsResized);
        fileOutputStream.close();

        return Environment.getExternalStorageDirectory() + filename;
    }
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private static  void launch(String path, String strPackage,Context context) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(strPackage);
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(strPackage);
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("file://" + path));
                shareIntent.setType("image/jpeg");
                context.startActivity(shareIntent);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id="
                    + strPackage));
            context.startActivity(intent);
        }
    }
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir("picazzy");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
}
