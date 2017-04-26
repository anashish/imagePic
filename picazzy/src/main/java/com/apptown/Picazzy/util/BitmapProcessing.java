package com.apptown.Picazzy.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.ExifInterface;

import com.apptown.Picazzy.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import magick.CompositeOperator;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

public class BitmapProcessing {

    // [-360, +360] -> Default = 0
    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

  /*  public static Bitmap emboss(Bitmap src) {
        double[][] EmbossConfig = new double[][]{
                {-1, 0, -1},
                {0, 4, 0},
                {-1, 0, -1}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(EmbossConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 127;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }*/

    // [0, 150], default => 100
    public static Bitmap cfilter(Bitmap src, double red, double green, double blue) {
        red = (double) red / 100;
        green = (double) green / 100;
        blue = (double) blue / 100;

        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // apply filtering on each channel R, G, B
                A = Color.alpha(pixel);
                R = (int) (Color.red(pixel) * red);
                G = (int) (Color.green(pixel) * green);
                B = (int) (Color.blue(pixel) * blue);
                // set new color pixel to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        // return final image
        return bmOut;
    }

   /* public static Bitmap gaussian(Bitmap src) {
        double[][] GaussianBlurConfig = new double[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(GaussianBlurConfig);
        convMatrix.Factor = 16;
        convMatrix.Offset = 0;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }*/

    // bitOffset = (16, 32, 64, 128)
    public static Bitmap cdepth(Bitmap src, int bitOffset) {
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // round-off color offset
                R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
                if (R < 0) {
                    R = 0;
                }
                G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
                if (G < 0) {
                    G = 0;
                }
                B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
                if (B < 0) {
                    B = 0;
                }

                // set pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        return bmOut;
    }

   /* public static Bitmap sharpen(Bitmap src) {
        double[][] SharpConfig = new double[][]{
                {0, -2, 0},
                {-2, 11, -2},
                {0, -2, 0}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(SharpConfig);
        convMatrix.Factor = 3;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }*/

    public static Bitmap noise(Bitmap source) {
        final int COLOR_MAX = 0xFF;

        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

        source.recycle();
        source = null;

        return bmOut;
    }

    // [-255, +255] -> Default = 0
    public static Bitmap brightness(Bitmap src, int value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if (R > 255) {
                    R = 255;
                } else if (R < 0) {
                    R = 0;
                }

                G += value;
                if (G > 255) {
                    G = 255;
                } else if (G < 0) {
                    G = 0;
                }

                B += value;
                if (B > 255) {
                    B = 255;
                } else if (B < 0) {
                    B = 0;
                }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        /*src.recycle();*/
        src = null;

        return bmOut;
    }

    public static Bitmap sepia(Bitmap src) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // constant grayscale
        final double GS_RED = 0.3;
        final double GS_GREEN = 0.59;
        final double GS_BLUE = 0.11;
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // get color on each channel
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // apply grayscale sample
                B = G = R = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);

                // apply intensity level for sepid-toning on each channel
                R += 110;
                if (R > 255) {
                    R = 255;
                }

                G += 65;
                if (G > 255) {
                    G = 255;
                }

                B += 20;
                if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        return bmOut;
    }

    // red, green, blue [0, 48]
    public static Bitmap gamma(Bitmap src, double red, double green, double blue) {
        red = (double) (red + 2) / 10.0;
        green = (double) (green + 2) / 10.0;
        blue = (double) (blue + 2) / 10.0;
        // create output image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
        // color information
        int A, R, G, B;
        int pixel;
        // constant value curve
        final int MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        // gamma arrays
        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        // setting values for every gamma channels
        for (int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
            gammaG[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
            gammaB[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
        }

        // apply gamma table
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // look up gamma
                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];
                // set new color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        // return final image
        return bmOut;
    }

    // [-100, +100] -> Default = 0
    public static Bitmap contrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
       // src.recycle();
        src = null;
        return bmOut;
    }

    // [0, 200] -> Default = 100
    public static Bitmap saturation(Bitmap src, int value) {
        float f_value = (float) (value / 100.0);

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult =
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(f_value);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        src.recycle();
        src = null;

        return bitmapResult;
    }

    public static Bitmap grayscale(Bitmap src) {
        //Array to generate Gray-Scale image
        float[] GrayArray = {
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        };

        ColorMatrix colorMatrixGray = new ColorMatrix(GrayArray);

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult = Bitmap
                .createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrixGray);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        src.recycle();
        src = null;

        return bitmapResult;
    }

    public static Bitmap vignette(Context context,Bitmap inputImage, int value) {

        Bitmap drawableBitmap = inputImage.copy(Bitmap.Config.ARGB_8888, true);


        Bitmap vignette = BitmapFactory.decodeResource(context.getResources(), R.drawable.vignetteshade);
        vignette = Bitmap.createScaledBitmap(vignette, inputImage.getWidth(), inputImage.getHeight(), true);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(value);
        Canvas comboImage = new Canvas(drawableBitmap);
        comboImage.drawBitmap(vignette, 0f, 0f, paint);

        return drawableBitmap;
    }

    // hue = [0, 360] -> Default = 0
    public static Bitmap hue(Bitmap bitmap, float hue) {
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        final int width = newBitmap.getWidth();
        final int height = newBitmap.getHeight();
        float[] hsv = new float[3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = newBitmap.getPixel(x, y);
                Color.colorToHSV(pixel, hsv);
                hsv[0] = hue;
                newBitmap.setPixel(x, y, Color.HSVToColor(Color.alpha(pixel), hsv));
            }
        }

        bitmap.recycle();
        bitmap = null;

        return newBitmap;
    }

    public static Bitmap tint(Bitmap src, int color) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        Paint p = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas c = new Canvas();
        c.setBitmap(bmOut);
        c.drawBitmap(src, 0, 0, p);

        src.recycle();
        src = null;

        return bmOut;
    }

    public static Bitmap invert(Bitmap src) {
        Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        int A, R, G, B;
        int pixelColor;
        int height = src.getHeight();
        int width = src.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelColor = src.getPixel(x, y);
                A = Color.alpha(pixelColor);

                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);

                output.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        return output;
    }

    // percent = [0, 150], type = (1, 2, 3) => (R, G, B)
    public static Bitmap boost(Bitmap src, int type, float percent) {
        percent = (float) percent / 100;

        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                if (type == 1) {
                    R = (int) (R * (1 + percent));
                    if (R > 255) R = 255;
                } else if (type == 2) {
                    G = (int) (G * (1 + percent));
                    if (G > 255) G = 255;
                } else if (type == 3) {
                    B = (int) (B * (1 + percent));
                    if (B > 255) B = 255;
                }
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        return bmOut;
    }

    public static final Bitmap sketch(Bitmap src) {
        int type = 6;
        int threshold = 130;

        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[3][3];
        for (int y = 0; y < height - 2; ++y) {
            for (int x = 0; x < width - 2; ++x) {
                //      get pixel matrix
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        pixels[i][j] = src.getPixel(x + i, y + j);
                    }
                }
                // get alpha of center pixel
                A = Color.alpha(pixels[1][1]);
                // init color sum
                sumR = sumG = sumB = 0;
                sumR = (type * Color.red(pixels[1][1])) - Color.red(pixels[0][0]) - Color.red(pixels[0][2]) - Color.red(pixels[2][0]) - Color.red(pixels[2][2]);
                sumG = (type * Color.green(pixels[1][1])) - Color.green(pixels[0][0]) - Color.green(pixels[0][2]) - Color.green(pixels[2][0]) - Color.green(pixels[2][2]);
                sumB = (type * Color.blue(pixels[1][1])) - Color.blue(pixels[0][0]) - Color.blue(pixels[0][2]) - Color.blue(pixels[2][0]) - Color.blue(pixels[2][2]);
                // get final Red
                R = (int) (sumR + threshold);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }
                // get final Green
                G = (int) (sumG + threshold);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }
                // get final Blue
                B = (int) (sumB + threshold);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }

        src.recycle();
        src = null;

        return result;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_url) throws IOException {
        ExifInterface ei = new ExifInterface(image_url);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap changeBitmapBrightness(Bitmap bmp, float value)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1, 0, 0, 0, value,
                        0, 1, 0, 0, value,
                        0, 0, 1, 0, value,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        RectF drawRect=new RectF();
        drawRect.set(0,0,bmp.getWidth(),bmp.getHeight());
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, null, drawRect, paint);

        return ret;
    }

    public static Bitmap changeBitmapContrast(Bitmap bmp, float contrast)
    {
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        scale, 0, 0, 0, translate,
                        0, scale, 0, 0, translate,
                        0, 0, scale, 0, translate,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        RectF drawRect=new RectF();
        drawRect.set(0,0,bmp.getWidth(),bmp.getHeight());

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, null, drawRect, paint);

        return ret;
    }

    public static Bitmap changeBitmapSaturation(Bitmap bmp, float value)
    {
        float x = 1 + ((value > 0) ? 3 * value / 100 : value / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        RectF drawRect=new RectF();
        drawRect.set(0,0,bmp.getWidth(),bmp.getHeight());

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, null, drawRect, paint);

        return ret;
    }

    public static Bitmap changeBitmapExposure(Bitmap bmp, float value)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        value, 0, 0, 0, 0,
                        0, value, 0, 0, 0,
                        0, 0, value, 0, 0,
                        0, 0, 0, 1, 0,
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        RectF drawRect=new RectF();
        drawRect.set(0,0,bmp.getWidth(),bmp.getHeight());

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, null, drawRect, paint);

        return ret;
    }

    public static Bitmap doHighlightImage(Bitmap src,int value) {
        // create new bitmap, which will be painted and becomes result image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 96, src.getHeight() + 96, Bitmap.Config.ARGB_8888);
        // setup canvas for painting
        Canvas canvas = new Canvas(bmOut);
        // setup default color
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // create a blur paint for capturing alpha
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];

        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);

        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);

        canvas.drawBitmap(src, 0, 0, null);

        // return out final image
        return bmOut;
    }

    public static Bitmap sharpen(Bitmap src, double weight) {
        double[][] SharpConfig = new double[][] {
                { 0 , -2    , 0  },
                { -2, 10, -2 },
                { 0 , -2    , 0  }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(SharpConfig);
        convMatrix.Factor = 2;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public static Bitmap apply_Auto(Bitmap src) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);
        imgSrc.modulateImage("110,100,100");
        imgSrc.contrastImage(true);
        byte[] blobImg = imgSrc.imageToBlob(iSrc);
        Bitmap imgOutput = BitmapFactory.decodeByteArray(blobImg, 0, blobImg.length);
        imgSrc.destroyImages();
        return imgOutput;

    }

    public static Bitmap apply_Event(Bitmap src ,Bitmap eventType) throws MagickException {




        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);

        Bitmap bmp1_1 = eventType;
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
    public static  Bitmap apply_LEvent(Bitmap src,Bitmap eventType) throws MagickException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte [] ba = bao.toByteArray();
        ImageInfo iSrc = new ImageInfo();
        MagickImage imgSrc = new MagickImage(iSrc,ba);
       // Drawable myDrawable1 = getResources().getDrawable(R.drawable.festlandscape);
        Bitmap bmp1_1 = eventType;
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

    public static Bitmap doGamma(Bitmap src, double red, double green, double blue) {
        // create output image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
        // color information
        int A, R, G, B;
        int pixel;
        // constant value curve
        final int    MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int    MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        // gamma arrays
        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        // setting values for every gamma channels
        for(int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int)Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
            gammaG[i] = (int)Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
            gammaB[i] = (int)Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
        }

        // apply gamma table
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // look up gamma
                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];
                // set new color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    public static Bitmap applySmoothEffect(Bitmap src, double value) {
        //create convolution matrix instance
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.setAll(1);
        convMatrix.Matrix[1][1] = value;
        // set weight of factor and offset
        convMatrix.Factor = value;
        convMatrix.Offset = 1;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public  static  Bitmap addShadow(final Bitmap bm  , float size, float dx, float dy) {
        final Bitmap mask = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ALPHA_8);

        final Matrix scaleToFit = new Matrix();
        final RectF src = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        final RectF dst = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        final Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(dx, dy);

        final Canvas maskCanvas = new Canvas(mask);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(bm, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(bm, dropShadow, paint);

        final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);
        final Bitmap ret = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0,  0, paint);
        retCanvas.drawBitmap(bm, scaleToFit, null);
        mask.recycle();
        return ret;
    }
}