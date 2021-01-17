package com.example.tasteit_alpha.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreferencesUtils {

    public String getFileName() {
        return fileN;
    }

    public void setFileName(String fileName) {
        fileN = fileName;
    }


    private final SharedPreferences sharedPref;


    private SharedPreferencesUtils(Activity activity, String fileName) {
        sharedPref=activity.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        fileN=fileName;
    }

    private SharedPreferencesUtils(Activity activity, int resID){
        this(activity,activity.getString(resID));
    }

    private static String fileN;
    private static SharedPreferencesUtils sharedInstance;

    public synchronized static SharedPreferencesUtils getSharedInstance(Activity activity, String fileName){
        if (sharedInstance == null){
            sharedInstance = new SharedPreferencesUtils(activity,fileName);
        }
        return sharedInstance;
    }

    public synchronized static SharedPreferencesUtils getSharedInstance(Activity activity){
        if (sharedInstance == null){
            sharedInstance = new SharedPreferencesUtils(activity,fileN);
        }
        return sharedInstance;
    }

    public synchronized static SharedPreferencesUtils getSharedInstance(Activity activity, int resID){
        if (sharedInstance == null){
            sharedInstance = new SharedPreferencesUtils(activity,resID);
        }
        return sharedInstance;
    }

    public void putString(String tag, String s){
        sharedPref.edit().putString(tag,s).apply();
    }

    public void putInt(String tag, int s){
        sharedPref.edit().putInt(tag,s).apply();
    }

    public String getString(String key,String defVal){
        return sharedPref.getString(key,defVal);
    }

    public int getInt(String key , int defVal){
        return sharedPref.getInt(key,defVal);
    }

    public Map<String,?>getAll(){
        return sharedPref.getAll();
    }

    public String checkIfExists(String key,String defVal){
        return sharedPref.getString(key,defVal);
    }

    public boolean removeIfExists(String key){
        if(checkIfExists(key,"null").equals("null"))return false;
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.remove(key).apply();
        return true;
    }


    /*
     * you can add more implemetation to the calss such as putboolean and more
     */


}
