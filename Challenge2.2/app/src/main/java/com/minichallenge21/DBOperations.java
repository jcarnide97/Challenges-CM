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
    private static final String DATABASE_NAME = "Challenge22.db";
    private static final String NOTE_TABLE_NAME = "Notes";
    private static final String TOPIC_TABLE_NAME = "Topics";

    public interface Callback {
        void onCompleteRead(String result);
    }

    public DBOperations(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + NOTE_TABLE_NAME + "(id INTEGER primary key autoincrement not null, Title TEXT, Details TEXT)");
        sqLiteDatabase.execSQL("create table " + TOPIC_TABLE_NAME + "(id INTEGER primary key autoincrement not null, Name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + NOTE_TABLE_NAME);
        sqLiteDatabase.execSQL("drop table if exists " + TOPIC_TABLE_NAME);
    }

    public Boolean insertNoteData(String title, String details) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", title);
            contentValues.put("details", details);
            long result = DB.insert(NOTE_TABLE_NAME, null, contentValues);
            if (result == -1) {
                check.set(false);
            } else {
                check.set(true);
            }
        });
        return check.get();
    }

    public Boolean insertTopic(String name) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(()->{
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            long result = DB.insert(TOPIC_TABLE_NAME, null, contentValues);
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
            Cursor cursor = DB.rawQuery("select * from " + NOTE_TABLE_NAME + " where Title = ?", new String[]{title});
            if (cursor.getCount() > 0) {
                long result = DB.delete(NOTE_TABLE_NAME, "Title=?", new String[]{title});
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

    public Boolean deleteTopic(String name) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(()->{
            SQLiteDatabase DB = this.getWritableDatabase();
            Cursor cursor = DB.rawQuery("select * from " + TOPIC_TABLE_NAME + " where Name = ?", new String[]{name});
            if (cursor.getCount() > 0) {
                long result = DB.delete(TOPIC_TABLE_NAME, "Name=?", new String[]{name});
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
            Cursor cursor = DB.rawQuery("select Title from " + NOTE_TABLE_NAME, null);
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

    public ArrayList<String> getTopics() {
        ArrayList<String> topics = new ArrayList<String>();
        executor.execute(()->{
            SQLiteDatabase DB = this.getWritableDatabase();
            Cursor cursor = DB.rawQuery("select Name from " + TOPIC_TABLE_NAME, null);
            try {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            String topicName = cursor.getString(cursor.getColumnIndex("Name"));
                            topics.add(topicName);
                        } while (cursor.moveToNext());
                    }
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            cursor.close();
        });
        return topics;
    }

    public Boolean editNoteTitle(String oldTitle, String newTitle) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", newTitle);
            Cursor cursor = DB.rawQuery("select * from " + NOTE_TABLE_NAME + " where Title = ?", new String[]{oldTitle});
            if (cursor.getCount() > 0) {
                long result = DB.update(NOTE_TABLE_NAME, contentValues, "Title=?", new String[]{oldTitle});
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

    public AtomicReference<String> getNoteDetails(String title, Callback callback) {
        AtomicReference<String> noteDetails = new AtomicReference<>("");
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from " + NOTE_TABLE_NAME + " where Title = ?", new String[]{title});
        cursor.moveToFirst();
        do {
            noteDetails.set(cursor.getString(cursor.getColumnIndex("Details")));
        } while (cursor.moveToNext());
        cursor.close();
        handler.post(()->{
            callback.onCompleteRead(noteDetails.get());
        });
        return noteDetails;
    }

    public Boolean editNoteDetails(String title, String details) {
        AtomicBoolean check = new AtomicBoolean(false);
        executor.execute(() -> {
            SQLiteDatabase DB = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("details", details);
            Cursor cursor = DB.rawQuery("select * from " + NOTE_TABLE_NAME + " where Title = ?", new String[]{title});
            if (cursor.getCount() > 0) {
                long result = DB.update(NOTE_TABLE_NAME, contentValues, "Title=?", new String[]{title});
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
