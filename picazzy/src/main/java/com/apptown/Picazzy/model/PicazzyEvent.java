
package com.apptown.Picazzy.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PicazzyEvent implements Parcelable {

    @SerializedName("image_land")
    @Expose
    private String imageLand;
    @SerializedName("image_potr")
    @Expose
    private String imagePotr;
    @SerializedName("image_thumb")
    @Expose
    private String imageThumb;
    @SerializedName("eventCode")
    @Expose
    private String eventCode;
    @SerializedName("eventName")
    @Expose
    private String eventName;


    private Bitmap imageLandBitmap;
    private Bitmap imagePotrBitmap;

    protected PicazzyEvent(Parcel in) {
        imageLand = in.readString();
        imagePotr = in.readString();
        imageThumb = in.readString();
        eventCode = in.readString();
        eventName = in.readString();
        imageLandBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        imagePotrBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageLand);
        dest.writeString(imagePotr);
        dest.writeString(imageThumb);
        dest.writeString(eventCode);
        dest.writeString(eventName);
        dest.writeParcelable(imageLandBitmap, flags);
        dest.writeParcelable(imagePotrBitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PicazzyEvent> CREATOR = new Creator<PicazzyEvent>() {
        @Override
        public PicazzyEvent createFromParcel(Parcel in) {
            return new PicazzyEvent(in);
        }

        @Override
        public PicazzyEvent[] newArray(int size) {
            return new PicazzyEvent[size];
        }
    };

    public Bitmap getImageLandBitmap() {
        return imageLandBitmap;
    }

    public void setImageLandBitmap(Bitmap imageLandBitmap) {
        this.imageLandBitmap = imageLandBitmap;
    }

    public Bitmap getImagePotrBitmap() {
        return imagePotrBitmap;
    }

    public void setImagePotrBitmap(Bitmap imagePotrBitmap) {
        this.imagePotrBitmap = imagePotrBitmap;
    }


    public String getImageLand() {
        return imageLand;
    }

    public void setImageLand(String imageLand) {
        this.imageLand = imageLand;
    }

    public String getImagePotr() {
        return imagePotr;
    }

    public void setImagePotr(String imagePotr) {
        this.imagePotr = imagePotr;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

}
