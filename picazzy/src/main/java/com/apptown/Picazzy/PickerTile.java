package com.apptown.Picazzy;

import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ${="Ashish"} on 17/12/16.
 */

public class PickerTile {

    public static final int IMAGE = 1;
    public static final int CAMERA = 2;
    public static final int GALLERY = 3;
    protected final Uri imageUri;


    public PickerTile(@Nullable Uri imageUri) {
        this.imageUri = imageUri;

    }

    @Nullable
    public Uri getImageUri() {
        return imageUri;
    }

}
