package com.vladstoick.DataModel;

import android.content.ContentValues;
import android.content.Context;
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
import com.vladstoick.OttoBus.NewsSourceFeedLoaded;
import com.vladstoick.sql.SqlHelper;
import com.vladstoick.stiridinromania.StiriApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
                for(int i = 0 ;i < allNewsGroups.size(); i++ ) {
                    NewsGroup ng = allNewsGroups.get(i);
                    insertNewsGroupInDb(ng);
                    for(int j =0 ; j < ng.newsSources.size() ; j++ ){
                        NewsSource ns = ng.newsSources.get(j);
                    }

                }
                BusProvider.getInstance().post(new DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE,
                        allNewsGroups));
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        StiriApp.queue.add(request);

    }
    //PARCELABLE

    @Override
    public int describeContents() {
        return 0;
    }
    @Subscribe public void OnDataLoaded(DataLoadedEvent event)
    {
        if(event.dataLoadedType == DataLoadedEvent.TAG_NEWSDATASOURCE)
        {
            for(int i=0;i<allNewsGroups.size();i++)
            {
                NewsGroup ng = allNewsGroups.get(i);
                for(int j=0;j<ng.newsSources.size();j++)
                {
                    NewsSource ns = ng.newsSources.get(j);
                    String url = NewsSource.BASE_URL+ ns.getRssLink()+"&feedId="+ns.getId();
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
                        allNewsGroups.get(i).newsSources.get(j).news=newsItems;
                        allNewsGroups.get(i).newsSources.get(j).setNumberOfUnreadNews
                                (newsItems.size());
                        insertNewsSourceInDb(allNewsGroups.get(i).newsSources.get(j));
                    }

        }
        catch(Exception e)
        {
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
        while(!cursor.isAfterLast())
        {
            NewsSource ns = new NewsSource(cursor);
            //TODO REMOVE
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
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.GROUPS_TABLE,null,values,
                SQLiteDatabase.CONFLICT_REPLACE);
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
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.SOURCES_TABLE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }
}
