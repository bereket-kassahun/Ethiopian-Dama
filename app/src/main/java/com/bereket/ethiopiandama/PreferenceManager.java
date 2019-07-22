package com.bereket.ethiopiandama;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;
    Context context;
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String LAST_LANGUAGE = "lastLanguage";
    int MODE = 0;
    private static final String PREFERENCE = "Javapapers";


    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE, MODE);
        spEditor = sharedPreferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        spEditor.putBoolean(FIRST_LAUNCH, isFirstTime);
        spEditor.commit();
    }
    public void setLastLanguage(String language){
        spEditor.putString(LAST_LANGUAGE, language);
        spEditor.commit();
    }

    public boolean FirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }
    public String LastLanguage(){
        return sharedPreferences.getString(LAST_LANGUAGE,"default");
    }
}
