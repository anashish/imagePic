package com.apptown.Picazzy.util;

import com.apptown.Picazzy.model.EventInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ${="Ashish"} on 15/1/17.
 */

public interface ApiInterface {

    @GET("picazzy.html")
    Call<EventInfo> getEventInfo();
}
