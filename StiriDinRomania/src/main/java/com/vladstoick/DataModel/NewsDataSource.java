package com.vladstoick.DataModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.sql.SqlHelper;
import com.vladstoick.stiridinromania.StiriApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vlad on 7/20/13.
 */
public class NewsDataSource implements Parcelable{

    private AsyncHttpClient httpClient;
    private String BASE_URL = "http://stiriromania.eu01.aws.af.cm/user/";
    private ArrayList<NewsGroup> allNewsGroups;
    private int userId;
    private StiriApp app;
    private SqlHelper sqlHelper;
    private Date updateAt;

    //CONSTRUCTORS
    public NewsDataSource(int userId, StiriApp app) {
        this.userId = userId;
        loadDataFromInternet();
        this.app = app;
        sqlHelper = new SqlHelper(this.app);
        BusProvider.getInstance().register(this);

    }

    private void loadDataFromInternet() {
        StringRequest request = new StringRequest(Request.Method.GET,
                BASE_URL+userId,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                allNewsGroups = JSONParsing.parseNewsDataSource(s);
                for(int i = 0 ;i < allNewsGroups.size(); i++ )
                    insertNewsGroupInDb(allNewsGroups.get(i));
                BusProvider.getInstance().post(new DataLoadedEvent(
                        DataLoadedEvent.TAG_NEWSDATASOURCE,allNewsGroups));
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        StiriApp.queue.add(request);

    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Subscribe public void OnDataLoaded(DataLoadedEvent event)
    {
        final Date currentDate =  Calendar.getInstance().getTime();
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final String dateString = fmt.format(currentDate);

        if(event.dataLoadedType == DataLoadedEvent.TAG_NEWSDATASOURCE)
        {
            for(int i=0;i<allNewsGroups.size();i++)
            {
                NewsGroup ng = allNewsGroups.get(i);
                for(int j=0;j<ng.newsSources.size();j++)
                {
                    NewsSource ns = ng.newsSources.get(j);
                    String url = NewsSource.BASE_URL+ ns.getRssLink()+"&feedId="+ns.getId();
//                            +"&date="+dateString;
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            addNewSource(response);
                        }
                    }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    });
                    StiriApp.queue.add(stringRequest);
                }
            }
        }
    }
    public void addNewSource(String response)
    {
        try{
            JSONObject jsonObject = new JSONObject(response);
            String sFeedId = jsonObject.getString("feedId");
            int feedId = Integer.parseInt(sFeedId);
            NewsSource ns;
            JSONArray feedArray = jsonObject.getJSONArray("articles");
            ArrayList<NewsItem> newsItems = JSONParsing.parseFeed(feedArray);
            for(int i=0;i<allNewsGroups.size();i++)
                for(int j=0;j<allNewsGroups.get(i).newsSources.size();j++)
                    if(allNewsGroups.get(i).newsSources.get(j).getId()==feedId)
                    {
                        allNewsGroups.get(i).newsSources.get(j).
                                setNumberOfUnreadNews(newsItems.size());
                        allNewsGroups.get(i).newsSources.get(j).news = newsItems;
                        insertNewsSourceInDb(allNewsGroups.get(i).newsSources.get(j));
                        insertNewsItemsInDb(allNewsGroups.get(i).newsSources.get(j));
                    }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //ACCESSING DATA
    public ArrayList<NewsGroup> getAllNewsGroups() {
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
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
    public NewsSource getNewsSource(int id)
    {
        NewsSource ns;
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                SqlHelper.COLUMN_ID +" = " + id , null , null , null , null , null);
        cursor.moveToFirst();
        ns = new NewsSource(cursor);
        cursor = db.query(SqlHelper.NEWSITEMS_TABLE, SqlHelper.NEWSITEMS_COLUMNS,
                SqlHelper.COLUMN_SOURCE_ID +" = "+id , null , null , null , null , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            ns.news.add(new NewsItem(cursor));
            cursor.moveToNext();
        }
        return ns;
    }
    public NewsGroup getNewsGroup(int id) {
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor cursor = db.query(SqlHelper.GROUPS_TABLE, SqlHelper.GROUPS_COLUMNS,
                SqlHelper.COLUMN_ID +" = " + id , null , null , null , null , null);
        cursor.moveToFirst();
        NewsGroup ng = new NewsGroup(cursor);
        ng.newsSources = new ArrayList<NewsSource>();
        cursor = db.query(SqlHelper.SOURCES_TABLE, SqlHelper.SOURCES_COLUMNS,
                SqlHelper.COLUMN_GROUP_ID +" = "+id , null , null , null , null , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            ng.newsSources.add(new NewsSource(cursor));
            cursor.moveToNext();
        }
        return ng;
    }
    //MODIFYING DATA
    public void deleteNewsGroup(final int id) {
        httpClient = new AsyncHttpClient();
        httpClient.delete(BASE_URL + userId + "/" + id,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
//                        allNewsGroups.remove(position);
                        BusProvider.getInstance().post(new DataLoadedEvent(
                                DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED,
                                allNewsGroups));
                    }
                });

    }

    public void addNewsGroup(final String groupTitle) {
        httpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("title", groupTitle);
        httpClient.post(BASE_URL + userId, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                NewsGroup ng = new NewsGroup(groupTitle, JSONParsing.parseAddNewsGroupResponse(s));
                allNewsGroups.add(ng);
                insertNewsGroupInDb(ng);
                BusProvider.getInstance().post(new
                        DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED,
                        allNewsGroups));
            }
        });
    }

    //SQLITE Helper
    private void insertNewsGroupInDb(NewsGroup ng)
    {
        ContentValues values = new ContentValues();
        values.put(SqlHelper.COLUMN_TITLE,ng.getTitle());
        values.put(SqlHelper.COLUMN_ID,ng.getId());
        values.put(SqlHelper.COLUMN_NOFEEDS,ng.getNoFeeds());
        SQLiteDatabase sqlLiteDatabase = sqlHelper.getWritableDatabase();
        try{
            sqlLiteDatabase.insertWithOnConflict(SqlHelper.GROUPS_TABLE,null,values,
                SQLiteDatabase.CONFLICT_REPLACE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void insertNewsItemsInDb(NewsSource ns)
    {
        SQLiteDatabase sqlLiteDatabase = sqlHelper.getWritableDatabase();
        for(int i=0 ; i < ns.news.size() ; i++)
        {
            NewsItem ni = ns.news.get(i);
            ContentValues values = new ContentValues();
            values.put(SqlHelper.COLUMN_URL,ni.getUrlLink());
            values.put(SqlHelper.COLUMN_TITLE,ni.getTitle());
            values.put(SqlHelper.COLUMN_DESCRIPTION,ni.getDescription());
            values.put(SqlHelper.COLUMN_SOURCE_ID,ns.getId());
            try{
                sqlLiteDatabase.insertWithOnConflict(SqlHelper.NEWSITEMS_TABLE, null , values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void insertNewsSourceInDb(NewsSource ns)
    {

        ContentValues values = new ContentValues();
        SQLiteDatabase sqlLiteDatabase = sqlHelper.getWritableDatabase();
        values.put(SqlHelper.COLUMN_TITLE,ns.getTitle());
        values.put(SqlHelper.COLUMN_ID,ns.getId());
        values.put(SqlHelper.COLUMN_DESCRIPTION,ns.getDescription());
        values.put(SqlHelper.COLUMN_URL,ns.getRssLink());
        values.put(SqlHelper.COLUMN_GROUP_ID,ns.getGroupId());
        values.put(SqlHelper.COLUMN_NOUNREADNEWS,ns.getNumberOfUnreadNews());
        try{
            sqlLiteDatabase.insertWithOnConflict(SqlHelper.SOURCES_TABLE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(allNewsGroups);
        dest.writeInt(userId);
    }
    private NewsDataSource(Parcel in)
    {
        in.readTypedList(allNewsGroups,NewsGroup.CREATOR);
        userId = in.readInt();
    }
    public static final Parcelable.Creator<NewsDataSource> CREATOR
            = new Parcelable.Creator<NewsDataSource>() {
        public NewsDataSource createFromParcel(Parcel in) {
            return new NewsDataSource(in);
        }

        public NewsDataSource[] newArray(int size) {
            return new NewsDataSource[size];
        }
    };
}
