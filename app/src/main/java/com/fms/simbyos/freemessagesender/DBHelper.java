package com.fms.simbyos.freemessagesender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "contactsdb", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table favcont ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "phone text" + ");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}