package com.example.apps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RateDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "exchange_rates.db";
    private static final int DATABASE_VERSION = 1;
    
    public static final String TABLE_RATES = "rates";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_RATE = "rate";
    
    private static final String CREATE_TABLE = 
            "CREATE TABLE " + TABLE_RATES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_COUNTRY + " TEXT NOT NULL, " +
            COLUMN_RATE + " REAL NOT NULL);";

    public RateDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
        onCreate(db);
    }
}