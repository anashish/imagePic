package com.apptown.Picazzy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

public class NewFilter {


    private Context context;

    public NewFilter(Context context) {
        this.context=context;
    }

    //PT2_1
    public Bitmap apply_PF1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0,100,2,0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }
    //from PT7 b.png
    public Bitmap apply_PF2(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.setFilter(1);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);



        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //from PT7 output1.png
    public Bitmap apply_PF3(Bitmap src) throws MagickException {


        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.setFilter(1);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);



        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt74softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4,ba4);

        Drawable myDrawable5 = context.getResources().getDrawable(R.drawable.pt75vividlight);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1,450,600,false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte [] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5,ba5);


        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img5, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img4.destroyImages();
        img5.destroyImages();

        return imgOutput;

    }
    //from PT7 output2.png
    public Bitmap apply_PF4(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.setFilter(1);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);



        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        Drawable myDrawable5 = context.getResources().getDrawable(R.drawable.pt75vividlight);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1,450,600,false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte [] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5 = new MagickImage(i5,ba5);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img5, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img5.destroyImages();
        return imgOutput;

    }
    //PT11 c2.png
    public Bitmap apply_PF5(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();

        return imgOutput;

    }
    //PT11 d2.png
    public Bitmap apply_PF6(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        //Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt11gradient);
        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        // imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //PT11 output2.jpg
    public Bitmap apply_PF7(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

       // Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt11gradient);
        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);
        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }
    //PT26 output1
    public Bitmap apply_PF8(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        //Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt262vividlight);
        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.modulateImage("90,100,100");
        imgSrc.contrastImage(true);
        imgSrc.contrastImage(true);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();

        return imgOutput;

    }
    //PT27 d1.png
    public Bitmap apply_PF9(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt271normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt27grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt27grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);


        img1.compositeImage(CompositeOperator.OverCompositeOp, imgSrc, 0, 0);

        MagickImage imgTemp = img1.cloneImage(0,0,true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }
    //PT31 a1.png
    public Bitmap apply_PF10(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        MagickImage imgSrc1 = imgSrc.sharpenImage(0,3);
        imgSrc1.modulateImage("100,50,100");


        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgSrc1.destroyImages();

        imgSrc.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }
    //pt31 output1
    public Bitmap apply_PF11(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt312normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt31grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt31grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        MagickImage imgSrc1 = imgSrc.sharpenImage(0,3);
        imgSrc1.modulateImage("100,50,100");
        imgSrc1.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);

        MagickImage imgTemp = imgSrc1.cloneImage(0,0,true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        imgSrc1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }
    //PT34 c1.png
    public Bitmap apply_PF12(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt342colordodge);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.ColorDodgeCompositeOp, img2, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //a2
    public Bitmap apply_PF13(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.embossImage(0,1);
        imgSrc.contrastImage(true);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //b1
    public Bitmap apply_PF14(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.unsharpMaskImage(0,100,1,0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //2.jpg
    public Bitmap apply_PF15(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.embossImage(0,1);
        imgSrc.modulateImage("100,50,100");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //b
    public Bitmap apply_PF16(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("120,90,100");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //c1
    public Bitmap apply_PF17(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("90,100,100");
        imgSrc.contrastImage(true);
        imgSrc.contrastImage(true);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //e
    public Bitmap apply_PF18(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("110,120,80");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //grayscale
    public Bitmap apply_PF19(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("100,0,100");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //color brown
    public Bitmap apply_PF20(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pf20hardlight);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //color green
    public Bitmap apply_PF21(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pf21softlight);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //color brown
    public Bitmap apply_PF22(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pf22overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //Filters landscape
    //PT2_1
    public Bitmap apply_LPF1(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        MagickImage imgSrc1 = imgSrc.unsharpMaskImage(0,100,2,0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;


    }
    //from PT7 b.png
    public Bitmap apply_LPF2(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.setFilter(1);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2,ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //from PT7 output1.png
    public Bitmap apply_LPF3(Bitmap src) throws MagickException {


        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.setFilter(1);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2,ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt74softlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4_1 = new MagickImage(i4,ba4);
        MagickImage img4 = img4_1.rotateImage(90);

        Drawable myDrawable5 = context.getResources().getDrawable(R.drawable.pt75vividlight);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1,450,600,false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte [] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5_1 = new MagickImage(i5,ba5);
        MagickImage img5 = img5_1.rotateImage(90);


        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img4, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img5, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img4.destroyImages();
        img5.destroyImages();
        return imgOutput;

    }
    //from PT7 output2.png
    public Bitmap apply_LPF4(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.setFilter(1);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt71softlight);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt72overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2,ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        Drawable myDrawable5 = context.getResources().getDrawable(R.drawable.pt75vividlight);
        Bitmap bmp5_1 = ((BitmapDrawable) myDrawable5).getBitmap();
        Bitmap bmp5 = Bitmap.createScaledBitmap(bmp5_1,450,600,false);
        ByteArrayOutputStream bao5 = new ByteArrayOutputStream();
        bmp5.compress(Bitmap.CompressFormat.PNG, 100, bao5);
        byte [] ba5 = bao5.toByteArray();
        ImageInfo i5 = new ImageInfo();
        MagickImage img5_1 = new MagickImage(i5,ba5);
        MagickImage img5 = img5_1.rotateImage(90);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img5, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img5.destroyImages();
        return imgOutput;

    }
    //PT11 c2.png
    public Bitmap apply_LPF5(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();

        return imgOutput;

    }
    //PT11 d2.png
    public Bitmap apply_LPF6(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

       // Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt11gradient);
        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        //imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //PT11 output2.jpg
    public Bitmap apply_LPF7(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        //Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt11gradient);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);
        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }
    //PT26 output1
    public Bitmap apply_LPF8(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt224vividlight);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4_1 = new MagickImage(i4,ba4);
        MagickImage img4 = img4_1.rotateImage(90);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);

        imgSrc.modulateImage("90,100,100");
        imgSrc.contrastImage(true);
        imgSrc.contrastImage(true);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, img4, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img4.destroyImages();
        img4_1.destroyImages();

        return imgOutput;

    }
    //PT27 d1.png
    public Bitmap apply_LPF9(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt271normal);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt27grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt27grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,600,450,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);


        img1.compositeImage(CompositeOperator.OverCompositeOp, imgSrc, 0, 0);

        MagickImage imgTemp = img1.cloneImage(0,0,true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }
    //PT31 a1.png
    public Bitmap apply_LPF10(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        MagickImage imgSrc1 = imgSrc.sharpenImage(0,3);
        imgSrc1.modulateImage("100,50,100");


        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }
    //pt31 output1
    public Bitmap apply_LPF11(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt312normal);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt31grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt31grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,600,450,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        MagickImage imgSrc1 = imgSrc.sharpenImage(0,3);
        imgSrc1.modulateImage("100,50,100");
        imgSrc1.compositeImage(CompositeOperator.OverCompositeOp, img2, 0, 0);

        MagickImage imgTemp = imgSrc1.cloneImage(0,0,true);
        imgTemp.modulateImage("100,0,100");
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgGrad1, 0, 0);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        imgSrc1.compositeImage(CompositeOperator.SoftLightCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc1.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();
        imgTemp.destroyImages();
        imgSrc1.destroyImages();

        return imgOutput;

    }
    //PT34 c1.png
    public Bitmap apply_LPF12(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt342colordodge);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2_1 = new MagickImage(i2,ba2);
        MagickImage img2 = img2_1.rotateImage(90);

        imgSrc.compositeImage(CompositeOperator.ColorDodgeCompositeOp, img2, 0, 0);
        imgSrc.levelImage("0%,24%,1");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //a2
    public Bitmap apply_LPF13(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.embossImage(0,1);
        imgSrc.contrastImage(true);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //b1
    public Bitmap apply_LPF14(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.unsharpMaskImage(0,100,1,0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //2.jpg
    public Bitmap apply_LPF15(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.embossImage(0,1);
        imgSrc.modulateImage("100,50,100");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //b
    public Bitmap apply_LPF16(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("120,90,100");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //c1
    public Bitmap apply_LPF17(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("90,100,100");
        imgSrc.contrastImage(true);
        imgSrc.contrastImage(true);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //e
    public Bitmap apply_LPF18(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("110,120,80");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //grayscale
    public Bitmap apply_LPF19(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        imgSrc.modulateImage("100,0,100");

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //color brown
    public Bitmap apply_LPF20(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pf20hardlight);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //color green
    public Bitmap apply_LPF21(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pf21softlight);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.SoftLightCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //color brown
    public Bitmap apply_LPF22(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pf22overlay);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img2.destroyImages();

        return imgOutput;

    }
    //new effects
    //pt11 output1
    public Bitmap apply_PT11a(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        //Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt11gradient);
        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);
        imgSrc.setGrayscale();
        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }
    //PT19 a.png
    public Bitmap apply_PT19a(Bitmap src) throws MagickException {


        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.thresholdImage(31000);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();

        return imgOutput;

    }
    //PT19 b.png
    public Bitmap apply_PT19b(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.modulateImage("100,100,100");
        imgSrc.thresholdImage(31000);

        imgTemp.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;


    }
    //pt19 d.png
    public Bitmap apply_PT19c(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt191overlay);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.modulateImage("100,100,100");
        imgSrc.thresholdImage(31000);

        imgTemp.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);
        imgTemp.modulateImage("100,60,100");
        imgTemp.compositeImage(CompositeOperator.HardLightCompositeOp, img1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }
    //geometric mirroring
    public Bitmap apply_PT38(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt381multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.blank);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        Drawable myDrawable6 = context.getResources().getDrawable(R.drawable.pt38border);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmp6 = Bitmap.createScaledBitmap(bmp6_1,450,600,false);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        bmp6.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte [] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage img6 = new MagickImage(i6,ba6);

        Bitmap bmp3_1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.pt38newmask);
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);

        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgMask1 = new MagickImage(i3,ba3);

        Bitmap result = Bitmap.createBitmap( 450,600, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmp3, 0,0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4,ba4);

        MagickImage imgTemp2 = img4.trimImage();
        MagickImage imgTempUp = imgTemp2.cloneImage(0,0,true);
        // imgTemp1.trimImage();
        MagickImage imgTempGreen = img1.trimImage();
        imgTemp2.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTempGreen, 0, 0);
        MagickImage imgTempDown = imgTemp2.flipImage();
        // img4.flopImage();
        img2.compositeImage(CompositeOperator.OverCompositeOp, imgTempUp, 0, 0);
        img2.compositeImage(CompositeOperator.OverCompositeOp, imgTempDown, 0, 300);
        MagickImage imgTempScale =  img2.scaleImage(337,450);
        MagickImage imgTemp = imgSrc.blurImage(0,6);
        imgTemp.compositeImage(CompositeOperator.OverCompositeOp, imgTempScale, 60, 75);
        imgTemp.compositeImage(CompositeOperator.OverCompositeOp, img6, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img4.destroyImages();
        img6.destroyImages();
        imgMask1.destroyImages();
        imgTemp.destroyImages();
        imgTemp2.destroyImages();
        imgTempDown.destroyImages();
        imgTempGreen.destroyImages();
        imgTempScale.destroyImages();

        return imgOutput;

    }
    //inksketch
    public Bitmap apply_PT39(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt39grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt39grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0,0,true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0,1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        MagickImage imgTemp2 = imgSrc.cloneImage(0,0,true);
        imgTemp2.modulateImage("100,0,100");
        imgTemp2.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // imgTemp2.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        byte[] blobImg = imgTemp2.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp_1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //glitch effect
    public Bitmap apply_PT40(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt40finallines1);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,450,600,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt40finallines2);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,450,600,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);

        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0,0,true);
        MagickImage imgTemp2 = imgSrc.cloneImage(0,0,true);

        imgTemp.modulateImage("100,120,30");
        imgTemp1.modulateImage("100,150,90");

        imgSrc.compositeImage(CompositeOperator.LightenCompositeOp, imgTemp1, 6, 0);
        imgSrc.compositeImage(CompositeOperator.LightenCompositeOp, imgTemp, -6, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp2.destroyImages();

        return imgOutput;

    }
    //gradient map 1
    public Bitmap apply_PT41(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt41grad1a);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt41grad1b);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        imgSrc.thresholdImage(31000);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        // imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.cloneImage(0,0,true);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // MagickImage imgTemp2 = imgTemp.cloneImage(0,0,true);
        imgTemp1.negateImage(0);
        imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //gradient map 2
    public Bitmap apply_PT42(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt42grad2a);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt42grad2b);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        imgSrc.thresholdImage(31000);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        // imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.cloneImage(0,0,true);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // MagickImage imgTemp2 = imgTemp.cloneImage(0,0,true);
        imgTemp1.negateImage(0);
        imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //gradient map 3
    public Bitmap apply_PT43(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt43grad3a);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,450,600,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt43grad3b);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,450,600,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        imgSrc.thresholdImage(31000);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        // imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.cloneImage(0,0,true);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // MagickImage imgTemp2 = imgTemp.cloneImage(0,0,true);
        //imgTemp1.negateImage(0);
        // imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);
        // imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //New Effects Landscape
    //pt11 output1
    public Bitmap apply_LPT11a(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        //Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt11gradient);
        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt111colorburn);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        byte[] blobImg1 = imgSrc.imageToBlob(iSrc);
        Bitmap src1 = BitmapFactory.decodeByteArray(blobImg1, 0, blobImg1.length);
        imgSrc.setGrayscale();
        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.compositeImage(CompositeOperator.ColorBurnCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, imgTemp, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();

        return imgOutput;

    }
    //PT19 a.png
    public Bitmap apply_LPT19a(Bitmap src) throws MagickException {


        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        imgSrc.thresholdImage(31000);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        // imgTemp.destroyImages();
        return imgOutput;

    }
    //PT19 b.png
    public Bitmap apply_LPT19b(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);


        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.modulateImage("100,100,100");
        imgSrc.thresholdImage(31000);

        imgTemp.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;


    }
    //pt19 d.png
    public Bitmap apply_LPT19c(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt191overlay);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        imgSrc.modulateImage("100,100,100");
        imgSrc.thresholdImage(31000);

        imgTemp.compositeImage(CompositeOperator.SoftLightCompositeOp, imgSrc, 0, 0);
        imgTemp.modulateImage("100,60,100");
        imgTemp.compositeImage(CompositeOperator.HardLightCompositeOp, img1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        imgTemp.destroyImages();
        return imgOutput;

    }
    //geometric mirroring
    public Bitmap apply_LPT38(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.lpt381multiply);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.blank);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,472,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);
        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);

        Drawable myDrawable6 = context.getResources().getDrawable(R.drawable.pt38border);
        Bitmap bmp6_1 = ((BitmapDrawable) myDrawable6).getBitmap();
        Bitmap bmp6 = Bitmap.createScaledBitmap(bmp6_1,600,450,false);
        ByteArrayOutputStream bao6 = new ByteArrayOutputStream();
        bmp6.compress(Bitmap.CompressFormat.PNG, 100, bao6);
        byte [] ba6 = bao6.toByteArray();
        ImageInfo i6 = new ImageInfo();
        MagickImage img6 = new MagickImage(i6,ba6);

        Bitmap bmp3_1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.lpt38mask);
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);

        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgMask1 = new MagickImage(i3,ba3);

        Bitmap result = Bitmap.createBitmap( 600,450, Bitmap.Config.ARGB_8888);
        Canvas cc = new Canvas();
        cc.setBitmap(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        cc.drawBitmap(src, 0, 0, null);
        cc.drawBitmap(bmp3, 0,0, paint);
        paint.setXfermode(null);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage img4 = new MagickImage(i4,ba4);

        MagickImage imgTemp2 = img4.trimImage();
        MagickImage imgTempUp = imgTemp2.cloneImage(0,0,true);
        // imgTemp1.trimImage();
        MagickImage imgTempGreen = img1.trimImage();
        imgTemp2.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTempGreen, 0, 0);
        MagickImage imgTempDown = imgTemp2.flipImage();
        // img4.flopImage();
        img2.compositeImage(CompositeOperator.OverCompositeOp, imgTempUp, 0, 0);
        img2.compositeImage(CompositeOperator.OverCompositeOp, imgTempDown, 0, 236);
        MagickImage imgTempScale =  img2.scaleImage(450,337);
        MagickImage imgTemp = imgSrc.blurImage(0,6);
        imgTemp.compositeImage(CompositeOperator.OverCompositeOp, imgTempScale, 75, 65);
        imgTemp.compositeImage(CompositeOperator.OverCompositeOp, img6, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        img4.destroyImages();
        img6.destroyImages();
        imgMask1.destroyImages();
        imgTemp.destroyImages();
        imgTemp2.destroyImages();
        imgTempDown.destroyImages();
        imgTempGreen.destroyImages();
        imgTempScale.destroyImages();

        return imgOutput;

    }
    //inksketch
    public Bitmap apply_LPT39(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt39grad1);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt39grad2);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,600,450,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0,0,true);

        imgSrc.modulateImage("100,0,100");
        imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp_1 = imgTemp.blurImage(0,1.5);
        imgSrc.compositeImage(CompositeOperator.DivideSrcCompositeOp, imgTemp_1, 0, 0);
        imgSrc.normalizeImage();
        imgSrc.levelImage("0%,24%,.05"); //0, 0.05, 255);
        imgSrc.compositeImage(CompositeOperator.VividLightCompositeOp, imgTemp_1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.HardLightCompositeOp, imgTemp1, 0, 0);

        MagickImage imgTemp2 = imgSrc.cloneImage(0,0,true);
        imgTemp2.modulateImage("100,0,100");
        imgTemp2.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // imgTemp2.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);

        byte[] blobImg = imgTemp2.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgTemp_1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //glitch effect
    public Bitmap apply_LPT40(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable1 = context.getResources().getDrawable(R.drawable.pt40finallines1);
        Bitmap bmp1_1 = ((BitmapDrawable) myDrawable1).getBitmap();
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp1_1,600,450,false);
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, bao1);
        byte [] ba1 = bao1.toByteArray();
        ImageInfo i1 = new ImageInfo();
        MagickImage img1 = new MagickImage(i1,ba1);

        Drawable myDrawable2 = context.getResources().getDrawable(R.drawable.pt40finallines2);
        Bitmap bmp2_1 = ((BitmapDrawable) myDrawable2).getBitmap();
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp2_1,600,450,false);
        ByteArrayOutputStream bao2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, bao2);

        byte [] ba2 = bao2.toByteArray();
        ImageInfo i2 = new ImageInfo();
        MagickImage img2 = new MagickImage(i2,ba2);


        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        MagickImage imgTemp1 = imgSrc.cloneImage(0,0,true);

        imgTemp.modulateImage("100,120,30");
        imgTemp1.modulateImage("100,150,90");

        imgSrc.compositeImage(CompositeOperator.LightenCompositeOp, imgTemp1, 6, 0);
        imgSrc.compositeImage(CompositeOperator.LightenCompositeOp, imgTemp, -6, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img1, 0, 0);
        imgSrc.compositeImage(CompositeOperator.OverlayCompositeOp, img2, 0, 0);

        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        img1.destroyImages();
        img2.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();

        return imgOutput;

    }
    //gradient map 1
    public Bitmap apply_LPT41(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt41grad1a);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt41grad1b);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,600,450,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        imgSrc.thresholdImage(31000);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        // imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.cloneImage(0,0,true);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // MagickImage imgTemp2 = imgTemp.cloneImage(0,0,true);
        imgTemp1.negateImage(0);
        imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //gradient map 2
    public Bitmap apply_LPT42(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt42grad2a);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt42grad2b);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,600,450,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        imgSrc.thresholdImage(31000);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        // imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.cloneImage(0,0,true);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);
        // MagickImage imgTemp2 = imgTemp.cloneImage(0,0,true);
        imgTemp1.negateImage(0);
        imgTemp1.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad2, 0, 0);
        imgTemp.compositeImage(CompositeOperator.MultiplyCompositeOp, imgTemp1, 0, 0);

        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }
    //gradient map 3
    public Bitmap apply_LPT43(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Drawable myDrawable3 = context.getResources().getDrawable(R.drawable.pt43grad3a);
        Bitmap bmp3_1 = ((BitmapDrawable) myDrawable3).getBitmap();
        Bitmap bmp3 = Bitmap.createScaledBitmap(bmp3_1,600,450,false);
        ByteArrayOutputStream bao3 = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, bao3);
        byte [] ba3 = bao3.toByteArray();
        ImageInfo i3 = new ImageInfo();
        MagickImage imgGrad1 = new MagickImage(i3,ba3);

        Drawable myDrawable4 = context.getResources().getDrawable(R.drawable.pt43grad3b);
        Bitmap bmp4_1 = ((BitmapDrawable) myDrawable4).getBitmap();
        Bitmap bmp4 = Bitmap.createScaledBitmap(bmp4_1,600,450,false);
        ByteArrayOutputStream bao4 = new ByteArrayOutputStream();
        bmp4.compress(Bitmap.CompressFormat.PNG, 100, bao4);
        byte [] ba4 = bao4.toByteArray();
        ImageInfo i4 = new ImageInfo();
        MagickImage imgGrad2 = new MagickImage(i4,ba4);

        imgSrc.thresholdImage(31000);

        MagickImage imgTemp = imgSrc.cloneImage(0,0,true);
        // imgTemp.modulateImage("100,0,100");
        MagickImage imgTemp1 = imgTemp.cloneImage(0,0,true);
        imgTemp.compositeImage(CompositeOperator.ScreenCompositeOp, imgGrad1, 0, 0);


        byte[] blobImg = imgTemp.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);

        imgSrc.destroyImages();
        imgTemp.destroyImages();
        imgTemp1.destroyImages();
        imgGrad1.destroyImages();
        imgGrad2.destroyImages();

        return imgOutput;

    }

}
