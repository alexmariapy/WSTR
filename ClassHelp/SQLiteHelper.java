package com.writingstar.autotypingandtextexpansion.ClassHelp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.writingstar.autotypingandtextexpansion.KotlinData.SQLiteHandler;
import com.writingstar.autotypingandtextexpansion.Model.LoadAppModel;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.writingstar.autotypingandtextexpansion.KotlinData.SQLiteHandler.DATABASE_NAME;

public class SQLiteHelper {

    private static final String phrase_ID = "phrase_id";
    SharedPreferences sharedPreferences;
    SQLiteDatabase database;
    SQLiteHandler dbHandler;

    private int lastId = 0;
    Context context;

    public SQLiteHelper(Context context) {
        if (dbHandler == null)
            dbHandler = new SQLiteHandler(context);
        this.context = context;
    }

    public void open() {
        try {
            if (database == null) {
                database = dbHandler.getWritableDatabase();
            }
        }catch (Exception e){
        }
    }

    public void close() {
        if (database != null && database.isOpen())
            dbHandler.close();
    }


    public void phraseInsert(String phrase_title,
                             String phrase_disc,
                             String phrase_modified_time,
                             String phrase_note,
                             int backspace_undo,
                             int smart_case,
                             int append_case,
                             int space_for_expansion,
                             int within_words) {
        ContentValues values = new ContentValues();
        values.put("phrase_title", phrase_title);
        values.put("phrase_disc", phrase_disc);
        values.put("phrase_modified_time", phrase_modified_time);
        values.put("phrase_note", phrase_note);
        values.put("backspace_undo", backspace_undo);
        values.put("smart_case", smart_case);
        values.put("append_case", append_case);
        values.put("space_for_expansion", space_for_expansion);
        values.put("within_words", within_words);

        Log.d("Sql___", "Title:: " + phrase_title + "  Disc:: " + phrase_disc + "  Time:: " + phrase_modified_time);

        database.insert("phrase_detail", null, values);
    }
    public void phraseInsertFromCSV(String phrase_title,
                             String phrase_disc,
                             String phrase_modified_time,
                             String phrase_use_time,
                             int phrase_usage_count,
                             String phrase_note,
                             int backspace_undo,
                             int smart_case,
                             int append_case,
                             int space_for_expansion,
                             int within_words) {
        ContentValues values = new ContentValues();
        values.put("phrase_title", phrase_title);
        values.put("phrase_disc", phrase_disc);
        values.put("phrase_modified_time", phrase_modified_time);
        values.put("phrase_use_time", phrase_use_time);
        values.put("phrase_usage_count", phrase_usage_count);
        values.put("phrase_note", phrase_note);
        values.put("backspace_undo", backspace_undo);
        values.put("smart_case", smart_case);
        values.put("append_case", append_case);
        values.put("space_for_expansion", space_for_expansion);
        values.put("within_words", within_words);

        Log.d("Sql___", "Title:: " + phrase_title + "  Disc:: " + phrase_disc + "  Time:: " + phrase_modified_time);

        database.insert("phrase_detail", null, values);
    }



    public void phraseUpdate(int phrase_id,
                             String phrase_title,
                             String phrase_disc,
                             String phrase_modified_time,
                             String phrase_note,
                             int backspace_undo,
                             int smart_case,
                             int append_case,
                             int space_for_expansion,
                             int within_words) {
        ContentValues values = new ContentValues();

        values.put("phrase_title", phrase_title);
        values.put("phrase_disc", phrase_disc);
        values.put("phrase_modified_time", phrase_modified_time);
        values.put("phrase_note", phrase_note);
        values.put("backspace_undo", backspace_undo);
        values.put("smart_case", smart_case);
        values.put("append_case", append_case);
        values.put("space_for_expansion", space_for_expansion);
        values.put("within_words", within_words);

        database.update("phrase_detail", values, phrase_ID + " = ?", new String[]{String.valueOf(phrase_id)});

    }




    public void lastUseTime(int phrase_id, String phrase_use_time) {
        try{
            ContentValues values = new ContentValues();
            values.put("phrase_use_time", phrase_use_time);
            database.update("phrase_detail", values, phrase_ID + "= ?", new String[]{String.valueOf(phrase_id)});
        }catch (Exception e){}

    }

    public void lastUseCount(int phrase_id, int phrase_usage_count) {
        try{
            ContentValues values = new ContentValues();
            values.put("phrase_usage_count", phrase_usage_count);
            database.update("phrase_detail", values, phrase_ID + "= ?", new String[]{String.valueOf(phrase_id)});
        }catch (Exception e){}
    }

    public boolean isTableExists(String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean dropOldTable(String tableName) {
        Cursor cursor = database.rawQuery("DROP TABLE " + tableName, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void truncateAll() {
        String truncatephrase = "DELETE FROM phrase_detail";
        database.execSQL(truncatephrase);
    }

    public int getLastphrase() {
        lastId = 0;
        String selectQuery = "SELECT phrase_id FROM phrase_detail order by phrase_id DESC";
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.d("CURSUR__", "LOOPP OUT 01 :" + cursor.getColumnCount());
        Log.d("CURSUR__", "LOOPP OUT 02 :" + cursor.getCount());
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(cursor.getColumnIndex("phrase_id"));
            Log.d("CURSUR__", "LOOPP   :" + lastId);
        }
        cursor.close();
        return lastId;
    }

    public ArrayList<TxpGetSet> getAllphraseList() {
        ArrayList<TxpGetSet> phraseList = new ArrayList<TxpGetSet>();
        Cursor cursor;
        try {
            String selectQuery = "SELECT * FROM phrase_detail";
            cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    getDatabyKey(cursor, phraseList);
                } while (cursor.moveToNext());
            }
            Log.e("phraseList::", "" + phraseList.size());
        } catch (SQLiteException e) {
        }
        return phraseList;
    }

    public Cursor getTableCursur() {
        Cursor cursor;
        String selectQuery = "SELECT * FROM phrase_detail";
        cursor = database.rawQuery(selectQuery, null);
        return cursor;
    }

    public ArrayList<TxpGetSet> getSearchItem(String sname) {
        ArrayList<TxpGetSet> phraseList = new ArrayList<TxpGetSet>();
        try {
            String selectQuery = "SELECT * FROM phrase_detail where phrase_title like ? or phrase_disc like ?";
            Cursor cursor = database.rawQuery(selectQuery, new String[]{"%" + sname + "%", "%" + sname + "%"});
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    getDatabyKey(cursor, phraseList);
                } while (cursor.moveToNext());
            }
            Log.e("phraseList::", "" + phraseList.size());
        } catch (SQLiteException e) {
        }
        return phraseList;
    }

    private void getDatabyKey(Cursor cursor, ArrayList<TxpGetSet> phraseList) {
        int phrase_id = cursor.getInt(cursor.getColumnIndex("phrase_id"));
        String phrase_title = cursor.getString(cursor.getColumnIndex("phrase_title"));
        String phrase_disc = cursor.getString(cursor.getColumnIndex("phrase_disc"));
        String phrase_modified_time = cursor.getString(cursor.getColumnIndex("phrase_modified_time"));
        String phrase_note = cursor.getString(cursor.getColumnIndex("phrase_note"));
        String phrase_use_time = cursor.getString(cursor.getColumnIndex("phrase_use_time"));
        int phrase_usage_count = cursor.getInt(cursor.getColumnIndex("phrase_usage_count"));
        int backspace_undo = cursor.getInt(cursor.getColumnIndex("backspace_undo"));
        int smart_case = cursor.getInt(cursor.getColumnIndex("smart_case"));
        int append_case = cursor.getInt(cursor.getColumnIndex("append_case"));
        int space_for_expansion = cursor.getInt(cursor.getColumnIndex("space_for_expansion"));
        int within_words = cursor.getInt(cursor.getColumnIndex("within_words"));

        TxpGetSet phrase = new TxpGetSet(phrase_id, phrase_title, phrase_disc, phrase_modified_time, phrase_note, phrase_use_time, phrase_usage_count, backspace_undo, smart_case, append_case, space_for_expansion, within_words);
        phraseList.add(phrase);
    }

    public void phraseDelete(int value) {
        String deleteQuery = "DELETE FROM phrase_detail WHERE phrase_id='" + value + "'";
        database.execSQL(deleteQuery);
    }

    public void transaction(boolean is) {
        if (is) {
            database.beginTransaction();
        } else {
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    public void enterDataBase(String s){
        database.execSQL(s);
    }

    public void appInsert(String app_package, String app_name) {
        ContentValues values = new ContentValues();
        values.put("app_package", app_package);
        values.put("app_name", app_name);
        Log.d("Sql___app_package", "app_package:: " + app_package);
        database.insert("app_detail", null, values);
    }

    public void appDelete(int value) {
        String deleteQuery = "DELETE FROM app_detail WHERE app_id='" + value + "'";
        database.execSQL(deleteQuery);
    }

    public ArrayList<LoadAppModel> getAppList() {
        ArrayList<LoadAppModel> appList = new ArrayList<LoadAppModel>();
        try {
            String selectQuery = "SELECT * FROM app_detail order by app_name ASC";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int app_id = cursor.getInt(cursor.getColumnIndex("app_id"));
                    String app_package = cursor.getString(cursor.getColumnIndex("app_package"));
                    String app_name = cursor.getString(cursor.getColumnIndex("app_name"));
                    LoadAppModel app = new LoadAppModel(app_id, app_package, app_name);
                    appList.add(app);
                } while (cursor.moveToNext());
            }

            if(cursor != null) cursor.close();
            Log.e("appList::", "" + appList.size());
        } catch (SQLiteException e) {
        }
        return appList;
    }

    public void backup(String outFileName) {
        //database path
        final String inFileName = context.getDatabasePath(DATABASE_NAME).toString();
        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(context, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    public void importDB(String inFileName) {

        final String outFileName = context.getDatabasePath(DATABASE_NAME).toString();

        try {
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(context, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
