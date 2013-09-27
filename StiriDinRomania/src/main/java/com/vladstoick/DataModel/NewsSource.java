package com.vladstoick.DataModel;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.otto.Subscribe;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.NewsSourceFeedLoaded;
import com.vladstoick.stiridinromania.StiriApp;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlad on 7/19/13.
 */
public class NewsSource implements Parcelable {
    public static String TAG = "NEWSSORUCE";
    public static String BASE_URL = "http://37.139.8.146:3000/?feedId=";
    public static String TAG_RSSLINK = "url";
    private String rssLink;
    public static String TAG_TITLE = "title";
    private String title;
    public ArrayList<NewsItem> news;
    public static String TAG_ID = "id";
    private int id;
    private int groupId;

    public NewsSource(String rssLink, String title, int id) {
        this.rssLink = rssLink;
        this.title = title;
        this.id = id;
        news = new ArrayList<NewsItem>();

    }

    public NewsSource() {
    }

    public NewsSource(Cursor cursor) {

        this.id = cursor.getInt(0);
        this.title = cursor.getString(1);
        this.rssLink = cursor.getString(2);
        this.groupId = cursor.getInt(3);
        news = new ArrayList<NewsItem>();
    }



    public int getNumberOfUnreadNews(StiriApp app) {
        return app.newsDataSource.getNumberOfNewsForNewsSource(getId());
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRssLink() {

        return rssLink;
    }

    public void setRssLink(String rssLink) {
        this.rssLink = rssLink;
    }

    //PARCELABLE
    public static final Parcelable.Creator<NewsSource> CREATOR
            = new Parcelable.Creator<NewsSource>() {
        public NewsSource createFromParcel(Parcel in) {
            return new NewsSource(in);
        }

        public NewsSource[] newArray(int size) {
            return new NewsSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rssLink);
        dest.writeString(this.title);
        dest.writeTypedList(news);
        dest.writeInt(groupId);
        dest.writeInt(id);
    }

    private NewsSource(Parcel in) {
        news = new ArrayList<NewsItem>();
        this.rssLink = in.readString();
        this.title = in.readString();
        in.readTypedList(news, NewsItem.CREATOR);
        this.groupId = in.readInt();
        this.id = in.readInt();
    }

}
