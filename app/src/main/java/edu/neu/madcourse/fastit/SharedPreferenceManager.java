package edu.neu.madcourse.fastit;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceManager {

    private SharedPreferences sharedPref;

    public SharedPreferenceManager(Context context) {
        this.sharedPref = context.getSharedPreferences(context.getString(R.string.app_name),MODE_PRIVATE);
    }

    public String getStringPref(String key) {
        return sharedPref.getString(key, "");
    }

    public int getIntPref(String key) {
        return sharedPref.getInt(key, -1);
    }

    public long getLongPref(String key) {
        return sharedPref.getLong(key, -1);
    }

    public float getFloatPref(String key) {
        return sharedPref.getFloat(key, -1);
    }

    public void setStringPref(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setIntPref(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLongPref(String key, long value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void setFloatPref(String key, float value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void removePref(String key){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }
}
