package com.apptown.Picazzy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ParseException;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apptown.Picazzy.adapter.EventAdapter;
import com.apptown.Picazzy.model.EventInfo;
import com.apptown.Picazzy.model.PicazzyEvent;
import com.apptown.Picazzy.util.AndroidUtil;
import com.apptown.Picazzy.util.ApiClient;
import com.apptown.Picazzy.util.ApiInterface;
import com.apptown.Picazzy.util.BitmapProcessing;
import com.apptown.Picazzy.util.NewFilter;
import com.apptown.Picazzy.util.SessionManager;
import com.apptown.Picazzy.util.ShareUtils;
import com.apptown.Picazzy.view.EditEnum;
import com.apptown.Picazzy.view.ItemClickSupport;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import magick.CompositeOperator;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;


//import com.example.makemagick.ImageInfo;
//import com.example.makemagick.Rectangle;

/**
 * Created by Picazzy on 5/14/16.
 */
public class EditorActivity extends AppCompatActivity
{


    /**
     * Static List of All the subcategory resources
     */
    String filename;
    private HorizontalListView hlv;
    private Adapter hlvAdapter;
    ArrayList<String> title;
    ArrayList<String> effect_code;
    private ImageView imageView;
    private ImageView imv_back_button;
    private ImageView imv_share_button, img_save_gallery;
    private ImageView tapOnRecrop, instagram_share, facebook_share;
    private Bitmap myBitmap;
    private String Effect_Code;
    private String Effect_Name;

    private String path = null;
    private Uri originalImageURI;
    private boolean isPortrait = false;
    private boolean isWaterMarkOn = true;

    Bitmap decodedByte;
    FileOutputStream fileOutputStream = null;
    private boolean isServiceActive = false;
    LoaderFragment trans;
    MediaScannerConnection msc;
    ProgressDialog progressBar;
    private static FragmentManager fragmentManager;
    private FirebaseAnalytics firebaseAnalytics;
    private ArrayList<String> newFilterTitle;
    private ArrayList<String> nEffectCode;
    private boolean isArtisticFilter=true;
    private List<Drawable> mArtisticImagePathList;
    private List<Drawable> mFilterImagePathList;
    private  NewFilter mNewFilter;
    private PhotoViewAttacher mAttacher;
    private EventAdapter mEventAdapter;
    private boolean isShareContentVisibile=true;
    private SessionManager mSessionManager;

    private Toast toast;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        FacebookSdk.sdkInitialize(this);
        shareDialog = new ShareDialog(this);
        callbackManager=  CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    changeStatusBarColor();
        mSessionManager=new SessionManager(this);
        isPortrait =mSessionManager.isPortrait();
        mNewFilter=new NewFilter(EditorActivity.this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            mArtisticImagePathList= AndroidUtil.getImage(this,"thumbnailsartistic");
            mFilterImagePathList= AndroidUtil.getImage(this,"thumbnailsFilters");

        } catch (IOException e) {
            e.printStackTrace();
        }

        title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Artistic_effects)));
        effect_code = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.artistic_filter_code)));
        newFilterTitle = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.filter_name)));
        nEffectCode = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.filter_code)));
        fragmentManager = this.getSupportFragmentManager();

        init();
        hlv = (HorizontalListView) findViewById(R.id.hlv);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //  prefs.edit().putBoolean("isEditor", true).commit();
        // effect_lsyer= (ImageView) findViewById(R.id.filter);
        hlvAdapter = new Adapter(EditorActivity.this, title, mArtisticImagePathList);
        hlv.setAdapter(hlvAdapter);

        hlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mShareLayout.setVisibility(View.INVISIBLE);


                if(isArtisticFilter){
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("Title", title.get(position));
                    editor.apply();

                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View v = parent.getChildAt(i);
                        Adapter.ViewHolder viewHolder1;
                        viewHolder1 = (Adapter.ViewHolder) v.getTag();
                        viewHolder1.image_layer.setVisibility(View.INVISIBLE);
                    }

                    Adapter.ViewHolder viewHolder;
                    viewHolder = (Adapter.ViewHolder) view.getTag();
                    viewHolder.image_layer.setVisibility(View.VISIBLE);
                    //Toast.makeText(EditorActivity.this, " " + title.get(position).toString()+effect_code.get(position).toString(), Toast.LENGTH_SHORT).show();
                    Effect_Code = effect_code.get(position).toString();
                    Effect_Name = title.get(position).toString();
                }else{
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("Title", newFilterTitle.get(position));
                    editor.apply();

                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View v = parent.getChildAt(i);
                        Adapter.ViewHolder viewHolder1;
                        viewHolder1 = (Adapter.ViewHolder) v.getTag();
                        viewHolder1.image_layer.setVisibility(View.INVISIBLE);
                    }

                    Adapter.ViewHolder viewHolder;
                    viewHolder = (Adapter.ViewHolder) view.getTag();
                    viewHolder.image_layer.setVisibility(View.VISIBLE);
                    //Toast.makeText(EditorActivity.this, " " + title.get(position).toString()+effect_code.get(position).toString(), Toast.LENGTH_SHORT).show();
                    Effect_Code = nEffectCode.get(position).toString();
                    Effect_Name = newFilterTitle.get(position).toString();
                }



                EditorActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        fragment();
                    }
                });
            }
        });





        path = getIntent().getStringExtra("mUri");
        Uri myUri = Uri.parse(getIntent().getStringExtra("mUri"));
        handleShare(myUri);
        File imgFile = new File(myUri.getPath());

        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            mAttacher = new PhotoViewAttacher(imageView);

        }
        initEditControl();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        setupTabLayout();

        mShareLayout=(LinearLayout)findViewById(R.id.layout_share);
        mShareLayout.setVisibility(View.INVISIBLE);
        ImageView share=(ImageView)findViewById(R.id.img_share);
        share.setOnClickListener(onClickListener);

    }


    private  LinearLayout mShareLayout;
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.img_share:
                    if(isShareContentVisibile){
                        mShareLayout.setVisibility(View.VISIBLE);
                        isShareContentVisibile=false;
                    }else{
                        mShareLayout.setVisibility(View.GONE);
                        isShareContentVisibile=true;
                    }

                    break;
            }
        }
    };

    private void setupTabLayout() {


        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabs);
        final int[] iconArray={R.drawable.filter_one_selector,
                R.drawable.filter_two_selector,R.drawable.filter_edit_selector,R.drawable.filter_event_selector};

        for(int i=0;i<iconArray.length;i++){
            tabLayout.addTab(tabLayout.newTab());
            View custom1 = LayoutInflater.from(this).inflate(R.layout.tab_row, null);
            ((ImageView) custom1.findViewById(R.id.tab_image_row)).setImageResource(iconArray[i]);
            TabLayout.Tab customTab1 = tabLayout.getTabAt(i);
            customTab1.setCustomView(custom1);
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                switch (tab.getPosition()){
                    case 0:
                        hlvAdapter = new Adapter(EditorActivity.this, title, mArtisticImagePathList);
                        hlv.setAdapter(hlvAdapter);
                        isArtisticFilter=true;
                        hlv.setVisibility(View.VISIBLE);
                        mLinearEditLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        showToastMessage(getResources().getString(R.string.toast_artistic_msg));
                        break;
                    case 1:
                        hlvAdapter = new Adapter(EditorActivity.this, newFilterTitle, mFilterImagePathList);
                        hlv.setAdapter(hlvAdapter);
                        isArtisticFilter=false;
                        hlv.setVisibility(View.VISIBLE);
                        mLinearEditLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        showToastMessage(getResources().getString(R.string.toast_filter_msg));
                        break;
                    case 2:
                        hlv.setVisibility(View.GONE);
                        mLinearEditLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        showToastMessage(getResources().getString(R.string.toast_edit_msg));
                        break;
                    case 3:
                        hlv.setVisibility(View.GONE);
                        mLinearEditLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        showToastMessage(getResources().getString(R.string.toast_event_msg));
                        break;
                }
               /* mViewPager.setCurrentItem(tab.getPosition());
                changeBackGround(tab.getPosition());
                mCurrentPosition=tab.getPosition();*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showToastMessage(String message) {

            LayoutInflater inflater = LayoutInflater.from(EditorActivity.this);
            View layout = inflater.inflate(R.layout.layout_dynamic_toast, null);
            TextView textV = (TextView) layout.findViewById(R.id.text_msg);
            textV.setText(message);
        if (toast != null) {
            toast.cancel();
            toast = new Toast(EditorActivity.this);
        } else {
            toast = new Toast(EditorActivity.this);
        }

            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

    }


    void init() {
        img_save_gallery = (ImageView) findViewById(R.id.save_to_gallery);
        imv_share_button = (ImageView) findViewById(R.id.share_button);
        imv_back_button = (ImageView) findViewById(R.id.back_button);
        imageView = (ImageView) findViewById(R.id.edit_image);
        instagram_share = (ImageView) findViewById(R.id.instagram);
        facebook_share = (ImageView) findViewById(R.id.facebook);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void fragment() {
        FragmentTransaction fragmentTransaction;
        // fragmentManager=getSupportFragmentManager();
        trans = new LoaderFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, trans);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isArtisticFilter){
                    callApplyEffact();
                }else{
                    try {
                        callApplyNewAffect();
                    } catch (MagickException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, 2000);

    }

    private void callApplyNewAffect() throws MagickException {

        isWaterMarkOn = true;
        Bitmap scaledBitmap;
        Bitmap imgOut = null;
        if (isPortrait) {
            scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
            Bundle bundle = new Bundle();
            bundle.putString("Dimension", "Portrait");
            bundle.putString("EffectName", Effect_Name);
            firebaseAnalytics.logEvent("AppliedEffect", bundle);
            firebaseAnalytics.logEvent(Effect_Name, bundle);
        }
        else {
            scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
            Bundle bundle = new Bundle();
            bundle.putString("Dimension", "Landscape");
            bundle.putString("EffectName", Effect_Name);
            firebaseAnalytics.logEvent("AppliedEffect", bundle);
            firebaseAnalytics.logEvent(Effect_Name, bundle);
        }

        if(isPortrait){
           switch (Effect_Code){
               case "PT40":
                   imgOut=mNewFilter.apply_PT40(scaledBitmap);
                   break;
               case "PF11":
                   imgOut=mNewFilter.apply_PF11(scaledBitmap);
                   break;
               case "PF12":
                   imgOut=mNewFilter.apply_PF12(scaledBitmap);
                   break;
               case "PF1":
                   imgOut=mNewFilter.apply_PF1(scaledBitmap);
                   break;
               case "PF7":
                   imgOut=mNewFilter.apply_PF7(scaledBitmap);
                   break;
               case "PT17":
                   imgOut=apply_PT17(scaledBitmap);
                   break;
               case "PF8":
                   imgOut=mNewFilter.apply_PF8(scaledBitmap);
                   break;
               case "PF10":
                   imgOut=mNewFilter.apply_PF10(scaledBitmap);
                   break;
               case "PF3":
                   imgOut=mNewFilter.apply_PF3(scaledBitmap);
                   break;
               case "PT38":
                   imgOut=mNewFilter.apply_PT38(scaledBitmap);
                   break;
               case "PT33":
                   imgOut=apply_PT33(scaledBitmap);
                   break;
               case "PT35":
                   imgOut=apply_PT35(scaledBitmap);
                   break;
               case "PF22":
                   imgOut=mNewFilter.apply_PF22(scaledBitmap);
                   break;
               case "PF16":
                   imgOut=mNewFilter.apply_PF16(scaledBitmap);
                   break;
               case "PF17":
                   imgOut=mNewFilter.apply_PF17(scaledBitmap);
                   break;
               case "PF19":
                   imgOut=mNewFilter.apply_PF19(scaledBitmap);
                   break;
               case "PT5-1":
                   imgOut=apply_PT5_1(scaledBitmap);
                   break;
               case "PT6":
                   imgOut=apply_PT6(scaledBitmap);
                   break;
               case "PF4":
                   imgOut=mNewFilter.apply_PF4(scaledBitmap);
                   break;
               case "PF5":
                   imgOut=mNewFilter.apply_PF5(scaledBitmap);
                   break;
               case "PF20":
                   imgOut=mNewFilter.apply_PF20(scaledBitmap);
                   break;
               case "PS08":
                   imgOut=apply_PS08(scaledBitmap);
                   break;
               case "PS07":
                   imgOut=apply_PS07(scaledBitmap);
                   break;
               case "PF21":
                   imgOut=mNewFilter.apply_PF21(scaledBitmap);
                   break;
               case "PF2":
                   imgOut=mNewFilter.apply_PF2(scaledBitmap);
                   break;
               case "PF15":
                   imgOut=mNewFilter.apply_PF15(scaledBitmap);
                   break;
               case "PF14":
                   imgOut=mNewFilter.apply_PF14(scaledBitmap);
                   break;
               case "PF13":
                   imgOut=mNewFilter.apply_PF13(scaledBitmap);
                   break;

           }
        }else{
            switch (Effect_Code){
                case "PT40":
                    imgOut=mNewFilter.apply_LPT40(scaledBitmap);
                    break;
                case "PF11":
                    imgOut=mNewFilter.apply_LPF11(scaledBitmap);
                    break;
                case "PF12":
                    imgOut=mNewFilter.apply_LPF12(scaledBitmap);
                    break;
                case "PF1":
                    imgOut=mNewFilter.apply_LPF1(scaledBitmap);
                    break;
                case "PF7":
                    imgOut=mNewFilter.apply_LPF7(scaledBitmap);
                    break;
                case "PT17":
                    imgOut=apply_LPT17(scaledBitmap);
                    break;
                case "PF8":
                    imgOut=mNewFilter.apply_LPF8(scaledBitmap);
                    break;
                case "PF10":
                    imgOut=mNewFilter.apply_LPF10(scaledBitmap);
                    break;
                case "PF3":
                    imgOut=mNewFilter.apply_LPF3(scaledBitmap);
                    break;
                case "PT38":
                    imgOut=mNewFilter.apply_LPT38(scaledBitmap);
                    break;
                case "PT33":
                    imgOut=apply_LPT33(scaledBitmap);
                    break;
                case "PT35":
                    imgOut=apply_LPT35(scaledBitmap);
                    break;
                case "PF22":
                    imgOut=mNewFilter.apply_LPF22(scaledBitmap);
                    break;
                case "PF16":
                    imgOut=mNewFilter.apply_LPF16(scaledBitmap);
                    break;
                case "PF17":
                    imgOut=mNewFilter.apply_LPF17(scaledBitmap);
                    break;
                case "PF19":
                    imgOut=mNewFilter.apply_LPF19(scaledBitmap);
                    break;
                case "PT5-1":
                    imgOut=apply_LPT5_1(scaledBitmap);
                    break;
                case "PT6":
                    imgOut=apply_LPT6(scaledBitmap);
                    break;
                case "PF4":
                    imgOut=mNewFilter.apply_LPF4(scaledBitmap);
                    break;
                case "PF5":
                    imgOut=mNewFilter.apply_LPF5(scaledBitmap);
                    break;
                case "PF20":
                    imgOut=mNewFilter.apply_LPF20(scaledBitmap);
                    break;
                case "PS08":
                    imgOut=apply_LPS08(scaledBitmap);
                    break;
                case "PS07":
                    imgOut=apply_LPS07(scaledBitmap);
                    break;
                case "PF21":
                    imgOut=mNewFilter.apply_LPF21(scaledBitmap);
                    break;
                case "PF2":
                    imgOut=mNewFilter.apply_LPF2(scaledBitmap);
                    break;
                case "PF15":
                    imgOut=mNewFilter.apply_LPF15(scaledBitmap);
                    break;
                case "PF14":
                    imgOut=mNewFilter.apply_LPF14(scaledBitmap);
                    break;
                case "PF13":
                    imgOut=mNewFilter.apply_LPF13(scaledBitmap);
                    break;

            }
        }

       displayOutPutImage(imgOut);
    }


    /**
     * This method  is responsible to display final image after apply filter
     * @param imgOut
     */
    private void displayOutPutImage(Bitmap imgOut) {

        AndroidUtil.printLog("displayOutPutImage isPortrait "+isPortrait);
        if (isPortrait) {
            Bitmap finalOutput = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
            Drawable myDrawable = getResources().getDrawable(R.drawable.watermarkportrait);
            Bitmap watermark1 = ((BitmapDrawable) myDrawable).getBitmap();
            Bitmap watermark = Bitmap.createScaledBitmap(watermark1, 450, 600, false);
            Canvas cc = new Canvas();
            cc.setBitmap(finalOutput);
            cc.drawBitmap(imgOut, 0, 0, null);
            if (isWaterMarkOn) {
                cc.drawBitmap(watermark, 0, 0, null);
            }
            imageView.setImageBitmap(finalOutput);
            mAttacher.update();
        } else {
            Bitmap finalOutput = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
            Drawable myDrawable = getResources().getDrawable(R.drawable.watermarklandscape);
            Bitmap watermark1 = ((BitmapDrawable) myDrawable).getBitmap();
            Bitmap watermark = Bitmap.createScaledBitmap(watermark1, 600, 450, false);
            Canvas cc = new Canvas();
            cc.setBitmap(finalOutput);
            cc.drawBitmap(imgOut, 0, 0, null);
            if (isWaterMarkOn) {
                cc.drawBitmap(watermark, 0, 0, null);
            }
            imageView.setImageBitmap(finalOutput);
            mAttacher.update();
        }
        //  imageView.setImageBitmap(imgOut);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoader();
            }
        }, 500);
    }

    public void callApplyEffact() {
        try {
            applyEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Here we Code for get Orignal Image On Crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                originalImageURI = result.getUri();
                File imgFile = new File(originalImageURI.getPath());

                if (imgFile.exists()) {
                    myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    mAttacher.update();

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 2) {


        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //////*******NEED TO BE UNCOMMENT
    public void applyEffect() throws MagickException, java.text.ParseException {

        isWaterMarkOn = true;
        Bitmap scaledBitmap;
        Bitmap imgOut = null;
        if (isPortrait) {
            scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
            Bundle bundle = new Bundle();
            bundle.putString("Dimension", "Portrait");
            bundle.putString("EffectName", Effect_Name);
            firebaseAnalytics.logEvent("AppliedEffect", bundle);
            firebaseAnalytics.logEvent(Effect_Code, bundle);
        }
        else {
            scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
            Bundle bundle = new Bundle();
            bundle.putString("Dimension", "Landscape");
            bundle.putString("EffectName", Effect_Name);
            firebaseAnalytics.logEvent("AppliedEffect", bundle);
            firebaseAnalytics.logEvent(Effect_Code, bundle);
        }

        if (isPortrait) {
            switch (Effect_Code) {
                case "PT41":
                    imgOut = mNewFilter.apply_PT41(scaledBitmap);
                    break;
                case "PS02":
                    imgOut = apply_PS02(scaledBitmap);
                    break;
                case "PS03":
                    imgOut = apply_PS03(scaledBitmap);
                    break;
                case "PS07":
                    imgOut = apply_PS07(scaledBitmap);
                    break;
                case "PS08":
                    imgOut = apply_PS08(scaledBitmap);
                    break;
                case "PT1":
                    imgOut = apply_PT1(scaledBitmap);
                    break;
                case "PT2-1":
                    imgOut = apply_PT2_1(scaledBitmap);
                    break;
                case "PT2-2":
                    imgOut = apply_PT2_2(scaledBitmap);
                    break;
                case "PT2-3":
                    imgOut = apply_PT2_3(scaledBitmap);
                    break;
                case "PT2-4":
                    imgOut = apply_PT2_4(scaledBitmap);
                    break;
                case "PT2-5":
                    imgOut = apply_PT2_5(scaledBitmap);
                    break;
                case "PT3":
                    imgOut = apply_PT3(scaledBitmap);
                    break;
                case "PT4":
                    imgOut = apply_PT4(scaledBitmap);
                    break;
                case "PT5-1":
                    imgOut = apply_PT5_1(scaledBitmap);
                    break;
                case "PT6":
                    imgOut = apply_PT6(scaledBitmap);
                    break;
                case "PT7":
                    imgOut = apply_PT7(scaledBitmap);
                    break;
                case "PT11":
                    imgOut = apply_PT11(scaledBitmap);
                    break;
                case "PT15":
                    imgOut = apply_PT15(scaledBitmap);
                    break;
                case "PT13":
                    imgOut = apply_PT13(scaledBitmap);
                    break;
                case "PT17":
                    imgOut = apply_PT17(scaledBitmap);
                    break;
                case "PT19":
                    imgOut = apply_PT19(scaledBitmap);
                    break;
                case "PT20":
                    imgOut = apply_PT20(scaledBitmap);
                    break;
                case "PT21":
                    imgOut = apply_PT21(scaledBitmap);
                    break;
                case "PT22":
                    imgOut = apply_PT22(scaledBitmap);
                    break;
                case "PT27":
                    imgOut = apply_PT27(scaledBitmap);
                    break;
                case "PT28":
                    imgOut = apply_PT28(scaledBitmap);
                    break;
                case "PT29":
                    imgOut = apply_PT29(scaledBitmap);
                    break;
                case "PT31":
                    imgOut = apply_PT31(scaledBitmap);
                    break;
                case "PT32":
                    imgOut = apply_PT32(scaledBitmap);
                    break;
                case "PT33":
                    imgOut = apply_PT33(scaledBitmap);
                    break;
                case "PT34":
                    imgOut = apply_PT34(scaledBitmap);
                    break;
                case "PT35":
                    imgOut = apply_PT35(scaledBitmap);
                    break;
                case "PT36":
                    imgOut = apply_PT36(scaledBitmap);
                    break;
                case "PT37":
                    imgOut = apply_PT37(scaledBitmap);
                    break;
                case "PTM":
                    imgOut = apply_PTM(scaledBitmap);
                    break;
                case "PTB":
                    imgOut = apply_PTB(scaledBitmap);
                    break;
                case "PT39":
                    imgOut =mNewFilter.apply_PT39(scaledBitmap);
                    break;
                case "PT19a":
                    imgOut = mNewFilter.apply_PT19a(scaledBitmap);
                    break;
                case "PT19b":
                    imgOut = mNewFilter.apply_PT19b(scaledBitmap);
                    break;
                case "PT19c":
                    imgOut =  mNewFilter.apply_PT19c(scaledBitmap);
                    break;
                case "PT11a":
                    imgOut = mNewFilter.apply_PT11a(scaledBitmap);
                    break;
                case "PT42":
                    imgOut = mNewFilter.apply_PT42(scaledBitmap);
                    break;
                default:
                    break;
            }
        } else {
            switch (Effect_Code) {

                case "PT41":
                    imgOut = mNewFilter.apply_LPT41(scaledBitmap);
                    break;
                case "PS02":
                    imgOut = apply_LPS02(scaledBitmap);
                    break;
                case "PS03":
                    imgOut = apply_LPS03(scaledBitmap);
                    break;
                case "PS07":
                    imgOut = apply_LPS07(scaledBitmap);
                    break;
                case "PS08":
                    imgOut = apply_LPS08(scaledBitmap);
                    break;
                case "PT1":
                    imgOut = apply_LPT1(scaledBitmap);
                    break;
                case "PT2-1":
                    imgOut = apply_LPT2_1(scaledBitmap);
                    break;
                case "PT2-2":
                    imgOut = apply_LPT2_2(scaledBitmap);
                    break;
                case "PT2-3":
                    imgOut = apply_LPT2_3(scaledBitmap);
                    break;
                case "PT2-4":
                    imgOut = apply_LPT2_4(scaledBitmap);
                    break;
                case "PT2-5":
                    imgOut = apply_LPT2_5(scaledBitmap);
                    break;
                case "PT3":
                    imgOut = apply_LPT3(scaledBitmap);
                    break;
                case "PT4":
                    imgOut = apply_LPT4(scaledBitmap);
                    break;
                case "PT5-1":
                    imgOut = apply_LPT5_1(scaledBitmap);
                    break;
                case "PT6":
                    imgOut = apply_LPT6(scaledBitmap);
                    break;
                case "PT7":
                    imgOut = apply_LPT7(scaledBitmap);
                    break;
                case "PT11":
                    imgOut = apply_LPT11(scaledBitmap);
                    break;
                case "PT15":
                    imgOut = apply_LPT15(scaledBitmap);
                    break;
                case "PT13":
                    imgOut = apply_LPT13(scaledBitmap);
                    break;
                case "PT17":
                    imgOut = apply_LPT17(scaledBitmap);
                    break;
                case "PT19":
                    imgOut = apply_LPT19(scaledBitmap);
                    break;
                case "PT20":
                    imgOut = apply_LPT20(scaledBitmap);
                    break;
                case "PT21":
                    imgOut = apply_LPT21(scaledBitmap);
                    break;
                case "PT22":
                    imgOut = apply_LPT22(scaledBitmap);
                    break;
                case "PT27":
                    imgOut = apply_LPT27(scaledBitmap);
                    break;
                case "PT28":
                    imgOut = apply_LPT28(scaledBitmap);
                    break;
                case "PT29":
                    imgOut = apply_LPT29(scaledBitmap);
                    break;
                case "PT31":
                    imgOut = apply_LPT31(scaledBitmap);
                    break;
                case "PT32":
                    imgOut = apply_LPT32(scaledBitmap);
                    break;
                case "PT33":
                    imgOut = apply_LPT33(scaledBitmap);
                    break;
                case "PT34":
                    imgOut = apply_LPT34(scaledBitmap);
                    break;
                case "PT35":
                    imgOut = apply_LPT35(scaledBitmap);
                    break;
                case "PT36":
                    imgOut = apply_LPT36(scaledBitmap);
                    break;
                case "PT37":
                    imgOut = apply_LPT37(scaledBitmap);
                    break;
                case "PTM":
                    imgOut = apply_LPTM(scaledBitmap);
                    break;
                case "PTB":
                    imgOut = apply_LPTB(scaledBitmap);
                    break;
                case "PT39":
                    imgOut =mNewFilter.apply_LPT39(scaledBitmap);
                    break;
                case "PT19a":
                    imgOut = mNewFilter.apply_LPT19a(scaledBitmap);
                    break;
                case "PT19b":
                    imgOut = mNewFilter.apply_LPT19b(scaledBitmap);
                    break;
                case "PT19c":
                    imgOut =  mNewFilter.apply_LPT19c(scaledBitmap);
                    break;
                case "PT11a":
                    imgOut = mNewFilter.apply_LPT11a(scaledBitmap);
                    break;
                case "PT42":
                    imgOut = mNewFilter.apply_LPT42(scaledBitmap);
                    break;
                default:
                    break;
            }
        }
        displayOutPutImage(imgOut);


    }


    public void hideLoader() {
        getSupportFragmentManager().beginTransaction().remove(trans).commit();
    }

    public Bitmap apply_PS02(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);//with 27% it gave good result
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp1, 0, 0);
        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PS03(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.ps031overlay);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);
        imgSrc.contrastImage(true);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        img1.destroyImages();
        imgTemp_1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PS07(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.ps071normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PS08(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.ps081normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt11vividlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt12frame);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.setGrayscale();
        imgSrc.levelImage("0%,24%,.5");

        imgTemp.thresholdImage(31000);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img1, 0, 0);
        imgSrc.modulateImage("100,65,100");
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT2_1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt211multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt212multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);

        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT2_2(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt221multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT2_3(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt231multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT2_4(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt241multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT2_5(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt251multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT3(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt31normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt32multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt33softlight);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt34overlay);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.setGrayscale();
        imgSrc.levelImage("0%,18%,.8");
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, img4, 0, 0);
        imgSrc.modulateImage("90,100,100");
        imgSrc.contrastImage(true);
        imgSrc.contrastImage(true);


        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT4(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt41darken);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt42normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt43overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt44multiply);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.setGrayscale();

        imgSrc.compositeImage(CompositeOperator.DarkenCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img4, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT5_1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt511normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT6(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt6background);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Bitmap bmp3_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt6mask);
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);

        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgMask1 = new MagickImage(i3, ba3);

        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmp3, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        img1.compositeImage(CompositeOperator.OverCompositeOp, img4, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img4.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT7(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt73overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt74softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt75vividlight);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 450, 600, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        imgSrc.setGrayscale();

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img5, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT11(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt112overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Bitmap bmp3_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt11mask);
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);


        imgSrc.setGrayscale();
        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        img4.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        img4.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = img4.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img4.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT13(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt131multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt132normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt133overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt134softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT15(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt151normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt152colordodge);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt153softlight);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt15mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);


        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.ColorDodgeCompositeOp, img2, 0, 0);
        img1.compositeImage(CompositeOperator.SoftLightCompositeOp, img3, 0, 0);

        img1.levelImage("0%,24%,1");

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        imgMasked.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT17(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt171softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt172normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt173lighten);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt174overlay);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt175colorburn);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 450, 600, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        imgSrc.modulateImage("91,110,112");

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.LightenCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img4, 0, 0);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img5, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT19(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt191overlay);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt192darken);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.modulateImage("100,100,100");
        imgSrc.thresholdImage(31000);

        imgTemp.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);
        imgTemp.modulateImage("100,60,100");
        imgTemp.compositeImage(CompositeOperator.HardLightCompositeOp, img1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.DarkenCompositeOp, img2, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT20(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.setGrayscale();
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, imgTemp, 0, 0);
        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(5, 60, 2, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT21(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt211softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img1, 0, 0);
        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        img1.destroyImages();
        imgTemp_1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT22(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt221normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt222multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt223colorburn);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt224vividlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img4, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT27(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt271normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt272overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt27grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt27grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Bitmap bmpmask_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt27mask);
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmpmask_1, 450, 600, false);


        imgSrc.setGrayscale();

        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte[] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage imgMasked = new MagickImage(i6, ba6);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);

        MagickImage imgTemp = img1.cloneImage(0, 0, true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        img1.compositeImage(CompositeOperator.HueCompositeOp, imgTemp, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgMasked.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT28(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt28bg);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt281overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt28grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt28grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Bitmap bmpmask_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt28mask);
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmpmask_1, 450, 600, false);


        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte[] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage imgMasked = new MagickImage(i6, ba6);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        MagickImage imgTemp = img1.cloneImage(0, 0, true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        img1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgMasked.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT29(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt291vividlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt292lighten);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt293normal);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt29mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        imgTemp1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.PinLightCompositeOp, img1, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.LightenCompositeOp, img2, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.OverCompositeOp, img3, 0, 0);

        imgTemp1.levelImage("0%,24%,1");

        byte[] blobImg1 = imgTemp1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp_1.destroyImages();
        imgMasked.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT31(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt311multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt312normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt31grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt31grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt313multiply);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 450, 600, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        MagickImage imgSrc1 = imgSrc.sharpenImage(0, 3);
        imgSrc1.modulateImage("100,50,100");
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img5, 0, 0);

        MagickImage imgTemp = imgSrc1.cloneImage(0, 0, true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        imgSrc1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        img5.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT32(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt321multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt32mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);

        imgSrc.setGrayscale();
        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        imgMasked.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgMasked.levelImage("0%,24%,1");

        byte[] blobImg1 = imgMasked.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgMasked.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT33(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt331normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt332multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt33mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);

        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgMasked.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT34(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt341normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt342colordodge);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt343overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt344lighten);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt345overlay);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 450, 600, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt34mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);


        Bitmap result = Bitmap.createBitmap(450, 600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.ColorDodgeCompositeOp, img2, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        img1.compositeImage(CompositeOperator.LightenCompositeOp, img4, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img5, 0, 0);
        img1.levelImage("0%,24%,1");

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        imgMasked.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT35(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt351softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt352multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt353softlight);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt354softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);

        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PT36(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt361colorize);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt362screen);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt36grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt36grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt363overlay);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 450, 600, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt364normal);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmp6 = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        bmp6.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte[] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage img6 = new MagickImage(i6, ba6);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);

        imgTemp1.compositeImage(CompositeOperator.ColorizeCompositeOp, img1, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, img2, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.OverlayCompositeOp, img5, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.OverCompositeOp, img6, 0, 0);

        MagickImage imgTemp2 = imgTemp1.cloneImage(0, 0, true);
        imgTemp2.levelImage("0%,24%,.5");

        imgTemp2.modulateImage("100,0,100");
        imgTemp2.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp2.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        imgTemp1.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp2, 0, 0);

        byte[] blobImg = imgTemp1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        img5.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_PT37(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt371darken);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt372exclusion);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt373normal);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        imgSrc.compositeImage(CompositeOperator.DarkenCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.ExclusionCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img3, 0, 0);

        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp_1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_PTM(Bitmap src) throws MagickException {
        isWaterMarkOn = false;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        String valid_until = "18/12/2016 08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Drawable myDrawable1;
        if (new Date().after(strDate)) {
            myDrawable1 = getResources().getDrawable(R.drawable.mportraitafter);
        }
        else{
            myDrawable1 = getResources().getDrawable(R.drawable.mportraitbefore);
        }

        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }
    public Bitmap apply_PTB(Bitmap src) throws MagickException {
        isWaterMarkOn = false;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        String valid_until = "29/01/2017 08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Drawable myDrawable1;
        if (new Date().after(strDate)) {
            myDrawable1 = getResources().getDrawable(R.drawable.bportraitafter);
        }
        else{
            myDrawable1 = getResources().getDrawable(R.drawable.bportraitbefore);
        }

        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    //Landscape Effects
    public Bitmap apply_LPS02(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);//with 27% it gave good result
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp1, 0, 0);
        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPS03(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.ps031overlay);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);
        imgSrc.contrastImage(true);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        img1.destroyImages();
        imgTemp_1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPS07(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.ps071normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);

        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPS08(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.ps081normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img1_1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt11vividlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt12frame);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.setGrayscale();
        imgSrc.levelImage("0%,24%,.5");

        imgTemp.thresholdImage(31000);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img1, 0, 0);
        imgSrc.modulateImage("100,65,100");
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img1_1.destroyImages();
        img2_1.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT2_1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.lpt211multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt212multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);

        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT2_2(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt221multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT2_3(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt231multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT2_4(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt241multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT2_5(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt251multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0, 100, 2, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.modulateImage("100,106,119");

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT3(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt31normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt32multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt33softlight);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt34overlay);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.setGrayscale();
        imgSrc.levelImage("0%,18%,.8");
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, img4, 0, 0);
        imgSrc.modulateImage("90,100,100");
        imgSrc.contrastImage(true);
        imgSrc.contrastImage(true);


        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT4(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt41darken);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt42normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt43overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt44multiply);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.setGrayscale();

        imgSrc.compositeImage(CompositeOperator.DarkenCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img4, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT5_1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.lpt511normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT6(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.lpt6background);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Bitmap bmp3_1 = BitmapFactory.decodeResource(getResources(), R.drawable.lpt6mask);
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);

        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgMask1 = new MagickImage(i3, ba3);

        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmp3, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        img1.compositeImage(CompositeOperator.OverCompositeOp, img4, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img4.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT7(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt73overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3_1 = new MagickImage(i3, ba3);
        MagickImage img3 = img3_1.rotateImage(90);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt74softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4_1 = new MagickImage(i4, ba4);
        MagickImage img4 = img4_1.rotateImage(90);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt75vividlight);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 450, 600, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5_1 = new MagickImage(i5, ba5);
        MagickImage img5 = img5_1.rotateImage(90);

        imgSrc.setGrayscale();

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img5, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        img2_1.destroyImages();
        img3_1.destroyImages();
        img4_1.destroyImages();
        img5_1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT11(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt112overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Bitmap bmp3_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt11mask);
        Bitmap bmpMask_1 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        Bitmap bmpMask = RotateBitmap(bmpMask_1);


        imgSrc.setGrayscale();
        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        img4.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        img4.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = img4.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img4.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT13(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt131multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt132normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt133overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt134softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img1_1.destroyImages();
        img2.destroyImages();
        img2_1.destroyImages();
        img3.destroyImages();
        img4.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT15(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt151normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt152colordodge);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt153softlight);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt15mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 600, 450, false);


        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.ColorDodgeCompositeOp, img2, 0, 0);
        img1.compositeImage(CompositeOperator.SoftLightCompositeOp, img3, 0, 0);

        img1.levelImage("0%,24%,1");

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        imgMasked.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT17(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt171softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt172normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt173lighten);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt174overlay);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt175colorburn);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 600, 450, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        imgSrc.modulateImage("91,110,112");

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.LightenCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img4, 0, 0);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img5, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        img1_1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT19(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt191overlay);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt192darken);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.modulateImage("100,100,100");
        imgSrc.thresholdImage(31000);

        imgTemp.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);
        imgTemp.modulateImage("100,60,100");
        imgTemp.compositeImage(CompositeOperator.HardLightCompositeOp, img1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.DarkenCompositeOp, img2, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT20(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        imgSrc.setGrayscale();
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, imgTemp, 0, 0);
        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(5, 60, 2, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT21(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt211softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img1, 0, 0);
        imgSrc.levelImage("0%,24%,1");//15, .8, 240);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        img1.destroyImages();
        imgTemp_1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT22(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt221normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt222multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt223colorburn);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 450, 600, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3_1 = new MagickImage(i3, ba3);
        MagickImage img3 = img3_1.rotateImage(90);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt224vividlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 450, 600, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4_1 = new MagickImage(i4, ba4);
        MagickImage img4 = img4_1.rotateImage(90);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img4, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img2_1.destroyImages();
        img3_1.destroyImages();
        img4_1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT27(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt271normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt272overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt27grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt27grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Bitmap bmpmask_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt27mask);
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmpmask_1, 600, 450, false);


        imgSrc.setGrayscale();

        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte[] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage imgMasked = new MagickImage(i6, ba6);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);

        MagickImage imgTemp = img1.cloneImage(0, 0, true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        img1.compositeImage(CompositeOperator.HueCompositeOp, imgTemp, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgMasked.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT28(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt28bg);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt281overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt28grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt28grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Bitmap bmpmask_1 = BitmapFactory.decodeResource(getResources(), R.drawable.pt28mask);
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmpmask_1, 600, 450, false);


        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte[] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage imgMasked = new MagickImage(i6, ba6);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        MagickImage imgTemp = img1.cloneImage(0, 0, true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        img1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgMasked.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT29(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.lpt291vividlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.lpt292lighten);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.lpt293normal);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.lpt29mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 600, 450, false);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        imgTemp1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.PinLightCompositeOp, img1, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.LightenCompositeOp, img2, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.OverCompositeOp, img3, 0, 0);

        imgTemp1.levelImage("0%,24%,1");

        byte[] blobImg1 = imgTemp1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp_1.destroyImages();
        imgMasked.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT31(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt311multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt312normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt31grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt31grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt313multiply);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 600, 450, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        MagickImage imgSrc1 = imgSrc.sharpenImage(0, 3);
        imgSrc1.modulateImage("100,50,100");
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgSrc1.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);
        imgSrc1.compositeImage(CompositeOperator.MultiplyCompositeOp, img5, 0, 0);

        MagickImage imgTemp = imgSrc1.cloneImage(0, 0, true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        imgSrc1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        img5.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT32(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt321multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt32mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 600, 450, false);

        imgSrc.setGrayscale();
        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src1, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        imgMasked.compositeImage(CompositeOperator.MultiplyCompositeOp, img1, 0, 0);
        imgMasked.levelImage("0%,24%,1");

        byte[] blobImg1 = imgMasked.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgMasked.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT33(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);



        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt331normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 450, 600, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1_1 = new MagickImage(i1, ba1);
        MagickImage img1 = img1_1.rotateImage(90);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt332multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt33mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask_1 = Bitmap.createScaledBitmap(bmp6_1, 450, 600, false);
        Bitmap bmpMask = RotateBitmap(bmpMask_1);

        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgMasked.destroyImages();
        img1_1.destroyImages();
        img2_1.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT34(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt341normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt342colordodge);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt343overlay);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt344lighten);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt345overlay);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 600, 450, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt34mask);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmpMask = Bitmap.createScaledBitmap(bmp6_1, 600, 450, false);


        Bitmap result = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmpMask, 0, 0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream baoMasked = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, baoMasked);
        byte[] baMasked = baoMasked.toByteArray();
        ImageInfo iMasked = new ImageInfo();
        MagickImage imgMasked = new MagickImage(iMasked, baMasked);

        img1.compositeImage(CompositeOperator.OverCompositeOp, imgMasked, 0, 0);
        img1.compositeImage(CompositeOperator.ColorDodgeCompositeOp, img2, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img3, 0, 0);
        img1.compositeImage(CompositeOperator.LightenCompositeOp, img4, 0, 0);
        img1.compositeImage(CompositeOperator.OverlayCompositeOp, img5, 0, 0);
        img1.levelImage("0%,24%,1");

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        img2_1.destroyImages();
        imgMasked.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT35(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt351softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt352multiply);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 450, 600, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2, ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt353softlight);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt354softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4, ba4);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.MultiplyCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img3, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);

        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        img4.destroyImages();
        img2_1.destroyImages();
        return imgOutput;

    }

    public Bitmap apply_LPT36(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);

        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt361colorize);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);

        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt362screen);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt36grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3, ba3);

        Drawable myDrawable4 = getResources().getDrawable(R.drawable.pt36grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1, 600, 450, false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte[] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4, ba4);

        Drawable myDrawable5 = getResources().getDrawable(R.drawable.pt363overlay);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1, 600, 450, false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte[] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5, ba5);

        Drawable myDrawable6 = getResources().getDrawable(R.drawable.pt364normal);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmp6 = Bitmap.createScaledBitmap(bmp6_1, 600, 450, false);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        bmp6.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte[] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage img6 = new MagickImage(i6, ba6);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);

        imgTemp1.compositeImage(CompositeOperator.ColorizeCompositeOp, img1, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, img2, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.OverlayCompositeOp, img5, 0, 0);
        imgTemp1.compositeImage(CompositeOperator.OverCompositeOp, img6, 0, 0);

        MagickImage imgTemp2 = imgTemp1.cloneImage(0, 0, true);
        imgTemp2.levelImage("0%,24%,.5");

        imgTemp2.modulateImage("100,0,100");
        imgTemp2.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp2.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        imgTemp1.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp2, 0, 0);

        byte[] blobImg = imgTemp1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        img1.destroyImages();
        img5.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }

    public Bitmap apply_LPT37(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc, ba);


        Drawable myDrawable1 = getResources().getDrawable(R.drawable.pt371darken);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1, 600, 450, false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte[] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1, ba1);


        Drawable myDrawable2 = getResources().getDrawable(R.drawable.pt372exclusion);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1, 600, 450, false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte[] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2, ba2);

        Drawable myDrawable3 = getResources().getDrawable(R.drawable.pt373normal);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1, 600, 450, false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte[] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage img3 = new MagickImage(i3, ba3);

        MagickImage imgTemp = imgSrc.cloneImage(0, 0, true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0, 0, true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0, 1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        imgSrc.compositeImage(CompositeOperator.DarkenCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.ExclusionCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img3, 0, 0);

        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img3.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp_1.destroyImages();
        return imgOutput;

    }


    /**
     * Mumbai
     * @param src
     * @return
     * @throws MagickException
     */

    public Bitmap apply_LPTM(Bitmap src) throws MagickException {
        isWaterMarkOn = false;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        String valid_until = "18/12/2016 08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Drawable myDrawable1;
        if (new Date().after(strDate)) {
            myDrawable1 = getResources().getDrawable(R.drawable.mlandscapeafter);
        }
        else{
            myDrawable1 = getResources().getDrawable(R.drawable.mlandscapebefore);
        }

        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }
    public Bitmap apply_LPTB(Bitmap src) throws MagickException, java.text.ParseException {
        isWaterMarkOn = false;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        String valid_until = "29/01/2017 08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date strDate = null;
        try {
            strDate = sdf.parse(valid_until);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Drawable myDrawable1;
        if (new Date().after(strDate)) {
            myDrawable1 = getResources().getDrawable(R.drawable.blandscapeafter);
        }
        else{
            myDrawable1 = getResources().getDrawable(R.drawable.blandscapebefore);
        }

        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        imgSrc.compositeImage(CompositeOperator.OverCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        return imgOutput;

    }

    public static Bitmap RotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onStop() {
        super.onStop();

    }



    private SeekBar seekBar;
    private LinearLayout mSeekBarContainer;
    private LinearLayout mLinearEditLayout;
    private String currentEditMode=EditEnum.Edit.AUTOCOLOR.toString();
    private RecyclerView recyclerView;
    private ImageView mIconDiscardEffectView;
    private ImageView mIconAcceptEffectView;
    private Bitmap scaledBitmap;
    Bitmap editTempBitmap;
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private void initEditControl(){

        mIconDiscardEffectView=(ImageView)findViewById(R.id.discard);
        mIconAcceptEffectView=(ImageView)findViewById(R.id.accept);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mSeekBarContainer=(LinearLayout)findViewById(R.id.color_control_indicator);
        mSeekBarContainer.setVisibility(View.VISIBLE);

        if (isPortrait) {
            scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
            imageView.setImageBitmap(scaledBitmap);
        }else{
            scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
            imageView.setImageBitmap(scaledBitmap);
        }

        mIconDiscardEffectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSeekBarContainer.setVisibility(View.INVISIBLE);
                if (isPortrait) {
                    scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
                    imageView.setImageBitmap(scaledBitmap);
                }else{
                    scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
                    imageView.setImageBitmap(scaledBitmap);
                }
            }
        });
        mIconAcceptEffectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSeekBarContainer.setVisibility(View.INVISIBLE);
                if (isPortrait) {
                    editTempBitmap = Bitmap.createScaledBitmap(scaledBitmap, 450, 600, false);
                    myBitmap = Bitmap.createScaledBitmap(scaledBitmap, 450, 600, false);
                    imageView.setImageBitmap(myBitmap);
                }else{
                    editTempBitmap =  Bitmap.createScaledBitmap(scaledBitmap, 600, 450, false);
                    myBitmap = Bitmap.createScaledBitmap(scaledBitmap, 600, 450, false);
                    imageView.setImageBitmap(myBitmap);
                }
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {


                final PicazzyEvent picazzyEvent=mEventAdapter.getItem(position);

                if(isPortrait){
                    if(picazzyEvent.getImagePotrBitmap()==null || picazzyEvent.getImagePotrBitmap().getHeight()==0){
                        return;
                    }
                }else{
                    if(picazzyEvent.getImageLandBitmap()==null || picazzyEvent.getImageLandBitmap().getHeight()==0){
                        return;
                    }
                }


                mEventAdapter.notifyDateChanged(position);

                Effect_Code = picazzyEvent.getEventCode();
                Effect_Name = picazzyEvent.getEventName();
                EditorActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    FragmentTransaction fragmentTransaction;
                    trans = new LoaderFragment();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, trans);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    AndroidUtil.printLog("Is Pot "+isPortrait);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isWaterMarkOn = false;
                            Bitmap scaledBitmap;
                            if (isPortrait) {
                                scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
                                Bundle bundle = new Bundle();
                                bundle.putString("Dimension", "Portrait");
                                bundle.putString("EffectName", Effect_Name);
                                firebaseAnalytics.logEvent("AppliedEffect", bundle);
                                firebaseAnalytics.logEvent(Effect_Code, bundle);
                                try {
                                    if(picazzyEvent.getImagePotrBitmap()!=null && picazzyEvent.getImagePotrBitmap().getHeight()>2){
                                        displayOutPutImage(BitmapProcessing.apply_Event(scaledBitmap,picazzyEvent.getImagePotrBitmap()));
                                    }

                                } catch (MagickException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
                                Bundle bundle = new Bundle();
                                bundle.putString("Dimension", "Landscape");
                                bundle.putString("EffectName", Effect_Name);
                                firebaseAnalytics.logEvent("AppliedEffect", bundle);
                                firebaseAnalytics.logEvent(Effect_Code, bundle);
                                try {
                                    if(picazzyEvent.getImageLandBitmap()!=null && picazzyEvent.getImageLandBitmap().getHeight()>2){
                                        displayOutPutImage(BitmapProcessing.apply_LEvent(scaledBitmap,picazzyEvent.getImageLandBitmap()));
                                    }
                                } catch (MagickException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, 2000);
                }
            });


            }
        });


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<EventInfo> call = apiService.getEventInfo();

        Type type = new TypeToken<EventInfo>() {}.getType();
        EventInfo eventInfo= new Gson().fromJson( mSessionManager.getEventJson(), type);
        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(EditorActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(verticalLayoutmanager);
        mEventAdapter=new EventAdapter(EditorActivity.this,eventInfo);
        if(eventInfo!=null && eventInfo.getPicazzyEvent()!=null && eventInfo.getPicazzyEvent().size()>0){
            recyclerView.setAdapter(mEventAdapter);
        }

        call.enqueue(new Callback<EventInfo>() {
            @Override
            public void onResponse(Call<EventInfo>call, Response<EventInfo> response) {
                EventInfo movies = response.body();
                Gson gson = new Gson();
                String json = gson.toJson(movies);
                mSessionManager.saveEventJson(json);
                LinearLayoutManager verticalLayoutmanager
                        = new LinearLayoutManager(EditorActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(verticalLayoutmanager);
                mEventAdapter=new EventAdapter(EditorActivity.this,movies);
                recyclerView.setAdapter(mEventAdapter);
            }
            @Override
            public void onFailure(Call<EventInfo>call, Throwable t) {
                Type type = new TypeToken<EventInfo>() {}.getType();
                EventInfo eventInfo= new Gson().fromJson( mSessionManager.getEventJson(), type);
                LinearLayoutManager verticalLayoutmanager
                        = new LinearLayoutManager(EditorActivity.this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(verticalLayoutmanager);
                mEventAdapter=new EventAdapter(EditorActivity.this,eventInfo);
                if(eventInfo!=null && eventInfo.getPicazzyEvent()!=null && eventInfo.getPicazzyEvent().size()>0){
                    recyclerView.setAdapter(mEventAdapter);
                }
            }
        });


        seekBar=(SeekBar)findViewById(R.id.seekBar);
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dark_gray), PorterDuff.Mode.MULTIPLY);
        mSeekBarContainer.setVisibility(View.INVISIBLE);

        mLinearEditLayout=(LinearLayout)findViewById(R.id.edit_layout);
        TabLayout bottomNavigationView = (TabLayout)
                findViewById(R.id.edit_tab);

        final int[] iconArray={R.drawable.auto_selector,R.drawable.highlight_selector,R.drawable.brightness_selector,R.drawable.contrast_selector,R.drawable.saturation_selector,R.drawable.vignette_selector};
        String[] editOptionName=getResources().getStringArray(R.array.edit_option);
        for(int i=0;i<iconArray.length;i++){
            bottomNavigationView.addTab(bottomNavigationView.newTab());
            View custom1 = LayoutInflater.from(this).inflate(R.layout.edit_tab_row, null);
            ((ImageView) custom1.findViewById(R.id.tab_image_row)).setImageResource(iconArray[i]);
            ((TextView) custom1.findViewById(R.id.edit_tab_name)).setText(editOptionName[i]);
            TabLayout.Tab customTab1 = bottomNavigationView.getTabAt(i);
            customTab1.setCustomView(custom1);
        }

        bottomNavigationView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (isPortrait) {
                    scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
                    imageView.setImageBitmap(scaledBitmap);
                }else{
                    scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
                    imageView.setImageBitmap(scaledBitmap);
                }

                mSeekBarContainer.setVisibility(View.VISIBLE);
                editorTabSelection(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                if (isPortrait) {
                    scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);
                    imageView.setImageBitmap(scaledBitmap);
                }else{
                    scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
                    imageView.setImageBitmap(scaledBitmap);
                }

                mSeekBarContainer.setVisibility(View.VISIBLE);
                editorTabSelection(tab);

            }
        });


        if (isPortrait) {
            editTempBitmap = Bitmap.createScaledBitmap(myBitmap, 450, 600, false);

        }else{
            editTempBitmap = Bitmap.createScaledBitmap(myBitmap, 600, 450, false);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                int progress=arg0.getProgress();

                AndroidUtil.printLog("Valueee "+progress);


               if (currentEditMode.equalsIgnoreCase(EditEnum.Edit.BRIGHTNESS.toString())) {
                    scaledBitmap=BitmapProcessing.changeBitmapBrightness(editTempBitmap, (float) progress);
                    imageView.setImageBitmap(scaledBitmap);
                    mAttacher.update();

                } else if (currentEditMode.equalsIgnoreCase(EditEnum.Edit.CONTRAST.toString())) {
                    scaledBitmap=BitmapProcessing.changeBitmapContrast(editTempBitmap, (float) progress / 40);
                    imageView.setImageBitmap(scaledBitmap);
                    mAttacher.update();
                } else if (currentEditMode.equalsIgnoreCase(EditEnum.Edit.SATURATION.toString())) {
                    scaledBitmap=BitmapProcessing.changeBitmapSaturation(editTempBitmap, (float) progress);
                    imageView.setImageBitmap(scaledBitmap);
                    mAttacher.update();
                } else if (currentEditMode.equalsIgnoreCase(EditEnum.Edit.VIGENTTE.toString())) {
                    scaledBitmap=BitmapProcessing.vignette(EditorActivity.this, editTempBitmap, progress);
                    imageView.setImageBitmap(scaledBitmap);
                    mAttacher.update();
                } else if (currentEditMode.equalsIgnoreCase(EditEnum.Edit.SHADOW.toString())) {
                    float passValue = (progress+101)/1;
                    scaledBitmap=BitmapProcessing.doGamma(editTempBitmap, passValue, passValue, passValue);
                    imageView.setImageBitmap(scaledBitmap);
                    mAttacher.update();
                } else if (currentEditMode.equalsIgnoreCase(EditEnum.Edit.SMOTHINING.toString())) {
                    scaledBitmap=BitmapProcessing.applySmoothEffect(editTempBitmap,progress/10);
                    imageView.setImageBitmap(scaledBitmap);
                    mAttacher.update();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {



            }
        });

    }


    /**
     * This method used to set indicator position
     * @param tab
     */
    protected void editorTabSelection(TabLayout.Tab tab) {
        switch (tab.getPosition()){
            case 0:
                seekBar.setVisibility(View.INVISIBLE);
                try {
                    scaledBitmap=BitmapProcessing.apply_Auto(scaledBitmap);
                    imageView.setImageBitmap(scaledBitmap);
                } catch (MagickException e) {
                    e.printStackTrace();
                }
                seekBar.setProgress(50);
                currentEditMode= EditEnum.Edit.AUTOCOLOR.name();
                break;
        /*    case 1:
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
                currentEditMode= EditEnum.Edit.SMOTHINING.name();
                break;*/
            case 1:
                seekBar.setVisibility(View.INVISIBLE);
                scaledBitmap=BitmapProcessing.sharpen(scaledBitmap, 0);
                imageView.setImageBitmap(scaledBitmap);
                mAttacher.update();
                break;
            case 2:
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
                currentEditMode= EditEnum.Edit.BRIGHTNESS.name();
                break;
            case 3:
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
                currentEditMode= EditEnum.Edit.CONTRAST.name();
                break;

            case 4:
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
                currentEditMode= EditEnum.Edit.SATURATION.name();
                break;
            case 5:
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setProgress(0);
                currentEditMode= EditEnum.Edit.VIGENTTE.name();
                break;
        }
    }

    public void startHome(View v){

        Intent intent=new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private  void handleShare(final Uri myUri){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        instagram_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ShareUtils.shareWithInstagram(imageView,EditorActivity.this,isPortrait);
                    Bundle bundle = new Bundle();
                    bundle.putString("ShareActivity", "Instagram");
                    bundle.putString("EffectName", Effect_Name);
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MagickException e) {
                    e.printStackTrace();
                }

            }
        });



        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   /* ShareUtils.shareWithFacebook(imageView,EditorActivity.this);*/

                    Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    /*SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bmp)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .setContentUrl(Uri.parse("http://picazzy.com/"))
                            .build();

                shareDialog.show(content);*/



               if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://picazzy.com/"))
                            .setImageUrl(myUri)
                            .build();
                    shareDialog.show(linkContent);

                }

                    Bundle bundle = new Bundle();
                    bundle.putString("ShareActivity", "Facebook");
                    bundle.putString("EffectName", Effect_Name);
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            }
        });




        img_save_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Do you want to save in Gallery");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            ShareUtils.download(imageView,EditorActivity.this);
                            Toast.makeText(EditorActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("ShareActivity", "FSave To Galleryacebook");
                            bundle.putString("EffectName", Effect_Name);
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("AlertDialog", "Negative");
                            }
                        }).show();

            }
        });


        imv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //Here We share the Effected Image
        imv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ShareUtils.shareWithAll(imageView,EditorActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString("ShareActivity", "Shared Generic");
                bundle.putString("EffectName", Effect_Name);
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            }
        });

        ImageView whatsUp=(ImageView)findViewById(R.id.image_whats_up);
        whatsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ShareUtils.shareWithWhats(imageView,EditorActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("ShareActivity", "Instagram");
                    bundle.putString("EffectName", Effect_Name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        ImageView twitter=(ImageView)findViewById(R.id.twiietr);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ShareUtils.shareWithTwitter(imageView,EditorActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("ShareActivity", "Twitter");
                    bundle.putString("EffectName", Effect_Name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }



}

