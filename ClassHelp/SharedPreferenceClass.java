package com.writingstar.autotypingandtextexpansion.ClassHelp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPreferenceClass {

    static SharedPreferences sharedPreferences;



    public static void setString(Context context, String key, String Value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.putString(key, Value);
        sEdit.commit();
    }

    public static String getString(Context context, String key, String defaultValue) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }

    public static int setInteger(Context context, String key, Integer Value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.putInt(key, Value);
        sEdit.commit();
        return 0;
    }

    public static Integer getInteger(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Integer value = sharedPreferences.getInt(key, 0);
        return value;
    }
    public static Long setLong(Context context, String key, Long Value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.putLong(key, Value);
        sEdit.commit();
        return Long.valueOf(0);
    }

    public static Long getLong(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Long value = sharedPreferences.getLong(key, 0);
        return value;
    }

    public static Integer getInteger(Context context, String key, int defaultValue) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Integer value = sharedPreferences.getInt(key, defaultValue);
        return value;
    }

    public static void setBoolean(Context context, String key, Boolean Value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.putBoolean(key, Value);
        sEdit.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, Boolean Value) {
        boolean value = false;
        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            value = sharedPreferences.getBoolean(key, Value);
            return value;
        } catch (Exception e) {
            return value;
        }
    }

    public static void setLong(Context context, String key, long Value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.putLong(key, Value);
        sEdit.commit();
    }

    public static long getLong(Context context, String key, long Value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long value = sharedPreferences.getLong(key, Value);
        return value;
    }

    public static void clearSharedPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.clear();
        sEdit.commit();
    }

}
