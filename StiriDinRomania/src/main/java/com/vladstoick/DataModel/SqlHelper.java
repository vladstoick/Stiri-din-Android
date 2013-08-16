package com.vladstoick.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vladstoick.DialogFragment.RenameDialogFragment;

import java.util.ArrayList;

/**
 * Created by Vlad on 8/1/13.
 */
public class SqlHelper extends SQLiteOpenHelper {
    private static int DBVERSION = 1;
    private static String DB_NAME = "feeds.db";
    public static String GROUPS_TABLE = "groups";
    public static String SOURCES_TABLE = "sources";
    public static String NEWSITEMS_TABLE = "newsitems";
    public static String COLUMN_DATE = "date";
    public static String COLUMN_GROUP_ID = "groupid";
    public static String COLUMN_SOURCE_ID = "sourceid";
    public static String COLUMN_NOFEEDS = "nofeeds";
    public static String COLUMN_NOUNREADNEWS = "nounreadnews";
    public static String COLUMN_ID = "id";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_URL = "url";
    public static String COLUMN_DESCRIPTION = "description";
    private static String CREATE_NEWSITEMS_TABLE = "CREATE TABLE " + NEWSITEMS_TABLE + " ( " +
            COLUMN_URL + " text primary key , " + COLUMN_TITLE + " text not null , " +
            COLUMN_DESCRIPTION + " text not null , " +  COLUMN_SOURCE_ID + " int , " +
            COLUMN_DATE+ " long )";
    private static String CREATE_GROUPS_TABLE = "CREATE TABLE " + GROUPS_TABLE + " ( " +
            COLUMN_ID + " int primary key , " + COLUMN_TITLE + " text not null , " +
            COLUMN_NOFEEDS + " int )";
    private static String CREATE_SOURCES_TABLE = "CREATE TABLE " + SOURCES_TABLE + " ( " +
            COLUMN_ID + " int primary key , " + COLUMN_TITLE + " text not null , "+ COLUMN_URL
            + " text not null , " +
            COLUMN_GROUP_ID + " int , " + COLUMN_NOUNREADNEWS + " int ) ";
    public static String[] GROUPS_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_NOFEEDS};
    public static String[] SOURCES_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_URL, COLUMN_GROUP_ID,
            COLUMN_NOUNREADNEWS};
    public static String[] NEWSITEMS_COLUMNS = {COLUMN_URL, COLUMN_TITLE, COLUMN_DESCRIPTION,
            COLUMN_SOURCE_ID, COLUMN_DATE};

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

    //NEWSGROUP


    public ArrayList<NewsGroup> getAllNewsGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.GROUPS_TABLE, SqlHelper.GROUPS_COLUMNS,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<NewsGroup> newsGroups = new ArrayList<NewsGroup>();
        while (!cursor.isAfterLast()) {
            newsGroups.add(new NewsGroup(cursor));
            cursor.moveToNext();
        }
        return newsGroups;
    }

    public NewsGroup getNewsGroup(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.GROUPS_TABLE, SqlHelper.GROUPS_COLUMNS,
                SqlHelper.COLUMN_ID + " = " + groupId, null, null, null, null, null);
        cursor.moveToFirst();
        NewsGroup ng = new NewsGroup(cursor);
        ng.newsSources = new ArrayList<NewsSource>();
        cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                SqlHelper.COLUMN_GROUP_ID + " = " + groupId, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ng.newsSources.add(new NewsSource(cursor));
            cursor.moveToNext();
        }
        return ng;
    }

    public void insertNewsGroupInDb(NewsGroup ng) {
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_TITLE, ng.getTitle());
        values.put(SqlHelper.COLUMN_ID, ng.getId());
        values.put(SqlHelper.COLUMN_NOFEEDS, ng.newsSources.size());
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.GROUPS_TABLE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);

    }

    public void renameNewsGroup(RenameDialogFragment.ElementRenamedEvent event){
        NewsGroup ng = getNewsGroup(event.id);
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE,event.newName);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.update(GROUPS_TABLE, values, COLUMN_ID + " = " + event.id, null);
    }

    public void updateNewsGroupNoFeeds(int groupId) {
        NewsGroup ng = getNewsGroup(groupId);
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_NOFEEDS, ng.newsSources.size());
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        sqlLiteDatabase.update(GROUPS_TABLE, values, COLUMN_ID + " = " + groupId, null);
    }

    public void deleteNewsGroup(int groupId){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(GROUPS_TABLE, COLUMN_ID + " = " + groupId , null);
    }

    //NEWSOURCE
    public NewsSource getNewsSource(int sourceId) {
        NewsSource ns;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                SqlHelper.COLUMN_ID + " = " + sourceId, null, null, null, null, null);
        cursor.moveToFirst();
        ns = new NewsSource(cursor);
        ns.news = getNewsItems(ns);
        return ns;
    }

    public ArrayList<NewsSource> getAllNewsSources(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<NewsSource> newsSources = new ArrayList<NewsSource>();
        while (!cursor.isAfterLast()) {
            newsSources.add(new NewsSource(cursor));
            cursor.moveToNext();
        }
        return newsSources;
    }

    public void insertNewsSourceInDb(NewsSource ns) {
        ContentValues values = new ContentValues();
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        values.put(SqlHelper.COLUMN_TITLE, ns.getTitle());
        values.put(SqlHelper.COLUMN_ID, ns.getId());
        values.put(SqlHelper.COLUMN_URL, ns.getRssLink());
        values.put(SqlHelper.COLUMN_GROUP_ID, ns.getGroupId());
        int noUnreadNews = ns.getNumberOfUnreadNews();
        if(getNewsItems(ns).size() != 0 ) noUnreadNews = getNewsItems(ns).size();
        values.put(SqlHelper.COLUMN_NOUNREADNEWS, noUnreadNews);
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.SOURCES_TABLE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void renameNewsSource(RenameDialogFragment.ElementRenamedEvent event){
        NewsSource newsSource = getNewsSource(event.id);
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE,event.newName);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.update(SOURCES_TABLE, values, COLUMN_ID + " = " + event.id, null);
    }

    public void deleteNewsSource(int sourceId){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(SOURCES_TABLE, COLUMN_ID + " = " + sourceId , null);
    }

    //NEWSITEM

    public ArrayList<NewsItem> getNewsItems(NewsSource ns){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(NEWSITEMS_TABLE, NEWSITEMS_COLUMNS,
                SqlHelper.COLUMN_SOURCE_ID + " = " + ns.getId(), null, null, null, COLUMN_DATE

                + " DESC", null);
        ArrayList<NewsItem> news = new ArrayList<NewsItem>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            news.add(new NewsItem(cursor));
            cursor.moveToNext();
        }
        return news;
    }

    public NewsItem getNewsItem(String url){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NEWSITEMS_TABLE, NEWSITEMS_COLUMNS,
                SqlHelper.COLUMN_URL + " = \'" + url +"\'", null, null, null, null, null);
        cursor.moveToFirst();
        NewsItem ni = new NewsItem(cursor);
        return ni;
    }

    public ArrayList<NewsItem> searchNewsItem(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NEWSITEMS_TABLE, NEWSITEMS_COLUMNS,
                SqlHelper.COLUMN_TITLE + " LIKE '%" + query + "%'", null, null, null, null, null);
        ArrayList<NewsItem> results = new ArrayList<NewsItem>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            results.add(new NewsItem(cursor));
            cursor.moveToNext();
        }
        return results;
    }

    public void insertNewsItemsInDb(NewsSource ns) {
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        for (int i = 0; i < ns.news.size(); i++) {
            NewsItem ni = ns.news.get(i);
            ContentValues values = new ContentValues();
            values.put(SqlHelper.COLUMN_URL, ni.getUrlLink());
            values.put(SqlHelper.COLUMN_TITLE, ni.getTitle());
            values.put(SqlHelper.COLUMN_DESCRIPTION, ni.getDescription());
            values.put(SqlHelper.COLUMN_SOURCE_ID, ns.getId());
            values.put(SqlHelper.COLUMN_DATE, ni.getPubDate());
            sqlLiteDatabase.insertWithOnConflict(SqlHelper.NEWSITEMS_TABLE, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        }
    }


    public void updateNewsItem(String url, String paperized) {
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_DESCRIPTION, paperized);
        SQLiteDatabase sqlLiteDatabase = this.getWritableDatabase();
        try{
            sqlLiteDatabase.update(NEWSITEMS_TABLE, values, COLUMN_URL + " =  '" + url+ "'" ,
                    null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //GENERAL

    public void deleteOldNewsGroups(ArrayList<NewsGroup> groups) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String groupsQuery = "(";
        for(int i=0; i<groups.size(); i++){
            groupsQuery = groupsQuery + " " + groups.get(i).getId() + " ,";
        }
        groupsQuery = groupsQuery.substring(0,groupsQuery.length()-1);
        groupsQuery = groupsQuery + " )";
        if(groupsQuery.length()<4){
            groupsQuery = "";
        }
        sqLiteDatabase.delete(GROUPS_TABLE, groupsQuery, null);

    }

}
