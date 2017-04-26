
package com.apptown.Picazzy.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class EventInfo implements Parcelable {

    @SerializedName("picazzyEvent")
    private List<PicazzyEvent> mPicazzyEvent;

    protected EventInfo(Parcel in) {
        mPicazzyEvent = in.createTypedArrayList(PicazzyEvent.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mPicazzyEvent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventInfo> CREATOR = new Creator<EventInfo>() {
        @Override
        public EventInfo createFromParcel(Parcel in) {
            return new EventInfo(in);
        }

        @Override
        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };

    public List<PicazzyEvent> getPicazzyEvent() {
        return mPicazzyEvent;
    }

    public void setPicazzyEvent(List<PicazzyEvent> picazzyEvent) {
        mPicazzyEvent = picazzyEvent;
    }

}
