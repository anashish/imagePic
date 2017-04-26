// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.apptown.Picazzy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = null;
    private String mCurrentPhotoPath;
    private ImageButton btn_photoAlbum, btn_Camera;
    private ImageView imgVw_logo;
    private Bitmap bitmap;
    private Bitmap bmp;
    private boolean isCropViewShow = false;
    private String picturePath = null;

    private Uri mCropImageUri;
    private Uri orignalImageUri;
    private final static int ACTIVITY_TAKE_PHOTO = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 107;
    String dir = null;
    private boolean isPortrait = false;
    FragmentTransaction ft;
    FragmentManager fm;
    File file;
    private Uri fileUri;

    private FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Localytics.autoIntegrate(getApplication());

        setContentView(R.layout.activity_main);
        // Obtain the Firebase Analytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);


        btn_photoAlbum = (ImageButton) findViewById(R.id.btn_PhotoAlbum);
        btn_Camera = (ImageButton) findViewById(R.id.btn_Camera);

        Intent intent = getIntent();
// Get the action of the intent
        String action = intent.getAction();
// Get the type of intent (Text or Image)
        String type = intent.getType();
// When Intent's action is 'ACTION+SEND' and Tyoe is not null
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) { // When type is 'image/*'
                handleSendImage(intent); // Handle single image being sent
            }
        }

        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //Localytics.onNewIntent(this, intent);
    }

    private void handleSendImage(Intent intent) {
        // Get the image URI from intent
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // When image URI is not null
        if (imageUri != null) {
            startCropImageActivity(imageUri);
            // Update UI to reflect image being shared
            //  view_news.setImageURI(imageUri);
            // news.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Error occured, URI is invalid", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isCropViewShow)
            return;


    }

    /**
     * Start pick image from Gallery activity with chooser.
     */
    public void openGallery(View view) {
        try {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("Source", "Gallery");
            //Localytics.tagEvent("SourceOfImage", attributes);

            Bundle bundle = new Bundle();
            bundle.putString("Source", "Gallery");
            firebaseAnalytics.logEvent("SourceOfImage", bundle);

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    public void openCamera(View v) {
        try {


            if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                marshMallowPermission.requestPermissionForExternalStorage();
            } else {

                Map<String, String> attributes = new HashMap<String, String>();
                attributes.put("Source", "Camera");
               // Localytics.tagEvent("SourceOfImage", attributes);

                Bundle bundle = new Bundle();
                bundle.putString("Source", "Camera");
                firebaseAnalytics.logEvent("SourceOfImage", bundle);


                Uri outputFileUri = CropImage.getCaptureImageOutputUri(this.getApplicationContext());
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (outputFileUri != null) {
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                }

                startActivityForResult(captureIntent, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                // CropImage.startPickImageActivity(this);

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong Please try again", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // handle result of pick image chooser
            if (requestCode == ACTIVITY_TAKE_PHOTO) {
                Log.d(TAG, "onActivityResult: hello");

            }
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);
                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                boolean requirePermissions = true;
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requirePermissions = true;
                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropImageActivity(imageUri);
                }
                return;

            }

            if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE)
            {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);

                File folder = new File("/sdcard/Picazzy/");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                try {
                    String filename = "/Picazzy/" + random() + ".jpg";
                    FileOutputStream fileOutputStream = new FileOutputStream(String.format("/sdcard/" + filename,
                            System.currentTimeMillis()));

                    ExifInterface exif = new ExifInterface(imageUri.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    // Here we Resize the Image ...
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Bitmap bmRotated = rotateBitmap(bmp, rotation);


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bsResized = byteArrayOutputStream.toByteArray();
                    fileOutputStream.write(bsResized);
                    String str = new String(bsResized);
                    fileOutputStream.close();

                    String mediaPath = Environment.getExternalStorageDirectory() + filename;

                    addImageToGallery(mediaPath, MainActivity.this);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startCropImageActivity(imageUri);
            }
            // handle result of CropImageActivity
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri mUri = result.getUri();
                    checImageOrientation(mUri);
                    Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                    intent.putExtra("mUri", mUri.toString());
                    intent.putExtra("orignalImage", orignalImageUri);
                    intent.putExtra("isPortrait", isPortrait);
                    startActivity(intent);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
                return;
            }
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),"Something went wrong Please try again",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return;
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        checImageOrientation(imageUri);
        orignalImageUri = imageUri;
        isCropViewShow = true;
        if (isPortrait) {
            CropImage.activity(imageUri)
                    .setFixAspectRatio(true).setAspectRatio(3, 4).setMaxZoom(2).setMinCropWindowSize(150, 200)
                    .start(this);
        } else {
            CropImage.activity(imageUri)
                    .setFixAspectRatio(true).setAspectRatio(4, 3).setMaxZoom(2).setMinCropWindowSize(200, 150)
                    .start(this);
        }


    }

    private void checImageOrientation(Uri imageUri) {

        try {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            Bitmap bmRotated = rotateBitmap(bitmap, rotation);

            int imageWidth = bmRotated.getWidth();
            int imageHeight = bmRotated.getHeight();
            if (imageHeight >= imageWidth)
                isPortrait = true;
            else
                isPortrait = false;
        } catch (Exception e) {
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

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

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String random() {
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
}



