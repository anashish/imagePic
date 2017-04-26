package com.apptown.Picazzy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import static android.R.attr.orientation;

/**
 * Created by ${="Ashish"} on 15/1/17.
 */

public class RotateTransformation  extends BitmapTransformation {
    private float rotate = 0f;
    String id;


    public RotateTransformation(Context context, float rotate, String id) {
        super(context);
        this.rotate = rotate;
        this.id = id;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
                               int outWidth, int outHeight) {

        int exifOrientationDegrees = getExifOrientationDegrees(rotate);
        return TransformationUtils.rotateImageExif(toTransform, pool, exifOrientationDegrees);
    }

    private int getExifOrientationDegrees(float rotate) {

        int exifInt;
        switch (orientation) {
            case 90:
                exifInt = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            default:
                exifInt = ExifInterface.ORIENTATION_NORMAL;
                break;
        }
        return exifInt;
    }

    @Override
    public String getId() {
        return "com.apptown.Picazzy.util.RotateTransformation." + id;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
