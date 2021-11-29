package com.minichallenge21;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

public class DBOperations extends SQLiteOpenHelper {
    final Executor executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());
    private static final String DATABASE_NAME = "NoteTaker.db";
    private static final String TABLE_NAME = "NoteTaker";

    public interface Callback {
        void onCompleteRead(String result);
    }

    public DBOperations(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(Title TEXT primary key, Details TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
    }

    public Boolean insertNoteData(String title, String details) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", title);
            contentValues.put("details", details);
            long result = DB.insert(TABLE_NAME, null, contentValues);
            if (result == -1) {
                check.set(false);
            } else {
                check.set(true);
            }
        });
        return check.get();
    }

    public Boolean deleteNoteData(String title) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            Cursor cursor = DB.rawQuery("select * from " + TABLE_NAME + " where Title = ?", new String[]{title});
            if (cursor.getCount() > 0) {
                long result = DB.delete(TABLE_NAME, "Title=?", new String[]{title});
                if (result == -1) {
                    check.set(false);
                } else {
                    check.set(true);
                }
            } else {
                check.set(false);
            }
            cursor.close();
        });
        return check.get();
    }

    public ArrayList<String> getTitles() {
        ArrayList<String> titles = new ArrayList<String>();
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            Cursor cursor = DB.rawQuery("select Title from " + TABLE_NAME, null);
            try {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            String noteTitle = cursor.getString(cursor.getColumnIndex("Title"));
                            titles.add(noteTitle);
                        } while (cursor.moveToNext());
                    }
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            cursor.close();
        });
        return titles;
    }

    public Boolean editNoteTitle(String oldTitle, String newTitle) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", newTitle);
            Cursor cursor = DB.rawQuery("select * from " + TABLE_NAME + " where Title = ?", new String[]{oldTitle});
            if (cursor.getCount() > 0) {
                long result = DB.update(TABLE_NAME, contentValues, "Title=?", new String[]{oldTitle});
                if (result == -1) {
                    check.set(false);
                } else {
                    check.set(true);
                }
            } else {
                check.set(false);
            }
            cursor.close();
        });
        return check.get();
    }

    public AtomicReference<String> getNoteDetails(String title) {
        AtomicReference<String> noteDetails = new AtomicReference<>("");
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from " + TABLE_NAME + " where Title = ?", new String[]{title});
        cursor.moveToFirst();
        do {
            noteDetails.set(cursor.getString(cursor.getColumnIndex("Details")));
        } while (cursor.moveToNext());
        cursor.close();
        System.out.println("thing - " + noteDetails);
        return noteDetails;
    }

    public Boolean editNoteDetails(String title, String details) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("details", details);
            Cursor cursor = DB.rawQuery("select * from " + TABLE_NAME + " where Title = ?", new String[]{title});
            if (cursor.getCount() > 0) {
                long result = DB.update(TABLE_NAME, contentValues, "Title=?", new String[]{title});
                System.out.println(contentValues);
                if (result == -1) {
                    check.set(false);
                } else {
                    check.set(true);
                }
            } else {
                check.set(false);
            }
            cursor.close();
        });
        return check.get();
    }

}
