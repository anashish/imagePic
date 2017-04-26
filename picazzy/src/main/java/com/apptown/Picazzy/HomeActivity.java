package com.apptown.Picazzy;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.apptown.Picazzy.adapter.GalleryAdapter;
import com.apptown.Picazzy.model.PhotoModel;
import com.apptown.Picazzy.util.AlbumController;
import com.apptown.Picazzy.util.AndroidUtil;
import com.apptown.Picazzy.util.PermissionUtils;
import com.apptown.Picazzy.util.SessionManager;
import com.apptown.Picazzy.util.ShareUtils;
import com.apptown.Picazzy.view.GridSpacingItemDecoration;
import com.apptown.Picazzy.view.ItemClickSupport;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.List;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;

public class HomeActivity extends AppCompatActivity {


    private static final int SELECT_FILE = 200;
    private static final int SELECT_CAMERA = 201;
    private RecyclerView rc_gallery;
    private ImageView mImageView;
    private GalleryAdapter mGalleryAdapter;

    private AlbumController mAlbumController;
    private Button mButtonAlbumName;
    private PopupMenu popupMenu;
    private Uri mImageUr;
    private GridLayoutManager gridLayoutManager;
    private Uri cameraURI;
    private int mLastImageSelectedPostion =-1;
    private boolean isPortrait=false;
    public static final int CROP_START_REQ=1000;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAlbumController = new AlbumController(this);
        mButtonAlbumName = (Button) findViewById(R.id.button_home_album_name);
        ImageView go = (ImageView) findViewById(R.id.img_go);
        mButtonAlbumName.setOnClickListener(onClickListener);
        go.setOnClickListener(onClickListener);
        mImageView = (ImageView) findViewById(R.id.image_src);
        rc_gallery = (RecyclerView) findViewById(R.id.rc_gallery);
        gridLayoutManager = new GridLayoutManager(this, 4);
        rc_gallery.setLayoutManager(gridLayoutManager);
        rc_gallery.addItemDecoration(new GridSpacingItemDecoration(gridLayoutManager.getSpanCount(), 1, false));

        if (!PermissionUtils.getInstance().hasPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionUtils.getInstance().needPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE, SELECT_FILE, "Select File!");
            // Now you will get callback in onRequestPermissionsResult() method for further code
        } else {
            getImageListFromGallery();
            setRecyclerView();

        }


        ItemClickSupport.addTo(rc_gallery).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                mLastImageSelectedPostion =position;
                mGalleryAdapter.notifyDateChanged(position);
                PhotoModel imageInfo = mGalleryAdapter.getItem(position);
                mImageUr = imageInfo.getImageUri();

                gridLayoutManager.scrollToPosition(0);
            }
        });

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);


    }


    private void showFolderDialog() {


        final String[] gallaryName=new String[mAlbumController.getAlbums().size()];

        for(int i=0;i<gallaryName.length;i++){

            gallaryName[i]=mAlbumController.getAlbums().get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
        builder.setItems(gallaryName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mButtonAlbumName.setText(gallaryName[i]);
                try {
                    List<PhotoModel> photo = mAlbumController.getAlbum(gallaryName[i]);
                    mImageUr = photo.get(0).getImageUri();
                    Glide.with(HomeActivity.this)
                            .load(photo.get(0).getImageUri())
                            .thumbnail(0.1f)
                            .dontAnimate()
                            .centerCrop()
                            .placeholder(R.drawable.img_logo_white)
                            .error(R.drawable.img_logo_white)
                            .into(mImageView);
                    mGalleryAdapter = null;
                    mGalleryAdapter = new GalleryAdapter(HomeActivity.this, photo);
                    rc_gallery.setAdapter(mGalleryAdapter);
                } catch (IndexOutOfBoundsException e) {
                    setRecyclerView();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();









    }
    private static final int REQUEST_CAMERA = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case SELECT_FILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImageListFromGallery();
                    setRecyclerView();

                } else {
                    AndroidUtil.show(getString(R.string.gallery), this);
                }
                break;
            case REQUEST_SELECT_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameeraRequest();
                } else {
                    AndroidUtil.show( "You do not have permission to open camera",this);
                }
                break;
            case REQUEST_SAVE_IMAGE_CAMERA:
                System.out.println("here in REQUEST_SAVE_IMAGE_CAMERA.....................");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!PermissionUtils.getInstance().hasPermission(this, android.Manifest.permission.CAMERA)) {
                        PermissionUtils.getInstance().needPermission(this, android.Manifest.permission.CAMERA, REQUEST_CAMERA, "Open Camera!");
                        Log.e("@@@@@@@@@@@@@@2", "allowed");
                    } else {
                        Log.e("@@@@@@@@@@@@@@2", "not allowed");
                        startCameeraRequest();
                        //Your code related to permission
                    }
                } else {
                    AndroidUtil.show( "You do not have permission to save image",this);

                }

        }


    }


    private void setRecyclerView() {


        try {
            mImageUr = getImageListFromGallery().get(0).getImageUri();
            Glide.with(this)
                    .load(getImageListFromGallery().get(0).getImageUri())
                    .thumbnail(0.1f)
                    .dontAnimate()
                    .centerCrop()
                    .placeholder(R.drawable.img_logo_white)
                    .error(R.drawable.img_logo_white)
                    .into(mImageView);

            mGalleryAdapter = new GalleryAdapter(this, getImageListFromGallery());
            rc_gallery.setAdapter(mGalleryAdapter);

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return mGalleryAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
                }
            });

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }



    }

    private List<PhotoModel> getImageListFromGallery() {
        return mAlbumController.getCurrent();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_home_album_name:
                    //showFolderDialog();
                    break;
                case R.id.img_go:
                    startCropActivity();

                    break;
            }

        }
    };

    protected void startCropActivity() {
        UCrop.Options options=new UCrop.Options();
        options.setStatusBarColor(getResources().getColor(R.color.transparent_color_code));
        options.setToolbarColor(getResources().getColor(R.color.app_theme_color));
        options.setToolbarTitle("Crop Photo");
        options.setToolbarWidgetColor(ContextCompat.getColor(this,R.color.black));
        String destinationFileName = "picazzy" + SystemClock.currentThreadTimeMillis();
        options.setHideBottomControls(true);
        options.setDimmedLayerColor(ContextCompat.getColor(this, R.color.trans));
        isPortrait= AndroidUtil.checkImageOrientation(HomeActivity.this,mImageUr);
        SessionManager sessionManager=new SessionManager(HomeActivity.this);
        sessionManager.savePortrait(isPortrait);

        if(isPortrait){
            UCrop.of(mImageUr, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
                    .withAspectRatio(3, 4)
                    .withOptions(options)
                    .withMaxResultSize(maxWidth, maxHeight)
                    .start(HomeActivity.this,UCrop.REQUEST_CROP);
        }else{
            UCrop.of(mImageUr, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
                    .withAspectRatio(4, 3)
                    .withOptions(options)
                    .withMaxResultSize(maxWidth, maxHeight)
                    .start(HomeActivity.this,UCrop.REQUEST_CROP);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            Intent intent = new Intent(HomeActivity.this, EditorActivity.class);
            intent.putExtra("mUri", resultUri.toString());
            intent.putExtra("orignalImage",resultUri);
            startActivityForResult(intent,CROP_START_REQ);
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
        else if (requestCode == CROP_START_REQ && resultCode==RESULT_CANCELED) {

            startCropActivity();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CAMERA) {

            if (mCurrentPhotoPath != null) {
                File f = new File(mCurrentPhotoPath);
                mImageUr = Uri.fromFile(f);
                startCropActivity();
            }


        }
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    private FirebaseAnalytics firebaseAnalytics;

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    private static final int REQUEST_SAVE_IMAGE_CAMERA = 4;
    private static final int REQUEST_SELECT_CAMERA = 2;

    public void openCamera(View v) {

        if (!PermissionUtils.getInstance().hasPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionUtils.getInstance().needPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_SAVE_IMAGE_CAMERA, "Save Cropped Image");
        } else {
            if (!PermissionUtils.getInstance().hasPermission(this, android.Manifest.permission.CAMERA)) {
                PermissionUtils.getInstance().needPermission(this, android.Manifest.permission.CAMERA, REQUEST_SELECT_CAMERA, "Open Camera!");
            } else {
                startCameeraRequest();
            }
        }


    }


    private void startCameeraRequest() {

        Bundle bundle = new Bundle();
        bundle.putString("Source", "Camera");
        firebaseAnalytics.logEvent("SourceOfImage", bundle);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ShareUtils.createImageFile(HomeActivity.this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUr = FileProvider.getUriForFile(this,
                        "com.apptown.Picazzy",
                        photoFile);

                AndroidUtil.printLog("URI  "+mImageUr.toString());


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUr);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mLastImageSelectedPostion!=-1){

            gridLayoutManager.scrollToPosition(mLastImageSelectedPostion);
        }
    }

    private static String mCurrentPhotoPath;


    public void openFolder(View v){
        showFolderDialog();
    }
}

