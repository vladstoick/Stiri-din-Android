package com.vladstoick.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vladstoick.DataModel.NewsGroup;
import com.vladstoick.DataModel.NewsItem;
import com.vladstoick.DataModel.NewsSource;

import java.util.ArrayList;

/**
 * Created by Vlad on 8/1/13.
 */
public class SqlHelper extends SQLiteOpenHelper{
    private static int DBVERSION = 1;
    private static String DB_NAME = "feeds.db";
    public static String GROUPS_TABLE = "groups";
    public static String SOURCES_TABLE = "sources";
    public static String NEWSITEMS_TABLE = "newsitems";
    public static String COLUMN_GROUP_ID = "groupid";
    public static String COLUMN_SOURCE_ID = "sourceid";
    public static String COLUMN_NOFEEDS = "nofeeds";
    public static String COLUMN_NOUNREADNEWS = "nounreadnews";
    public static String COLUMN_ID = "id";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_URL = "url";
    public static String COLUMN_DESCRIPTION = "description";
    private static String CREATE_NEWSITEMS_TABLE = "CREATE TABLE " + NEWSITEMS_TABLE + " ( "+
            COLUMN_URL  + " text primary key , " + COLUMN_TITLE  + " text not null , " +
            COLUMN_DESCRIPTION +  " text not null , "+COLUMN_SOURCE_ID +" int )";
    private static String CREATE_GROUPS_TABLE = "CREATE TABLE " + GROUPS_TABLE + " ( " +
            COLUMN_ID + " int primary key , " + COLUMN_TITLE + " text not null , "+
            COLUMN_NOFEEDS + " int )";
    private static String CREATE_SOURCES_TABLE = "CREATE TABLE " + SOURCES_TABLE + " ( " +
            COLUMN_ID + " int primary key , " + COLUMN_TITLE + " text not null , "
            + COLUMN_DESCRIPTION + " text not null ," + COLUMN_URL + " text not null , "+
              COLUMN_GROUP_ID+ " int , "+COLUMN_NOUNREADNEWS  +" int ) ";
    public static String[] GROUPS_COLUMNS = {COLUMN_ID,COLUMN_TITLE,COLUMN_NOFEEDS};
    public static String[] SOURCES_COLUMNS = {COLUMN_ID,COLUMN_TITLE,COLUMN_DESCRIPTION,
            COLUMN_URL, COLUMN_GROUP_ID , COLUMN_NOUNREADNEWS};
    public static  String[] NEWSITEMS_COLUMNS = {COLUMN_URL, COLUMN_TITLE , COLUMN_DESCRIPTION,
                 COLUMN_SOURCE_ID};
    public SqlHelper(Context context) {
        super(context, DB_NAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_SOURCES_TABLE);
        db.execSQL(CREATE_NEWSITEMS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    //ACCESSING DATA
    public ArrayList<NewsGroup> getAllNewsGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.GROUPS_TABLE,SqlHelper.GROUPS_COLUMNS,
                null, null, null , null , null , null );
        cursor.moveToFirst();
        ArrayList<NewsGroup> newsGroups = new ArrayList<NewsGroup>();
        while(!cursor.isAfterLast())
        {
            newsGroups.add(new NewsGroup(cursor));
            cursor.moveToNext();
        }
        return newsGroups;
    }
    public NewsSource getNewsSource(int sourceId)
    {
        NewsSource ns;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                SqlHelper.COLUMN_ID +" = " + sourceId, null , null , null , null , null);
        cursor.moveToFirst();
        ns = new NewsSource(cursor);
        cursor = db.query(SqlHelper.NEWSITEMS_TABLE, SqlHelper.NEWSITEMS_COLUMNS,
                SqlHelper.COLUMN_SOURCE_ID +" = "+ sourceId, null , null , null , null , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            ns.news.add(new NewsItem(cursor));
            cursor.moveToNext();
        }
        return ns;
    }
    public NewsGroup getNewsGroup(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.GROUPS_TABLE, SqlHelper.GROUPS_COLUMNS,
                SqlHelper.COLUMN_ID +" = " + groupId, null , null , null , null , null);
        cursor.moveToFirst();
        NewsGroup ng = new NewsGroup(cursor);
        ng.newsSources = new ArrayList<NewsSource>();
        cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                SqlHelper.COLUMN_GROUP_ID +" = "+ groupId, null , null , null , null , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            ng.newsSources.add(new NewsSource(cursor));
            cursor.moveToNext();
        }
        return ng;
    }
    //SQLITE Helper
    public void insertNewsGroupInDb(NewsGroup ng)
    {
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_TITLE,ng.getTitle());
        values.put(SqlHelper.COLUMN_ID,ng.getId());
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.GROUPS_TABLE,null,values,
                SQLiteDatabase.CONFLICT_REPLACE);
        SQLiteDatabase sqlLiteDatabaseReadable = this.getReadableDatabase();
        ng = getNewsGroup(ng.getId());
        values.put(SqlHelper.COLUMN_NOFEEDS,ng.newsSources.size());
    }
    public void insertNewsItemsInDb(NewsSource ns)
    {
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        for(int i=0 ; i < ns.news.size() ; i++)
        {
            NewsItem ni = ns.news.get(i);
            ContentValues values = new ContentValues();
            values.put(SqlHelper.COLUMN_URL,ni.getUrlLink());
            values.put(SqlHelper.COLUMN_TITLE,ni.getTitle());
            values.put(SqlHelper.COLUMN_DESCRIPTION,ni.getDescription());
            values.put(SqlHelper.COLUMN_SOURCE_ID,ns.getId());
            sqlLiteDatabase.insertWithOnConflict(SqlHelper.NEWSITEMS_TABLE, null , values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
    }
    public void insertNewsSourceInDb(NewsSource ns){
        ContentValues values = new ContentValues();
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        values.put(SqlHelper.COLUMN_TITLE,ns.getTitle());
        values.put(SqlHelper.COLUMN_ID,ns.getId());
        values.put(SqlHelper.COLUMN_DESCRIPTION,ns.getDescription());
        values.put(SqlHelper.COLUMN_URL,ns.getRssLink());
        values.put(SqlHelper.COLUMN_GROUP_ID,ns.getGroupId());
        values.put(SqlHelper.COLUMN_NOUNREADNEWS,ns.getNumberOfUnreadNews());
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.SOURCES_TABLE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void updateNewsItem(String url, String paperized){
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_DESCRIPTION,url);
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        sqlLiteDatabase.update(NEWSITEMS_TABLE,values, COLUMN_URL + " = " + url, null);
    }
    public void deleteAllNewsGroupsAndNewsSources()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(GROUPS_TABLE,null,null);
        sqLiteDatabase.delete(SOURCES_TABLE,null,null);
    }
    public void updateNewsGroup(int groupId){
        NewsGroup ng = getNewsGroup(groupId);
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_NOFEEDS,ng.newsSources.size());
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        sqlLiteDatabase.update(GROUPS_TABLE ,values, COLUMN_ID + " = " + groupId  , null);
    }
}
