package com.apptown.Picazzy.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ${="Ashish"} on 26/1/17.
 */

public class SessionManager {


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "picazzy";
    private static final String MODE = "mode";
    private static final String EVENT_JSON = "event_json";
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void savePortrait(boolean isChecked){
        editor.putBoolean(MODE, isChecked);
        editor.commit();
    }

    public boolean isPortrait(){
        return pref.getBoolean(MODE, false);
    }
    public void saveEventJson(String json){
        editor.putString(EVENT_JSON, json);
        editor.commit();
    }
    public String getEventJson(){
        return pref.getString(EVENT_JSON,"");
    }
}
