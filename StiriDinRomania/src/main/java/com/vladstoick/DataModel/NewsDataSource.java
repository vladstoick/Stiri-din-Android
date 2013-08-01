package com.vladstoick.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataLoadedEvent;
import com.vladstoick.Sql.SqlHelper;

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
                for(int i = 0 ;i < allNewsGroups.size(); i++ )
                {
                    NewsGroup ng = allNewsGroups.get(i);
                    ContentValues values = new ContentValues();
                    values.put(SqlHelper.COLUMN_TITLE,ng.getTitle());
                    values.put(SqlHelper.COLUMN_ID,ng.getId());
                    SQLiteDatabase sqlLiteDatabase = sqlHelper.getWritableDatabase();
                    sqlLiteDatabase.insert(SqlHelper.GROUPS_TABLE,null,values);
                    for(int j =0 ; j < ng.newsSources.size() ; j++ )
                    {
                        NewsSource ns = ng.newsSources.get(j);
                        values = new ContentValues();
                        values.put(SqlHelper.COLUMN_TITLE,ns.getTitle());
                        values.put(SqlHelper.COLUMN_ID,ns.getId());
                        values.put(SqlHelper.COLUMN_DESCRIPTION,ns.getDescription());
                        values.put(SqlHelper.COLUMN_URL,ns.getRssLink());
                        sqlLiteDatabase.insert(SqlHelper.SOURCES_TABLE,null,values);
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
        return this.allNewsGroups;
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
                ContentValues values = new ContentValues();
                values.put(SqlHelper.COLUMN_TITLE,ng.getTitle());
                values.put(SqlHelper.COLUMN_ID,ng.getId());
                SQLiteDatabase sqlLiteDatabase = sqlHelper.getWritableDatabase();
                sqlLiteDatabase.insert(SqlHelper.GROUPS_TABLE,null,values);
                BusProvider.getInstance().post(new
                        DataLoadedEvent(DataLoadedEvent.TAG_NEWSDATASOURCE_MODIFIED,
                        allNewsGroups));
            }
        });
    }

    //SQLITE Helper

}
