package com.apptown.Picazzy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.apptown.Picazzy.R;

import java.io.ByteArrayOutputStream;

import magick.CompositeOperator;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

/**
 * Created by ${="Ashish"} on 11/1/17.
 */

public class InstaShare {


    //instagram
    public static Bitmap share_Instagram(Bitmap src, Context context,boolean isPortrait) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.i2next);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        if(isPortrait)
            img1.compositeImage(CompositeOperator.OverCompositeOp, imgSrc, 75, 0);
        else
            img1.compositeImage(CompositeOperator.OverCompositeOp, imgSrc, 0, 75);

        byte[] blobImg = img1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();

        return imgOutput;

    }
}
