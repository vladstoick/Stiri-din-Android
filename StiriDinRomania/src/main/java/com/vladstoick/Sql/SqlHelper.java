package com.vladstoick.Sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vlad on 8/1/13.
 */
public class SqlHelper extends SQLiteOpenHelper{
    private static int DBVERSION = 1;
    private static String DB_NAME = "feeds.db";
    public static String GROUPS_TABLE = "groups";
    public static String SOURCES_TABLE = "sources";
    public static String COLUMN_ID = "id";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_URL = "url";
    public static String COLUMN_DESCRIPTION = "description";
    private static String CREATE_GROUPS_TABLE = "CREATE TABLE " + GROUPS_TABLE + " ( " +
            COLUMN_ID + " int primary key , " + COLUMN_TITLE + " text not null "+")";
    private static String CREATE_SOURCES_TABLE = "CREATE TABLE " + SOURCES_TABLE + " ( " +
            COLUMN_ID + " int primary key , " + COLUMN_TITLE + " text not null , "
            + COLUMN_DESCRIPTION + " text not null ," + COLUMN_URL + " text not null )";
    public SqlHelper(Context context) {
        super(context, DB_NAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_SOURCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}