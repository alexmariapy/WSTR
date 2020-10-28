package com.writingstar.autotypingandtextexpansion.KotlinData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "writingstar.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Sql___", "   Create database 1");

        db.execSQL("create table phrase_detail (" +
                "phrase_id integer primary key autoincrement," +
                "phrase_title text," +
                "phrase_disc text," +
                "phrase_modified_time text default 'Unknown',"+
                "phrase_use_time text default 'Not used yet',"+
                "phrase_usage_count integer default 0,"+
                "phrase_note text,"+
                "backspace_undo integer default 0," +
                "smart_case integer default 0," +
                "append_case integer default 0," +
                "space_for_expansion integer default 0," +
                "within_words integer default 0)");

        db.execSQL("create table app_detail (" +
                "app_id integer primary key autoincrement," +
                "app_name text,"+
                "app_package text)");

     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("onUpgrade", "oldVersion" + oldVersion + ", newVersion" + newVersion);
    }
}