package com.vladstoick.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.sql.SqlHelper;

import java.util.ArrayList;

/**
 * Created by vlad on 7/20/13.
 */
public class NewsDataSource implements Parcelable{
    private AsyncHttpClient httpClient;
    private String BASE_URL = "http://stiriromania.eu01.aws.af.cm/user/";
    private ArrayList<NewsGroup> allNewsGroups;
    private int userId;
    private Context context;
    private SqlHelper sqlHelper;

    //CONSTRUCTORS
    public NewsDataSource(int userId, Context context) {
        this.userId = userId;
        loadDataFromInternet();
        this.context = context;
        sqlHelper = new SqlHelper(context);
        BusProvider.getInstance().register(this);
    }

    private void loadDataFromInternet() {
        httpClient = new AsyncHttpClient();
        httpClient.get(BASE_URL + userId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String s) {
                allNewsGroups = JSONParsing.parseNewsDataSource(s);
                for(int i = 0 ;i < allNewsGroups.size(); i++ ) {
                    NewsGroup ng = allNewsGroups.get(i);
                    insertNewsGroupInDb(ng);
                    for(int j =0 ; j < ng.newsSources.size() ; j++ ){
                        NewsSource ns = ng.newsSources.get(j);
                    }
                }
                BusProvider.getInstance().post(new DataLoadedEvent(
                        DataLoadedEvent.TAG_NEWSDATASOURCE,
                        allNewsGroups));

            }
        });

    }
    //PARCELABLE

    @Override
    public int describeContents() {
        return 0;
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

    public NewsGroup getNewsGroup(int position) {
        NewsGroup ng = allNewsGroups.get(position);

        return ng;
    }

    //MODIFYING DATA
    public void deleteNewsGroup(final int position) {
        httpClient = new AsyncHttpClient();
        httpClient.delete(BASE_URL + userId + "/" + getNewsGroup(position).getId(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        allNewsGroups.remove(position);
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
    private void insertNewsSourceInDb(NewsSource ns)
    {
        ContentValues values = new ContentValues();
        SQLiteDatabase sqlLiteDatabase = sqlHelper.getWritableDatabase();
        values.put(SqlHelper.COLUMN_TITLE,ns.getTitle());
        values.put(SqlHelper.COLUMN_ID,ns.getId());
        values.put(SqlHelper.COLUMN_DESCRIPTION,ns.getDescription());
        values.put(SqlHelper.COLUMN_URL,ns.getRssLink());
        values.put(SqlHelper.COLUMN_GROUP_ID,ns.getGroupId());
        sqlLiteDatabase.insertWithOnConflict(SqlHelper.SOURCES_TABLE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }
}
