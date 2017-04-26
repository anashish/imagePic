package com.apptown.Picazzy.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ${="Ashish"} on 15/1/17.
 */

public class ApiClient {
    public static final String BASE_URL = "https://s3.ap-south-1.amazonaws.com/updateapp/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
